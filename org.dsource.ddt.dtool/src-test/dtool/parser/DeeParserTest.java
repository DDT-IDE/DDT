package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.junit.Test;

import dtool.ast.ASTSourceRangeChecker;
import dtool.ast.definitions.Module;

public class DeeParserTest {
	
	@Test
	public void basicTest() throws Exception { basicTest$(); }
	public void basicTest$() throws Exception {
		String source = "module foo;";
		runParserTest(source);
	}
	
	public void runParserTest(String source) {
		DeeParserResult result = DeeParser.parse(source);
		
		Module module = result.module;
		assertNotNull(module);
		
		ASTSourceRangeChecker.ASTAssertChecker.checkConsistency(module);
		
		String generatedSource = ASTSourcePrinter.printSource(module);
		checkSourceEquality(source, generatedSource);
	}
	
	public void checkSourceEquality(String source, String generatedSource) {
		DeeLexer originalSourceLexer = new DeeLexer(source);
		DeeLexer generatedSourceLexer = new DeeLexer(generatedSource);
		
		while(true) {
			Token tok1 = getContentToken(originalSourceLexer, true);
			Token tok2 = getContentToken(generatedSourceLexer, true);
			assertEquals(tok1.tokenCode, tok2.tokenCode);
			assertEquals(tok1.value, tok2.value);
			
			if(tok1.tokenCode == DeeTokens.EOF) {
				break;
			}
		}
	}
	
	public Token getContentToken(DeeLexer lexer, boolean ignoreComments) {
		while(true) {
			Token token = lexer.next(); 
			if(!token.tokenCode.isParserIgnored || (!ignoreComments && 
				isCommentToken(token))) {
				return token;
			}
		}
		
	}
	public boolean isCommentToken(Token token) {
		return 
			token.tokenCode == DeeTokens.COMMENT_LINE ||
			token.tokenCode == DeeTokens.COMMENT_MULTI ||
			token.tokenCode == DeeTokens.COMMENT_NESTED;
	}
	
}