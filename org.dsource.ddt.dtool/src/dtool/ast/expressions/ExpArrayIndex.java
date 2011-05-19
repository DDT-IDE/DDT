package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ArrayExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpArrayIndex extends Expression {

	public Resolvable array;
	public Resolvable[] args;
	
	public ExpArrayIndex(ArrayExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.array = ExpressionConverter.convert(elem.e1, convContext);
		this.args = ExpressionConverter.convertMany(elem.arguments, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, array);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
