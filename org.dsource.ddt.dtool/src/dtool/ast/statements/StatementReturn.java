package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ReturnStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class StatementReturn extends Statement {

	public Resolvable exp;

	public StatementReturn(ReturnStatement element, ASTConversionContext convContext) {
		convertNode(element);
		this.exp = ExpressionConverter.convert(element.exp, convContext);
	}
	
	public StatementReturn(Resolvable exp) {
		this.exp = exp;
		
		if (this.exp != null)
			this.exp.setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	 
	}

}
