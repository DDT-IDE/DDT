package dtool.ast.definitions;

import java.util.List;

import descent.internal.compiler.parser.IdentifierExp;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * TODO clean up template parameter semantics a bit
 */
public abstract class TemplateParameter extends DefUnit {

	public TemplateParameter(IdentifierExp ident) {
		super(ident);
	}
	
	public static TemplateParameter[] convertMany(descent.internal.compiler.parser.TemplateParameter[] elems
			, ASTConversionContext convContext) {
		return DescentASTConverter.convertMany(elems, TemplateParameter.class, convContext);
	}
	
	public static TemplateParameter[] convertMany(List<descent.internal.compiler.parser.TemplateParameter> elems
			, ASTConversionContext convContext) {
		return DescentASTConverter.convertMany(elems, TemplateParameter.class, convContext);
	}

}
