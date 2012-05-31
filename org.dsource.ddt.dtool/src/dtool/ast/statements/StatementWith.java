package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.WithStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementWith extends Statement {

	public Resolvable exp;
	public IStatement body;

	public StatementWith(WithStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(elem.exp, convContext);
		this.body = Statement.convert(elem.body, convContext);
	}
	
	public StatementWith(Resolvable exp, IStatement body) {
		this.exp = exp;
		this.body = body;
		
		if (this.exp != null)
			this.exp.setParent(this);
		
		if (this.body != null)
			((ASTNeoNode) this.body).setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
