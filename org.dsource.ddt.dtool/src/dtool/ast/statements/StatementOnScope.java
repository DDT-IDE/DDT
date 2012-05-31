package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.OnScopeStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class StatementOnScope extends Statement {
	
	public enum EventType {
		ON_EXIT,
		ON_SUCCESS,
		ON_FAILURE
	}
	
	public IStatement st;
	public EventType eventType;

	public StatementOnScope(OnScopeStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.st = Statement.convert(elem.statement, convContext);
	}
	
	public StatementOnScope(EventType eventType, IStatement st) {
		this.eventType = eventType;
		this.st = st;
		
		if (this.st != null)
			((ASTNeoNode) this.st).setParent(this);
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
