package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CaseRangeStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementCaseRange extends Statement {

	public Resolvable expFirst;
	public Resolvable expLast;
	public IStatement st;
	
	public StatementCaseRange(CaseRangeStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.expFirst = ExpressionConverter.convert(elem.first, convContext);
		this.expLast = ExpressionConverter.convert(elem.last, convContext);
		this.st = Statement.convert(elem.statement, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, expFirst);
			TreeVisitor.acceptChildren(visitor, expLast);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
