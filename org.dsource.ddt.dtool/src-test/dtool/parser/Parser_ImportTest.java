package dtool.parser;

import static dtool.tests.MiscDeeTestUtils.fnDefUnitToStringAsElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;

/**
 * Parser/AST import tests.
 * TODO test aliasing, more tests for selection imports. Test more invalid cases
 */
public class Parser_ImportTest extends Parser__CommonTest {
	
	@Test
	public void testImport() throws Exception { testImport$(); }
	public void testImport$() throws Exception {
		Module module = testDtoolParse(
				"module foo;" +
				"import pack.bar;" +
				"public import pack;" +
				"public static import std.xpto, pack, blah.blah;" +
				"import asdf, std.foo : selec1, selec2;"
		);
		
		checkDefunit(module, "foo", 5+1); // Last decl gets folded in two, prolly DMD parser issue
		DeclarationModule child0 = downCast(module.getChildren()[0]);
		assertTrue(child0.getModuleNode() == module);
		
		
		DeclarationImport child1 = checkImport(module.getChildren()[1], false, false, 1);
		checkNode(child1.imports.get(0).moduleRef, "pack.bar");
		
		
		DeclarationImport child2 = checkImport(module.getChildren()[2], true, false, 1);
		checkNode(child2.imports.get(0).moduleRef, "pack");
		
		
		DeclarationImport child3 = checkImport(module.getChildren()[3], true, true, 3);
		checkNode(child3.imports.get(0).moduleRef, "std.xpto");
		checkNode(child3.imports.get(1).moduleRef, "pack");
		checkNode(child3.imports.get(2).moduleRef, "blah.blah");
		
		
		DeclarationImport child4 = checkImport(module.getChildren()[4], false, false, 2);
		checkNode(child4.imports.get(0).moduleRef, "asdf");
		checkNode(child4.imports.get(1).moduleRef, "std.foo");
		checkSelectiveImportFragment(child4.imports.get(1), "selec1", "selec2");
		
	}
	
	protected void checkDefunit(DefUnit module, String name, Integer numChildren) {
		assertEquals(module.getName(), name);
		assertEquals(module.getChildren().length, numChildren);
	}
	
	protected void checkNode(ASTNeoNode module, String name) {
		assertEquals(module.toStringAsElement(), name);
		assertTrue(module.hasNoSourceRangeInfo() == false);
	}
	
	protected DeclarationImport checkImport(ASTNeoNode node, boolean isPublic, boolean isStatic, int numChildren) {
		if(isPublic) {
			node = node.getChildren()[0];
		}
		DeclarationImport decImport = downCast(node, DeclarationImport.class);
		assertEquals(decImport.isStatic, isStatic);
		assertEquals(decImport.imports.size(), numChildren);
		return decImport;
	}
	
	protected void checkSelectiveImportFragment(ImportFragment importFragment, String... array) {
		checkNode(importFragment.moduleRef, "std.foo");
		
		ImportSelective decImport = downCast(importFragment, ImportSelective.class);
		assertEqualArrays(array, strmap(decImport.impSelFrags, fnDefUnitToStringAsElement(0)));
	}
	
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		testParseInvalidSyntax(
				"module foo;" +
				"static import std.xpto, std.foo : selec1, selec1 : mod;");
	}
	
}
