package dtool.parser;

import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IProblemReporter;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;

public class DeeParserSession extends DeeParserResult {
	
	public static DeeParserSession parseSource(String defaultModuleName, String source, int langVersion,
			IProblemReporter problemReporter) {
		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, langVersion, problemReporter);
		Module module = DescentASTConverter.convertModule(parserAdapter.mod, defaultModuleName);
		return new DeeParserSession(module, defaultModuleName, parserAdapter);
	}
	
	public static DeeParserSession parseWithRecovery(String defaultModuleName, String source, int langVersion,
			final int offset, Token lastTokenNonWS) {
		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, langVersion, null);
		parserAdapter.recoverForCompletion(source, offset, lastTokenNonWS);
		Module module = DescentASTConverter.convertModule(parserAdapter.mod, defaultModuleName);
		return new DeeParserSession(module, defaultModuleName, parserAdapter);
	}
	
	public static DeeParserSession parseSource(String source, String defaultModuleName) {
		return parseSource(defaultModuleName, source, Parser.D2, null);
	}
	
	protected final String defaultModuleName;
	protected final DescentParserAdapter parserAdapter;
	
	public DeeParserSession(Module module, String defaultModuleName, DescentParserAdapter parserAdapter) {
		super(null, module, false, null);
		this.defaultModuleName = defaultModuleName;
		this.parserAdapter = parserAdapter;
	}
	
	@Override
	public boolean hasSyntaxErrors() {
		return parserAdapter.mod.problems.size() != 0;
	}
	
	public boolean isQualifiedDotFix() {
		return parserAdapter.isQualifiedDotFix;
	}
	
	public descent.internal.compiler.parser.Module getDMDModule() {
		return parserAdapter.mod;
	}
	
}
