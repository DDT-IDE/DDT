package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.UnaExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class PrefixExpression extends Expression {
	
	public interface Type {
		
		int ADDRESS = 1;
		int PRE_INCREMENT = 2;
		int PRE_DECREMENT = 3;
		int POINTER = 4;
		int NEGATIVE = 5;
		int POSITIVE = 6;
		int NOT = 7;
		int INVERT = 8;
	}
	
	public Resolvable exp;

	public int kind;


	public PrefixExpression(UnaExp elem, int kind, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1, convContext);
		this.kind = kind;
	}

	public PrefixExpression(BinExp elem, int kind, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.exp = (Resolvable) DescentASTConverter.convertElem(elem.e1, convContext);
		this.kind = kind;
	}
	
	public PrefixExpression(Resolvable exp, int kind) {
		this.exp = exp;
		this.kind = kind;
		
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
