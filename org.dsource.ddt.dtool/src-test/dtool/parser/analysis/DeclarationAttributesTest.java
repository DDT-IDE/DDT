package dtool.parser.analysis;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribProtection.Protection;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.tests.CommonTestUtils;
import dtool.tests.MiscNodeUtils;

public class DeclarationAttributesTest extends CommonTestUtils {
	
	@Test
	public void testAttribs() throws Exception { testAttribs$(); }
	public void testAttribs$() throws Exception {
		checkDef(getDefToTest("static public abstract int foo;", "foo"), 
			Protection.PUBLIC, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		
		checkDef(getDefToTest("private public abstract int foo;", "foo"), 
			Protection.PUBLIC, AttributeKinds.ABSTRACT);
		
		checkDef(getDefToTest("abstract abstract int foo;", "foo"), 
			null, AttributeKinds.ABSTRACT);
		
		checkDef(getDefToTest("protected: static: private abstract int foo;", "foo"), 
			Protection.PRIVATE, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		
		checkDef(getDefToTest("protected: static: public private abstract int foo;", "foo"), 
			Protection.PRIVATE, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		
		String sourceA = "override int foo0; protected: static: private abstract int foo1; \n"+ 
			"public: immutable: int foo2"; 
		checkDef(getDefToTest(sourceA, "foo0"), 
			null, AttributeKinds.OVERRIDE);
		checkDef(getDefToTest(sourceA, "foo1"), 
			Protection.PRIVATE, AttributeKinds.ABSTRACT, AttributeKinds.STATIC);
		checkDef(getDefToTest(sourceA, "foo2"), 
			Protection.PUBLIC, AttributeKinds.STATIC, AttributeKinds.IMMUTABLE);
	}
	
	public CommonDefinition getDefToTest(String source, String name) {
		Module module = DeeParser.parseSource(source, "attribs_test").module;
		ASTNode node = MiscNodeUtils.searchDefUnit(module, name, ASTNodeTypes.DECLARATION_ATTRIB);
		CommonDefinition def = assertCast(node, CommonDefinition.class);
		assertEquals(def.getName(), name);
		return def;
	}
	
	public void checkDef(CommonDefinition def, Protection protection, AttributeKinds... expectedAttribs) {
		assertTrue(def.getProtection() == protection);
		for (AttributeKinds attrib : AttributeKinds.values()) {
			assertTrue(def.hasAttribute(attrib) == ArrayUtil.contains(expectedAttribs, attrib));
		}
	}
	
}