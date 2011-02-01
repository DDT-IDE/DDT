package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.FileExp;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpLiteralImportedString extends Expression {
	
	final public Resolvable exp; 

	public ExpLiteralImportedString(FileExp node, ASTConversionContext convContext) {
		convertNode(node);
		this.exp = Expression.convert(node.e1, convContext); 
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
