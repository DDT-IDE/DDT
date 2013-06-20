package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.Lexer;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IProblemReporter;

/**
 * Provides an adapter for Descent parser, adding:
 * - Some functionality to recover from syntax errors for content assist.
 */
public class DescentParserAdapter {
	
	public static DescentParserAdapter parseSource(String source, int apiLevel, IProblemReporter problemReporter) {
		Parser newparser = new Parser(apiLevel, source);
		newparser.setProblemReporter(problemReporter);
		DescentParserAdapter adapter = new DescentParserAdapter(newparser);
		adapter.mod = adapter.parser.parseModuleObj();
		assertTrue(adapter.mod.length == source.length());
		return adapter;
	}
	
	public Module mod;
	public Parser parser;
	
	/** Whether a qualified dot fix was performed. 
	 * If it was, then a non-prefixed search should be made. */
	public boolean isQualifiedDotFix = false;
	
	public DescentParserAdapter(Parser parser) {
		this.parser = parser;
	}
	
	public static Token tokenizeSource(String str) {
		Token tokenList = null;
		Token tokenListEnd = null;
		Lexer lexer = new Lexer(str, true, true, false, false, Parser.D2);
		do {
			lexer.nextToken();
			Token newtoken = new Token(lexer.token);
			if(tokenListEnd != null) {
				tokenListEnd.next = newtoken;
			} else {
				// First token
				tokenList = newtoken;
				tokenListEnd = newtoken;
			}
			tokenListEnd = newtoken;
		} while(tokenListEnd.value != TOK.TOKeof);
		return tokenList;
	}
	
	/** Attempt syntax recovery for the purposes of code completion. */
	public void recoverForCompletion(String str, int offset, Token lastToken) {
		internalRecover(str, offset, lastToken);
	}
	
	public void parseModule(String str) {
		parser = new Parser(Parser.D2, str);
		mod = parser.parseModuleObj();
	}
	
	private void internalRecover(String str, int offset, Token lastToken) {
		
		if(mod == null) {
			parseModule(str);
		}
		
		if(mod.problems.size() != 0) {
			if(lastToken != null && lastToken.value == TOK.TOKdot && lastToken.next.value != TOK.TOKidentifier) {
				// Insert a dummy identifier, so the reference will parse
				String newstr = str.substring(0, offset) + "_" + str.substring(offset, str.length());
				
				// Mark this for ahead
				isQualifiedDotFix = true;
				
				parseModule(newstr);
				
				if(mod.problems.size() == 0) {
					return;
				}
			} 
		}
		
	}
	
}
