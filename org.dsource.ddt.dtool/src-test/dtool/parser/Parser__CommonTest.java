package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	@Deprecated
	public static DeeParserResult parseSource(String source, Boolean expectErrors, boolean checkSourceRanges,
			String defaultModuleName) {
		DeeParserResult parseResult = DeeParserSession.parseSource(source, defaultModuleName);
		
		if(expectErrors != null) {
			assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
		}
		if(checkSourceRanges && !parseResult.hasSyntaxErrors()) {
			// We rarely get good source ranges with syntax errors; 
			ASTCommonSourceRangeChecker.checkConsistency(parseResult.module);
		}
		return parseResult;
	}
	
	public static DeeParserResult testParseSource(String source, Boolean expectErrors, boolean checkSourceRanges, 
		String defaultModuleName) {
		DeeParserResult parseResult = DeeParser.parseSource(source, defaultModuleName);
		
		if(expectErrors != null) {
			assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
//			source.substring(parseResult.errors.get(0).sourceRange.getStartPos() - 10);
//			source.substring(parseResult.errors.get(0).sourceRange.getStartPos());
		}
		
		if(checkSourceRanges) {
			ASTSourceRangeChecker.checkConsistency(parseResult.module);
		}
		
		return parseResult;
	}
	
}