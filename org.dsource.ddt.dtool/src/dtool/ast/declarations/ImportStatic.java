package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.api.IModuleResolver;

// TODO merge this with ImportContent
public abstract class ImportStatic extends ASTNode implements IImportFragment {
	
	public final RefModule moduleRef;
	
	private PartialPackageDefUnit defunit; // Non-Structural Element
	//private String[] names; // Non-Structural Element
	
	public ImportStatic(RefModule refModule) {
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
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
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInStaticImport(this, search);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(moduleRef);
	}
	
}