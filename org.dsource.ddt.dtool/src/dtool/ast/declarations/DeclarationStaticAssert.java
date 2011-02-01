package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.StaticAssert;
import descent.internal.compiler.parser.StaticAssertStatement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationStaticAssert extends ASTNeoNode implements IStatement {

	public Resolvable pred;
	public Resolvable msg;
	
	public DeclarationStaticAssert(StaticAssert elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.pred = Expression.convert(elem.exp, convContext);
		this.msg = Expression.convert(elem.msg, convContext);
	}
	
	public DeclarationStaticAssert(StaticAssertStatement elem, ASTConversionContext convContext) {
		this(elem.sa, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
