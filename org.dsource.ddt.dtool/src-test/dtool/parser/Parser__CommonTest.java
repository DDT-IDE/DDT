package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.Parser;
import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.descentadapter.DeeParserSession;
import dtool.descentadapter.DescentParserAdapter;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	@Deprecated
	public static void parseSource(String source, Boolean expectErrors, boolean checkSourceRanges,
			String defaultModuleName) {
		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, Parser.D2, null);
		
		boolean hasErrors = parserAdapter.parser.problems.size() > 0;
		
		if(expectErrors != null) {
			assertTrue(hasErrors == expectErrors, "expectedErrors is not: " + expectErrors);
		}
		if(checkSourceRanges && !hasErrors) {
		}
	}
	
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