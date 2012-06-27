package dtool.parser;

import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IProblemReporter;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;

public class DeeParserSession {
	
	public static DeeParserSession parseSource(String defaultModuleName, String source, int langVersion,
			IProblemReporter problemReporter) {
		DeeParserSession deeParserSession = new DeeParserSession(defaultModuleName);
		deeParserSession.parserAdapter = DescentParserAdapter.parseSource(source, langVersion, problemReporter);
		deeParserSession.neoModule = DescentASTConverter.convertModule(deeParserSession.parserAdapter.mod, 
				defaultModuleName);
		return deeParserSession;
	}
	
	public static DeeParserSession parseWithRecovery(String defaultModuleName, String source, int langVersion,
			final int offset, Token lastTokenNonWS) {
		DeeParserSession parseSession = new DeeParserSession(defaultModuleName);
		parseSession.parserAdapter = DescentParserAdapter.parseSource(source, langVersion, null);
		parseSession.parserAdapter.recoverForCompletion(source, offset, lastTokenNonWS);
		parseSession.neoModule = DescentASTConverter.convertModule(parseSession.parserAdapter.mod, defaultModuleName);
		return parseSession;
	}
	
	public static DeeParserSession parseSource(String source, String defaultModuleName) {
		return parseSource(defaultModuleName, source, Parser.D2, null);
	}
	
	protected final String defaultModuleName;
	protected DescentParserAdapter parserAdapter;
	public Module neoModule;
	
	public DeeParserSession(String defaultModuleName) {
		this.defaultModuleName = defaultModuleName;
	}
	
	public boolean hasSyntaxErrors() {
		return parserAdapter.mod.problems.size() != 0;
	}
	
	public boolean isQualifiedDotFix() {
		return parserAdapter.isQualifiedDotFix;
	}
	
	public descent.internal.compiler.parser.Module getDMDModule() {
		return parserAdapter.mod;
	}
	
	public Module getParsedModule() {
		return neoModule;
	}

}
