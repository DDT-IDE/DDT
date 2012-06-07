package dtool.ast.statements;

import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public abstract class Statement extends ASTNeoNode implements IStatement {

	public static IStatement convert(descent.internal.compiler.parser.Statement elem, ASTConversionContext convContext) {
		return DescentASTConverter.convertElem(elem, IStatement.class, convContext);
	}
	
}
