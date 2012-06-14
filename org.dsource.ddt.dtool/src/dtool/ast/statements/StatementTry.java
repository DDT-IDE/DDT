package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode implements IScopeNode {
		
		public final IFunctionParameter param;
		public final IStatement body;
		
		public CatchClause(IFunctionParameter param, IStatement body, SourceRange sourceRange) {
			initSourceRange(sourceRange);
			this.param = parentizeI(param);
			this.body = parentizeI(body);
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
		public Iterator<? extends IASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
			if(param != null)
				return IteratorUtil.singletonIterator(param);
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		@Override
		public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
			return null;
		}
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}
		
	}
	
	public final IStatement body;
	public final ArrayView<CatchClause> params;
	public final IStatement finallyBody;
	
	public StatementTry(IStatement body, ArrayView<CatchClause> params, IStatement finallyBody, 
			SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.body = parentizeI(body);
		this.params = parentize(params);
		this.finallyBody = parentizeI(finallyBody);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, finallyBody);
		}
		visitor.endVisit(this);
	}
	
}