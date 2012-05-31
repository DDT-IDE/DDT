package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CompileExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpStringMacro extends Expression {

	public final Resolvable exp;

	public ExpStringMacro(CompileExp node, ASTConversionContext convContext) {
		convertNode(node);
		this.exp = ExpressionConverter.convert(node.e1, convContext);
	}
	
	public ExpStringMacro(Resolvable exp) {
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
