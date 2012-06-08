package dtool.parser;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;

import descent.internal.compiler.parser.ast.IASTNode;
import descent.internal.compiler.parser.ast.NaiveASTFlattener;
import dtool.ast.ASTChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.ParserAdapter;
import dtool.tests.DToolBaseTest;

public abstract class Parser__CommonTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "parser/";
	
	public static Module parseTestFile(String filename) throws CoreException, IOException {
		return testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
	public static final String COMMON = "common/";
	
	public static Module testDtoolParse(final String source) {
		return testParse(source, false, true);
	}
	
	public static Module testParseInvalidSyntax(final String source) {
		return testParse(source, true, false);
	}
	
	public static Module testParse(String source, Boolean expectErrors) {
		return testParse(source, expectErrors, true);
	}
	
	public static Module testParse(String source, Boolean expectErrors, boolean checkAST) {
		return testParseDo(source, expectErrors, checkAST).neoModule;
	}
	
	public static class ParseResult {
		public descent.internal.compiler.parser.Module mod;
		
		public Module neoModule;

		public boolean hasSyntaxErrors() {
			return mod.problems.size() != 0;
		}

		public IASTNode getChild(int ix) {
			 return neoModule.getChildren()[ix];
		}
		
	}
	
	public static ParseResult testParseDo(String source, Boolean expectErrors, boolean checkAST) {
		ParseResult parseResult = new ParseResult();
		parseResult.mod = ParserAdapter.parseSource(source).mod;
		if(checkAST) {
			parseResult.mod.accept(new NaiveASTFlattener()); // Test NaiveASTFlattener
		}
		if(expectErrors != null) {
			assertTrue((parseResult.mod.problems.size() > 0) == expectErrors, "expectedErrors is not: " + expectErrors);
		}
		
		Module neoModule = DescentASTConverter.convertModule(parseResult.mod, "_tests_unnamed_");
		if(checkAST && parseResult.mod.problems.size() == 0) {
			// We rarely get good source ranges with size == 0; 
			ASTChecker.checkConsistency(neoModule);
		}
		parseResult.neoModule = neoModule;
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
	
	public static <T> T[] removeLast(T[] array, int count) {
		return ArrayUtil.removeLast(array, count);
	}
	
	protected static Reference reference(String identifier) {
		return new RefIdentifier(identifier);
	}
	
	protected static Reference reference(String... identifiers) {
		RefIdentifier subRef = new RefIdentifier(identifiers[identifiers.length-1]);
		if(identifiers.length == 1) {
			return subRef;
		} else {
			return new RefQualified(reference(removeLast(identifiers, 1)), subRef);
		}
	}
	
	public static void checkEqualAsElement(ASTNeoNode[] a, ASTNeoNode[] a2) {
        int length = a.length;
        assertTrue(a2.length == length);

        for (int i=0; i<length; i++) {
            ASTNeoNode o1 = a[i];
            ASTNeoNode o2 = a2[i];
            
            if (o1 == null) {
            	assertTrue(o2 == null);
			} else {
				assertAreEqual(o1.toStringAsElement(), o2.toStringAsElement());
			}
        }
    }
	
	public static void checkParent(ASTNeoNode parent, ASTNeoNode... nodes) {
		for (ASTNeoNode child : nodes) {
			assertTrue(child.getParent() == parent);
		}
	}
	
}
