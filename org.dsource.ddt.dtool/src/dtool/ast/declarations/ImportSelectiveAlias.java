package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefImportSelection;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class ImportSelectiveAlias extends DefUnit implements IImportSelectiveSelection {
	
	public final RefImportSelection target;
	
	public ImportSelectiveAlias(DefUnitTuple dudt, RefImportSelection impSelection, SourceRange sourceRange) {
		super(dudt);
		this.target = parentize(impSelection);
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, target);
		}
		visitor.endVisit(this);		
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return target.getTargetScope(moduleResolver);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(getName(), " = ");
		cp.appendNode(target);
	}
	
}