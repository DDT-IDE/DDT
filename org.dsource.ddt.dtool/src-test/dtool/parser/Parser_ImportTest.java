package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.RefModule;
import dtool.tests.MiscDeeTestUtils;

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
		checkImportFragment(child1.imports.get(0), "pack.bar");
		
		
		DeclarationImport child2 = checkImport(module.getChildren()[2], true, false, 1);
		checkImportFragment(child2.imports.get(0), "pack");
		
		
		DeclarationImport child3 = checkImport(module.getChildren()[3], true, true, 3);
		checkImportFragment(child3.imports.get(0), "std.xpto");
		checkImportFragment(child3.imports.get(1), "pack");
		checkImportFragment(child3.imports.get(2), "blah.blah");
		
		
		DeclarationImport child4 = checkImport(module.getChildren()[4], false, false, 2);
		checkImportFragment(child4.imports.get(0), "asdf");
		checkImportFragment(child4.imports.get(1), "std.foo");
		checkSelectiveImportFragment(child4.imports.get(1), "selec1", "selec2");
		
	}
	public void checkImportFragment(IImportFragment fragment, String name) {
		checkNode(fragment.getModuleRef(), name);
	}
	
	protected void checkDefunit(DefUnit module, String name, Integer numChildren) {
		assertEquals(module.getName(), name);
		assertEquals(module.getChildren().length, numChildren);
	}
	
	protected void checkNode(RefModule module, String name) {
		assertEquals(module.toStringAsCode(), name);
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
	
	protected void checkSelectiveImportFragment(IImportFragment importFragment, String... array) {
		checkNode(importFragment.getModuleRef(), "std.foo");
		
		ImportSelective decImport = downCast(importFragment, ImportSelective.class);
		assertEqualArrays(array, strmap(decImport.impSelFrags, MiscDeeTestUtils.fnDefUnitToStringAsCode()));
	}
	
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		testParseInvalidSyntax(
				"module foo;" +
				"static import std.xpto, std.foo : selec1, selec1 : mod;");
	}
	
}
