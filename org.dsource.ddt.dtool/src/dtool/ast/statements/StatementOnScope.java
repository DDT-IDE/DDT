package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class StatementOnScope extends Statement {
	
	public enum EventType {
		ON_EXIT,
		ON_SUCCESS,
		ON_FAILURE
	}
	
	public final IStatement st;
	public final EventType eventType;
	
	public StatementOnScope(EventType eventType, IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.eventType = eventType;
		this.st = parentizeI(st);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}
	
}