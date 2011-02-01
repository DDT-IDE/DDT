package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeidExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpTypeid extends Expression {
	
	Reference typeArgument;
	Expression expressionArgument;
	
	public ExpTypeid(TypeidExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.typeidType != null) {
			this.typeArgument = ReferenceConverter.convertType(elem.typeidType, convContext);
		} else {
			assertNotNull(elem.argumentExp__DDT_ADDITION);
			expressionArgument = Expression.convert(elem.argumentExp__DDT_ADDITION, convContext);
		}
	}
	
	public Resolvable getArgument() {
		return typeArgument != null ? typeArgument : expressionArgument;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, getArgument());
		}
		visitor.endVisit(this);
	}
	
}
