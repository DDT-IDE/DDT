package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.CoreUtil;

import org.junit.Before;

import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.ASTNode;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.tests.CommonTestUtils;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "parser/";
	
	@Deprecated
	public static Module parseTestFile(String filename) throws IOException {
		return testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
	@Deprecated
	public static Module testDtoolParse(final String source) {
		return testParse(source, false, true);
	}
	
	@Deprecated
	public static Module testParseInvalidSyntax(final String source) {
		return testParse(source, true, false);
	}
	
	@Deprecated
	public static Module testParse(String source, Boolean expectErrors) {
		return testParse(source, expectErrors, true);
	}
	
	@Deprecated
	public static Module testParse(String source, Boolean expectErrors, boolean checkAST) {
		return testParseDo(source, expectErrors, checkAST).module;
	}
	
	@Deprecated
	public static DeeParserResult testParseDo(String source, Boolean expectErrors) {
		return testParseDo(source, expectErrors, false);
	}
	
	@Deprecated
	public static DeeParserResult testParseDo(String source, Boolean expectErrors, boolean checkSourceRanges) {
		return parseSource(source, expectErrors, checkSourceRanges, "_tests_unnamed_");
	}
	
	@Deprecated
	public static DeeParserResult parseSource(String source, Boolean expectErrors, String defaultModuleName) {
		return parseSource(source, expectErrors, false, defaultModuleName);
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
	
	public static DeeParserResult parseSourceN(String source, boolean expectErrors) {
		DeeParserResult parseResult = DeeParser.parseSource(source);
		
		assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
		
		ASTSourceRangeChecker.checkConsistency(parseResult.module);
		
		return parseResult;
	}
	
	public static Set<String> executedTests = new HashSet<String>();
	
	@Before
	public void printSeparator() throws Exception {
		String simpleName = getClass().getSimpleName();
		if(!executedTests.contains(simpleName)) {
			System.out.println("===============================  "+simpleName+"  ===============================");
			executedTests.add(simpleName);
		}
	}
	
	public static <T, D extends T> D downCast(T object) {
		return CoreUtil.downCast(object);
	}
	
	public static <T, D extends T> D downCast(T object, Class<D> klass) {
		assertTrue(object != null && klass.isInstance(object));
		return klass.cast(object);
	}
	
	protected static Reference reference(String identifier) {
		return new RefIdentifier(identifier);
	}
	
	protected static Reference reference(String... identifiers) {
		RefIdentifier subRef = new RefIdentifier(identifiers[identifiers.length-1]);
		if(identifiers.length == 1) {
			return subRef;
		} else {
			return new RefQualified(
				(IQualifierNode) reference(CommonTestUtils.removeLast(identifiers, 1)), subRef);
		}
	}
	
	public static void checkEqualAsElement(ASTNode[] a, ASTNode[] a2) {
        int length = a.length;
        assertTrue(a2.length == length);

        for (int i=0; i<length; i++) {
            ASTNode o1 = a[i];
            ASTNode o2 = a2[i];
            
            if (o1 == null) {
            	assertTrue(o2 == null);
			} else {
				assertAreEqual(o1.toStringAsCode(), o2.toStringAsCode());
			}
        }
    }
	
	public static void checkParent(ASTNode parent, ASTNode... nodes) {
		for (ASTNode child : nodes) {
			assertTrue(child.getParent() == parent);
		}
	}
	
}