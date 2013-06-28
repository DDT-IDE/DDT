package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(moduleRef);
	}
	
	@Override
	protected void checkNewParent() {
		assertTrue(parent instanceof DeclarationImport || parent instanceof ImportSelective);
	}
	
	@Override
	public void doNodeAnalysis() {
		assertTrue(getDeclarationImport() != null);
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