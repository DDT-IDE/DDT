package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode implements IScopeNode {
		
		public final IFunctionParameter param;
		public final IStatement body;

		public CatchClause(IFunctionParameter param, IStatement body, SourceRange sourceRange) {
			initSourceRange(sourceRange);
			this.param = param; parentize(this.param);
			this.body = body; parentize(this.body);
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

	public IStatement body;
	public CatchClause[] params;
	public IStatement finallybody;


	public StatementTry(TryCatchStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		convertTryCatch(elem, convContext);
	}
	
	public StatementTry(IStatement body, CatchClause[] params, IStatement finallyBody) {
		this.body = body;
		this.params = params;
		this.finallybody = finallyBody;
		
		if (this.body != null)
			((ASTNeoNode) this.body).setParent(this);
		
		if (this.finallybody != null)
			((ASTNeoNode) this.finallybody).setParent(this);
		
		if (this.params != null) {
			for (CatchClause cc : params) {
				cc.setParent(this);
			}
		}
	}

	private void convertTryCatch(TryCatchStatement elem, ASTConversionContext convContext) {
		Object[] catches = elem.catches.toArray();
		this.params = DescentASTConverter.convertMany(catches, CatchClause.class, convContext);
		this.body = Statement.convert(elem.body, convContext);
	}
	
	public StatementTry(TryFinallyStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.body instanceof TryCatchStatement){
			convertTryCatch((TryCatchStatement)elem.body, convContext);
		} else {
			this.params = new CatchClause[0];
			this.body = Statement.convert(elem.body, convContext);
		}
		this.finallybody =  Statement.convert(elem.finalbody, convContext);
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
