package dtool.parser;

import dtool.ast.definitions.Module;

public class DeeParser extends DeeParser_ImplDecls {
	
	public DeeParser(String source) {
		super(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	public static DeeParserResult parse(String source) {
		DeeParser deeParser = new DeeParser(source);
		Module module = deeParser.parseModule();
		return new DeeParserResult(module, deeParser.errors);
	}
	
}