package dtool.parser.analysis;

import static dtool.tests.MiscNodeUtils.getNodeFromTreePath;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.arrayI;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribProtection.Protection;
import dtool.ast.definitions.Definition;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.tests.CommonTestUtils;

public class DeclarationAttributesTest extends CommonTestUtils {
	
	@Test
	public void testAttribs() throws Exception { testAttribs$(); }
	public void testAttribs$() throws Exception {
		checkDef(getDefToTest("static public abstract int foo;", "foo", arrayI(0, 0, 0, 0)), 
			Protection.PUBLIC, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		
		checkDef(getDefToTest("abstract abstract int foo;", "foo", arrayI(0, 0, 0)), 
			null, AttributeKinds.ABSTRACT);
		
		checkDef(getDefToTest("protected: static: private abstract int foo;", 
			"foo", arrayI(0, 0, 0, 0, 0)), 
			Protection.PRIVATE, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		
		String sourceA = "override int foo0; protected: static: private abstract int foo1; \n"+ 
			"public: immutable: int foo2"; 
		checkDef(getDefToTest(sourceA, "foo0", arrayI(0, 0)), 
			null, AttributeKinds.OVERRIDE);
		checkDef(getDefToTest(sourceA, "foo1", arrayI(1, 0, 0, 0, 0)), 
			Protection.PRIVATE, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		checkDef(getDefToTest(sourceA, "foo2", arrayI(1, 0, 1, 0, 0)), 
			Protection.PUBLIC, AttributeKinds.STATIC, AttributeKinds.IMMUTABLE);
	}
	
	public Definition getDefToTest(String source, String name, int... treePath) {
		Module module = DeeParser.parseSource(source, "attribs_test").module;
		Definition def = assertCast(getNodeFromTreePath(module, treePath), Definition.class);
		assertEquals(def.getName(), name);
		return def;
	}
	
	public void checkDef(Definition def, Protection protection, AttributeKinds... expectedAttribs) {
		assertTrue(def.getProtection() == protection);
		for (AttributeKinds attrib : AttributeKinds.values()) {
			assertTrue(def.hasAttribute(attrib) == ArrayUtil.contains(expectedAttribs, attrib));
		}
	}
	
}