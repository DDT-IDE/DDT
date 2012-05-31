package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementForeach extends Statement {

	public boolean reverse;
	public IFunctionParameter[] params;
	public Resolvable iterable;
	public IStatement body;

	public StatementForeach(ForeachStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		// TODO: implement foreach parameters, and unittest them.
		//this.params = new IFunctionParameter[elem.arguments.size()]; 
		//DescentASTConverter.convertMany(elem.arguments.toArray(), this.params);
		this.iterable = ExpressionConverter.convert(elem.sourceAggr, convContext);
		this.body = Statement.convert(elem.body, convContext);
		this.reverse = elem.op == TOK.TOKforeach_reverse;
	}
	
	public StatementForeach(IFunctionParameter[] params, Resolvable iterable, IStatement body, boolean reverse) {
		this.params = params;
		this.iterable = iterable;
		this.body = body;
		this.reverse = reverse;

		if (this.params != null) {
			for (IFunctionParameter fp: this.params)
				((ASTNeoNode) fp).setParent(this);
		}
		
		if (this.iterable != null)
			this.iterable.setParent(this);
		
		if (this.body != null)
			((ASTNeoNode) this.body).setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, iterable);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
