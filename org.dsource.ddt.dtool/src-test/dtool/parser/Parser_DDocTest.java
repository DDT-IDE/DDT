package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.tests.IDToolTestConstants;

/**
 * Very basic tests, needs more cases.
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
		Module module = parseTestFile(IDToolTestConstants.PARSER_DDOC);
		
		assertTrue(module.comments.length == 2);
		assertContains(module.comments[0], "Module");
	}
	
	@Test
	public void testDDocParse() throws Exception { testDDocParse$(); }
	public void testDDocParse$() throws Exception {
		Module module = parseTestFile(IDToolTestConstants.PARSER_DDOC);
		
		assertTrue(module.comments.length == 2);
		assertContains(module.comments[0], "Module");
		
		DefUnit struct1 = doCast(module.getChildren()[1]);
		assertTrue(struct1.comments.length == 1);
		check(struct1, "Struct1");
		
		DefUnit struct2 = doCast(module.getChildren()[2]);
		check(struct2, "Struct2", "Struct2-xx");
		
		DefUnit var = doCast(module.getChildren()[4]);
		check(var, "variable", "var-dxx");
		
		DefUnit var2 =  doCast(module.getChildren()[5]);
		check(var2, "variable2", "var2-xx");
		
		DefUnit func1 = doCast(module.getChildren()[6].getChildren()[0].getChildren()[0]);
		check(func1, "func1");
		
		DefUnit func2 = doCast(module.getChildren()[7].getChildren()[0].getChildren()[0]);
		check(func2, "func2");
		
		DefUnit func3 = doCast(module.getChildren()[8].getChildren()[0].getChildren()[0]);
		check(func3, "func3");
		
		DefUnit func4 = doCast(module.getChildren()[9].getChildren()[0] /*.getChildren()[0]*/);
		check(func4, "func4");
		
//		DefUnit funcXX = doCast(module.getChildren()[10].getChildren()[0].getChildren()[0].getChildren()[0]
//			/*.getChildren()[0]*/);
//		check(funcXX, "funcXX");
	}
	
	protected void check(DefUnit defunit, String... containStrings) {
		assertNotNull(defunit.comments);
		assertTrue(defunit.comments.length == containStrings.length);
		for(int i = 0; i < defunit.comments.length; i++) {
			assertContains(defunit.comments[i], containStrings[i]);
		}
	}
	
}
