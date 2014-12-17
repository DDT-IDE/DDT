package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.references.RefModule;

public class ImportContent extends ASTNode implements IImportFragment {
	
	public final RefModule moduleRef;
	private PackageNamespace defunit; // Non-Structural Element
	
	public ImportContent(RefModule refModule) {
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	protected ASTNode getParent_Concrete() {
		assertTrue(parent instanceof DeclarationImport || parent instanceof ImportSelective);
		return parent;
	}
	
	public DeclarationImport getDeclarationImport() {
		ASTNode parent = super.getParent();
		if(parent instanceof DeclarationImport) {
			return (DeclarationImport) parent;
		} else {
			return ((ImportSelective) parent).getDeclarationImport();
		}
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_CONTENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, moduleRef);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(moduleRef);
	}
	
	@Override
	public void doNodeSimpleAnalysis() {
		assertTrue(getDeclarationImport() != null);
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
	}
	
	private String[] getPackageNames() {
		return moduleRef.packages.getInternalArray();
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public void evaluateImportsScopeContribution(ScopeNameResolution scopeRes) {
		findDefUnitInStaticImport(this, scopeRes);
		if(!getDeclarationImport().isStatic) {
			findDefUnitInContentImport(this, scopeRes);
		}
	}
	
	public static void findDefUnitInStaticImport(ImportContent importStatic, ScopeNameResolution scopeRes) {
		INamedElement namedElement = importStatic.getPartialDefUnit(scopeRes.getContext());
		scopeRes.evaluateNamedElementForSearch(namedElement, true);
	}
	
	public INamedElement getPartialDefUnit(ISemanticContext mr) {
		if(getPackageNames().length == 0 || getPackageNames()[0] == "") {
			return moduleRef.resolveTargetElement(mr);
		}
		
		// Do lazy PartialDefUnit creation
		if(defunit == null) {
			if(moduleRef.isMissingCoreReference()) {
				defunit = null;
			} else {
				INamedElement moduleElem = moduleRef.getModuleProxy(mr);
				defunit = PackageNamespace.createPartialDefUnits(getPackageNames(), moduleElem, ImportContent.this); 
			}
		}
		return defunit;
	}
	
	public static void findDefUnitInContentImport(ImportContent impContent, ScopeNameResolution scopeRes) {
		findDefUnitInStaticImport(impContent, scopeRes);
		//if(search.isScopeFinished()) return;
		
		INamedElement targetModule = findImportTargetModule(scopeRes.getContext(), impContent);
		scopeRes.getLookup().evaluateInMembersScope(targetModule);
	}
	
	public static INamedElement findImportTargetModule(ISemanticContext modResolver, IImportFragment impSelective) {
		String[] packages = impSelective.getModuleRef().packages.getInternalArray();
		String moduleName = impSelective.getModuleRef().module;
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, moduleName));
		return CommonScopeLookup.resolveModule(modResolver, impSelective, moduleFullName);
	}
	
}