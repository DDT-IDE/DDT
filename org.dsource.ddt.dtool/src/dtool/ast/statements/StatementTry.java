package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode implements IScopeNode {
		
		public final IFunctionParameter param;
		public final IStatement body;

		public CatchClause(IFunctionParameter param, IStatement body, SourceRange sourceRange) {
			initSourceRange(sourceRange);
			this.param = param; parentizeI(this.param);
			this.body = body; parentizeI(this.body);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, param);
				TreeVisitor.acceptChildren(visitor, body);
			}
			visitor.endVisit(this);
		}
		@Override
		public Iterator<? extends IASTNode> getMembersIterator() {
			if(param != null)
				return IteratorUtil.singletonIterator(param);
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		@Override
		public List<IScope> getSuperScopes() {
			return null;
		}
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}

	}

	public final IStatement body;
	public final CatchClause[] params;
	public final IStatement finallybody;

	public StatementTry(IStatement body, CatchClause[] params, IStatement finallyBody, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.body = body; parentizeI(this.body);
		this.params = params; parentize(this.params);
		this.finallybody = finallyBody; parentizeI(this.finallybody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, finallybody);
		}
		visitor.endVisit(this);
	}

}
