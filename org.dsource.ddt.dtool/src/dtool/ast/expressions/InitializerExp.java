package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ExpInitializer;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class InitializerExp extends Initializer {
	
	public Resolvable exp;

	public InitializerExp(ExpInitializer elem, ASTConversionContext convContext) {
		initSourceRange(DefinitionConverter.sourceRange(elem, false));
		this.exp = ExpressionConverter.convert(elem.exp, convContext); 
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
