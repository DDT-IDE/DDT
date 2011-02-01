package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import descent.internal.compiler.parser.Comment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;

/**
 * Skeleton test, needs more cases.
 */
public class Parser_DDocTest extends Parser__CommonTest {
	
	@SuppressWarnings("unchecked")
	public static <T> T doCast(Object obj) {
		return (T) obj;
	}
	
	public static void assertContains(Object string, String substring) {
		assertTrue(string != null && string.toString().contains(substring));
	}
	
	@Test
	public void testDDocParse0() throws Exception { testDDocParse0$(); }
	public void testDDocParse0$() throws Exception {
		Module module = parseTestFile("ddoc.d");
		
		assertTrue(module.comments.length == 2);
		assertContains(module.comments[0], "Module");
	}

	@Test
	public void testDDocParse() throws Exception { testDDocParse$(); }
	public void testDDocParse$() throws Exception {
		Module module = parseTestFile("ddoc.d");
		
		assertTrue(module.comments.length == 2);
		assertContains(module.comments[0], "Module");
		
		
		DefUnit struct1 = doCast(module.getChildren()[1]);
		assertTrue(struct1.comments.length == 1);

		DefUnit struct2 = doCast(module.getChildren()[2]);
		check(struct2, "Struct2", "Struct2-b");

		DefUnit var = doCast(module.getChildren()[4]);
		check(var, "variable", "var-b");
		
		DefUnit var2 = doCast(module.getChildren()[6]);
		check(var2, "variable2", "var2-b");

		DefUnit func1 = doCast(module.getChildren()[5]);
		check(func1, "func1");
	}

	private void check(DefUnit defunit, String... containStrings ) {
		assertTrue(defunit.comments.length == containStrings.length);
		int i = 0;
		for (Comment comment : defunit.comments) {
			assertContains(comment, containStrings[i]);
			i++;
		}
	}
	
}
