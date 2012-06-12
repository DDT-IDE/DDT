package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IScopeNode;

public class DefinitionTypedef extends Definition implements IStatement {
	
	public final Reference type;
	public final Initializer initializer;
	
	public DefinitionTypedef(DefUnitDataTuple dudt, PROT prot, Reference type, Initializer initializer) {
		super(dudt, prot);
		this.type = parentize(type);
		this.initializer = parentize(initializer);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, initializer);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Typedef;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() +" -> "+ type.toStringAsElement() +" - "+ getModuleScope().toStringAsElement();
	}
	
}