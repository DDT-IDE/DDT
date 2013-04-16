package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class ImportAlias extends DefUnit implements IImportFragment {
		
	public final RefModule moduleRef;
	
	public ImportAlias(DefUnitTuple dudt, RefModule refModule) {
		super(dudt);
		this.moduleRef = parentize(refModule);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IMPORT_ALIAS;
	}
	
	@Override
	public RefModule getModuleRef() {
		return moduleRef;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias; // Maybe should be ImportAlias
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		// Do nothing. Aliasing imports do not contribute secondary-space DefUnits
		// TODO: this is a bug in D, it's not according to spec.
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return moduleRef.getTargetScope(moduleResolver);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendStrings(getName(), " = ");
		cp.appendNode(moduleRef);
	}
	
}