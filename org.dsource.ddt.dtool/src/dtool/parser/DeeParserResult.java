package dtool.parser;

import java.util.ArrayList;

import dtool.ast.ASTNeoNode;

public class DeeParserResult {
	
	public final ASTNeoNode node;
	public final ArrayList<ParserError> errors;
	
	public DeeParserResult(ASTNeoNode node, ArrayList<ParserError> errors) {
		this.node = node;
		this.errors = errors;
	}
	
}
