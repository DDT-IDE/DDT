package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.pluginadapters.IModuleResolver;

// TODO merge this with ImportContent
public class ImportStatic extends ASTNeoNode implements IImportFragment {
	
	public final RefModule moduleRef;
	
	private PartialPackageDefUnit defunit; // Non-Structural Element
	//private String[] names; // Non-Structural Element
	
	public ImportStatic(RefModule refModule, SourceRange sourceRange) {
		initSourceRange(sourceRange);
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
	public void accept0(IASTNeoVisitor visitor) {
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
		cp.appendNode(moduleRef);
	}
	
}