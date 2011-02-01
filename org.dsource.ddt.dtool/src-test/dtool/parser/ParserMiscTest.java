package dtool.parser;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * Miscellaneous convertion tests
 */
public class ParserMiscTest extends Parser__CommonTest {
	
	
	@Test
	public void testFoo() throws CoreException {
		testDtoolParse(
				"module foo;" +
				"import pack.bar;" +
				"public import std.stdio;"
		);
	}
	
	@Test
	public void testRenamed() throws CoreException {
		// RENAMED IMPORT, static import
		testDtoolParse(
				"module pack.modul;" +
				"import dee = std.stdio, lang = lang.string;"
		);
		testDtoolParse(
				"module pack.modul;" +
				"private static import dee_io = std.stdio;"
		);
	}
	
	@Test
	public void testSelective() throws CoreException {
		// SELECTIVE IMPORT, static import
		testDtoolParse(
				"module pack.modul;" +
				"import std.stdio : writefln, foo = writef;"
		);
		testDtoolParse(	"module pack.modul;" +
				"import langio = std.stdio : writefln, foo = writef; "
		);
		testDtoolParse(	"module pack.modul;" +
				"private static import langio = std.stdio : writefln, foo = writef; "
		);
	}
	
	// -- maybe the tests below should all go to common/miscCases
	
	@Test
	public void testAll() throws IOException, CoreException {
		parseTestFile("testNodes.d");
	}
	
	@Test
	public void testAll2() throws IOException, CoreException {
		parseTestFile("conditionals.d");
	}
	
	@Test
	public void testAllMixinContainer() throws IOException, CoreException {
		parseTestFile("mixincontainer.d");
	}
	
	@Test
	public void testRefNodes() throws IOException, CoreException {
		parseTestFile("refs.d");
	}
	
	@Test
	public void testDeclAttrib() throws IOException, CoreException {
		parseTestFile("declAttrib.d");
	}
	
	@Test
	public void testForeach() throws IOException, CoreException {
		parseTestFile("for_each.d");
	}
	
	@Test
	public void testNewExp() throws IOException, CoreException {
		parseTestFile("newExp.d");
	}
	
}
