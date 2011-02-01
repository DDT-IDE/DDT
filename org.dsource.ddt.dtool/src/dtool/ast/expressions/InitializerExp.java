package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ExpInitializer;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class InitializerExp extends Initializer {
	
	public Resolvable exp;

	public InitializerExp(ExpInitializer elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = Expression.convert(elem.exp, convContext); 
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
