package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	@Deprecated
	public static DeeParserResult testParseDo(String source, Boolean expectErrors, boolean checkSourceRanges) {
		return parseSource(source, expectErrors, checkSourceRanges, "_tests_unnamed_");
	}
	
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
		}
		
		if(checkSourceRanges) {
			ASTSourceRangeChecker.checkConsistency(parseResult.module);
		}
		
		return parseResult;
	}
	
}