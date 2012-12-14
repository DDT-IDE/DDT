package dtool.parser;

import java.util.ArrayList;

import dtool.ast.definitions.Module;

public class DeeParserResult {
	
	public final Module module;
	public final ArrayList<ParserError> errors;
	
	public DeeParserResult(Module module, ArrayList<ParserError> errors) {
		this.module = module;
		this.errors = errors;
	}
	
}
