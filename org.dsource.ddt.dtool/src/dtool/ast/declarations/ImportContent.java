package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

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
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInStaticImport(this, search);
		if(!getDeclarationImport().isStatic) {
			ReferenceResolver.findDefUnitInContentImport(this, search);
		}
	}
	
	private String[] getPackageNames() {
		return moduleRef.packages.getInternalArray();
	}
	
	public INamedElement getPartialDefUnit(IModuleResolver mr) {
		if(getPackageNames().length == 0 || getPackageNames()[0] == "") {
			return moduleRef.findTargetDefElement(mr);
		}
		
		// Do lazy PartialDefUnit creation
		if(defunit == null) {
			if(moduleRef.isMissingCoreReference()) {
				defunit = null;
			} else {
				INamedElement moduleElem = moduleRef.getModuleProxy(mr);
				defunit = PackageNamespace.createPartialDefUnits(getPackageNames(), moduleElem); 
			}
		}
		return defunit;
	}
	
}