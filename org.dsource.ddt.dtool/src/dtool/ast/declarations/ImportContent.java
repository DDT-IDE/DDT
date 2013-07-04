package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

public class ImportContent extends ASTNode implements IImportFragment {
	
	public final RefModule moduleRef;
	private PartialPackageDefUnit defunit; // Non-Structural Element
	
	public ImportContent(RefModule refModule) {
		this.moduleRef = parentize(refModule);
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
	protected void checkNewParent() {
		assertTrue(parent instanceof DeclarationImport || parent instanceof ImportSelective);
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
	
	public DefUnit getPartialDefUnit(IModuleResolver moduleResolver) {
		if(getPackageNames().length == 0 || getPackageNames()[0] == "") {
			return moduleRef.findTargetDefUnit(moduleResolver);
		}
		
		// Do lazy PartialDefUnit creation
		if(defunit == null) {
			defunit = PartialPackageDefUnit.createPartialDefUnits(getPackageNames(), moduleRef, null); 
		}
		return defunit;
	}
	
}