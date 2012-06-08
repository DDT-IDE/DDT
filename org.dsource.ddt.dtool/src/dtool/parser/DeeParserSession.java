package dtool.parser;

import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IASTNode;
import descent.internal.compiler.parser.ast.IProblemReporter;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;

public class DeeParserSession {
	
	public static DeeParserSession parseSource(String moduleName, String source, int langVersion,
			IProblemReporter problemReporter) {
		DeeParserSession deeParserSession = new DeeParserSession(moduleName);
		deeParserSession.parserAdapter = DescentParserAdapter.parseSource(source, langVersion, problemReporter);
		deeParserSession.neoModule = DescentASTConverter.convertModule(deeParserSession.parserAdapter.mod, moduleName);
		return deeParserSession;
	}
	
	public static DeeParserSession parseWithRecovery(String moduleName, String source, int langVersion,
			final int offset, Token lastTokenNonWS) {
		DeeParserSession parseSession = new DeeParserSession(moduleName);
		parseSession.parserAdapter = DescentParserAdapter.parseSource(source, langVersion, null);
		parseSession.parserAdapter.recoverForCompletion(source, offset, lastTokenNonWS);
		parseSession.neoModule = DescentASTConverter.convertModule(parseSession.parserAdapter.mod, moduleName);
		return parseSession;
	}
	
	public static DeeParserSession parseSource(String source, String moduleName) {
		return parseSource(moduleName, source, Parser.D2, null);
	}
	
	protected final String moduleName;
	protected DescentParserAdapter parserAdapter;
	public Module neoModule;
	
	public DeeParserSession(String moduleName) {
		this.moduleName = moduleName;
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
	
	public IASTNode getChild(int ix) {
		return neoModule.getChildren()[ix];
	}

}
