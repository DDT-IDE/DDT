package dtool.ast.expressions;

import java.util.List;

import dtool.ast.ASTNeoNode;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public abstract class Initializer extends ASTNeoNode{

	public static Initializer convert(descent.internal.compiler.parser.Initializer initializer
			, ASTConversionContext convContext) {
		return (Initializer) DescentASTConverter.convertElem(initializer, convContext);
	}

	
	public static Initializer[] convertMany(List<descent.internal.compiler.parser.Initializer> elements
			, ASTConversionContext convContext) {
		if(elements == null)
			return null;
		Initializer[] rets = new Initializer[elements.size()];
		DescentASTConverter.convertMany(elements, rets, convContext);
		return rets;
	}
}
