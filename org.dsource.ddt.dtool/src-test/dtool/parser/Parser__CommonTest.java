package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTSourceRangeChecker;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	public static DeeParserResult testParseSource(String source, Boolean expectErrors, boolean checkSourceRanges, 
		String defaultModuleName) {
		DeeParserResult parseResult = DeeParser.parseSource(source, defaultModuleName);
		
		if(expectErrors != null) {
			assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
//			source.substring(parseResult.errors.get(0).sourceRange.getStartPos() - 30);
//			source.substring(parseResult.errors.get(0).sourceRange.getStartPos());
		}
		
		if(checkSourceRanges) {
			ASTSourceRangeChecker.checkConsistency(parseResult.module);
		}
		
		return parseResult;
	}
	
}