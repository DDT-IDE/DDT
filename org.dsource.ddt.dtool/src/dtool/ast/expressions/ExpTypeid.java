package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeidExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpTypeid extends Expression {
	
	Reference typeArgument;
	Expression expressionArgument;
	
	public ExpTypeid(TypeidExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.typeidType != null) {
			this.typeArgument = ReferenceConverter.convertType(elem.typeidType, convContext);
		} else {
			assertNotNull(elem.argumentExp__DDT_ADDITION);
			expressionArgument = ExpressionConverter.convert(elem.argumentExp__DDT_ADDITION, convContext);
		}
	}
	
	public ExpTypeid(Reference typeArgument) {
		this.typeArgument = typeArgument;
		this.expressionArgument = null;
		
		if (this.typeArgument != null)
			this.typeArgument.setParent(this);
	}

	public ExpTypeid(Expression expressionArgument) {
		this.expressionArgument = expressionArgument;
		this.typeArgument = null;
		
		if (this.expressionArgument != null)
			this.expressionArgument.setParent(this);
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
