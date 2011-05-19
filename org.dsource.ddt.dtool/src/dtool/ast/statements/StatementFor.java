package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ForStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementFor extends Statement {

	public IStatement init;
	public Resolvable cond;
	public Resolvable inc;
	public IStatement body;


	public StatementFor(ForStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.init = Statement.convert(elem.init, convContext);
		this.cond = ExpressionConverter.convert(elem.condition, convContext);
		this.inc = ExpressionConverter.convert(elem.increment, convContext);
		this.body = Statement.convert(elem.body, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, init);
			TreeVisitor.acceptChildren(visitor, cond);
			TreeVisitor.acceptChildren(visitor, inc);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
