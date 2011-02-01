package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ArrayLiteralExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpArrayLiteral extends Expression {
	
	public Resolvable[] args;

	public ExpArrayLiteral(ArrayLiteralExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.args = Expression.convertMany(elem.elements, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
