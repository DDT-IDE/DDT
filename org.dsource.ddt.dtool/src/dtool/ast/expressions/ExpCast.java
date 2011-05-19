package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CastExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpCast extends Expression {
	
	public final Resolvable exp;
	public final Reference type;
	
	public ExpCast(CastExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(elem.sourceE1, convContext);
		this.type = ReferenceConverter.convertType(elem.sourceTo, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
}
