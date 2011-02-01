package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationStaticIf extends DeclarationConditional {
	
	public Resolvable exp;

	public DeclarationStaticIf(ASTNode elem, StaticIfCondition condition, NodeList thendecls, NodeList elsedecls
			, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = Expression.convert(condition.exp, convContext);
		this.thendecls = thendecls; 
		this.elsedecls = elsedecls;
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thendecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[static if("+"..."+")]";
	}

}
