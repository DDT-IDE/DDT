package dtool.ast.definitions;

import descent.internal.compiler.parser.IdentifierExp;

/**
 * TODO clean up template parameter semantics a bit
 */
public abstract class TemplateParameter extends DefUnit {

	public TemplateParameter(IdentifierExp ident) {
		super(ident);
	}
	
	public TemplateParameter(DefUnitDataTuple dudt) {
		super(dudt);
	}
	
}
