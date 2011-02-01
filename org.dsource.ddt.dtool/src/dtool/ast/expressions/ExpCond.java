package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CondExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpCond extends Expression {

	public Resolvable predExp;
	public Resolvable trueExp;
	public Resolvable falseExp;

	public ExpCond(CondExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.predExp = Expression.convert(elem.econd, convContext); 
		this.trueExp = Expression.convert(elem.e1, convContext);
		this.falseExp = Expression.convert(elem.e2, convContext); 
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, predExp);
			TreeVisitor.acceptChildren(visitor, trueExp);
			TreeVisitor.acceptChildren(visitor, falseExp);
		}
		visitor.endVisit(this);
	}

}
