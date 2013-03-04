package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;

public class DeeParserResult {
	
	public final Module module;
	public final ASTNeoNode node;
	public final ArrayList<ParserError> errors;
	
	public DeeParserResult(Module module, ArrayList<ParserError> errors) {
		this.node = this.module = module;
		this.errors = errors;
	}
	
	public DeeParserResult(ASTNeoNode node, ArrayList<ParserError> errors) {
		this.node = node;
		this.module = null;
		this.errors = errors;
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getParsedModule() {
		assertNotNull(module);
		return module;
	}
	
}