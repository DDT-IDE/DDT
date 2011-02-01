package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CastExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpCast extends Expression {
	
	Resolvable exp;
	Reference type;

	public ExpCast(CastExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = Expression.convert(elem.e1, convContext); 
		this.type = ReferenceConverter.convertType(elem.type, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}

}
