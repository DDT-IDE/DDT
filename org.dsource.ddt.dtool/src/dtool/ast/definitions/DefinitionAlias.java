package dtool.ast.definitions;


import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition implements IStatement {
	
	public final Reference target;
	
	public DefinitionAlias(DefUnitDataTuple dudt, PROT prot, Reference target) {
		super(dudt, prot);
		this.target = parentize(target);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, target);
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return target.getTargetScope(moduleResolver);
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " -> " + target.toStringAsElement();
	}
	
}