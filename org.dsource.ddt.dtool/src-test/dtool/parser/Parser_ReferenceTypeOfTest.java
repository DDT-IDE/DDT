package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.ast.references.RefTypeof;

public class Parser_ReferenceTypeOfTest extends Parser_Reference_CommonTest {
	
	protected String argCodeString;
	
	
	/*-----------------------------------*/
	
	protected void runCommonTestWithTypeOf(String refCodeFragment, String argCodeString) {
		this.argCodeString = argCodeString;
		runCommonTest(refCodeFragment);
	}
	
	@Override
	protected void checkReference(Reference ref, String nodeCode) {
		super.checkReference(ref, nodeCode);
		assertTrue(ref instanceof RefTypeof);
		Expression expression = downCast(ref, RefTypeof.class).expression;
		assertEquals(expression.toStringAsElement(), argCodeString);
	}
	
	@Test
	public void testBasic2() throws Exception { testBasic2$(); }
	public void testBasic2$() throws Exception {
		runCommonTestWithTypeOf("typeof(foo)", "foo");
		runCommonTestWithTypeOf("typeof(pack.foo)", "pack.foo");
		runCommonTestWithTypeOf("typeof(pack.subpack.foo)", "pack.subpack.foo");
		
		runCommonTestWithTypeOf("typeof(123)", "123");
		runCommonTestWithTypeOf("typeof(\"abc\")", "\"abc\"");
		//runTypeOfTest("typeof(2 + 3)", "123"); //TODO canonical string
	}
	
	
	@Test
	public void test_CombinedSamples() throws Exception { test_CombinedSamples$(); }
	public void test_CombinedSamples$() throws Exception {
		for (String refString : Parser_Reference_AllTest.sampleStaticRefs) {
			runCommonTestWithTypeOf("typeof("+refString+")", refString);
		}
	}
	
	@Test
	public void test_XArg_TemplateInstance() throws Exception { test_XArg_TemplateInstance$(); }
	public void test_XArg_TemplateInstance$() throws Exception {
		Parser_ReferenceTplInstanceTest test = new Parser_ReferenceTplInstanceTest() {
			
			String typeofArgumentCode;
			
			@Override
			protected void runCommonTest(String refCodeFragment, RefFragmentDesc fragmentDesc) {
				typeofArgumentCode = refCodeFragment;
				refCodeFragment = "typeof(" + refCodeFragment + ")";
				super.runCommonTest(refCodeFragment, RefFragmentDesc.TYPE);
			}
			
			@Override
			protected void checkReference(Reference ref, String nodeCode) {
				assertAreEqual(ref.toStringAsElement(), nodeCode);
				assertTrue(ref instanceof RefTypeof);
				Expression expression = downCast(ref, RefTypeof.class).expression;
				super.checkReference(downCast(expression, ExpReference.class).ref, typeofArgumentCode);
			}
		};
		test.testParseBasic();
		test.testParseQualifiedTplInstances();
		test.testParseEmptyargs();
	}
	
	
	
	protected void runCommonTestWithTypeOfPrefix(String nodeCodeString, final String typeofArgCode) {
		nodeCodeString = "typeof("+typeofArgCode+")." + nodeCodeString;
		
		Parser_ReferenceTypeOfTest test = new Parser_ReferenceTypeOfTest() {
			@Override
			protected void checkReference(Reference topRef, String nodeCode) {
				assertAreEqual(topRef.toStringAsElement(), nodeCode);
				Resolvable ref_obj = getRootRef(topRef);
				assertTrue(ref_obj instanceof RefTypeof);
				RefTypeof ref = downCast(ref_obj);
				assertAreEqual(ref.toStringAsElement(), "typeof("+typeofArgCode+")");
				Expression expression = ref.expression;
				assertEquals(downCast(expression, ExpReference.class).ref.toStringAsElement(), typeofArgCode);
			}
		};
		test.runCommonTest(nodeCodeString);
	}
	
	protected static Resolvable getRootRef(Resolvable root) {
		if(root instanceof RefQualified) {
			return getRootRef(downCast(root, RefQualified.class).qualifier);
		}
		if(root instanceof RefTemplateInstance) {
			return getRootRef(downCast(root, RefTemplateInstance.class).tplRef);
		}
		return root;
	}
	
	@Test
	public void testX_asPrefix() throws Exception { testX_asPrefix$(); }
	public void testX_asPrefix$() throws Exception {
		runCommonTestWithTypeOfPrefix("foo", "xxx.yyy");
		runCommonTestWithTypeOfPrefix("pack.foo", "xxx.yyy");
		runCommonTestWithTypeOfPrefix("pack.subpack.foo", "xxx.yyy");
		
		runCommonTestWithTypeOfPrefix("pack.subtpl!(int).foo", "xxx.yyy");
		runCommonTestWithTypeOfPrefix("pack.subtpl.foo!(int)", "xxx.yyy");
	}
	
	@Test
	public void testInvalidSyntax() throws Exception { testInvalidSyntax$(); }
	public void testInvalidSyntax$() throws Exception {
		// runCommonTest1("typeof(foo, bar)"); // This is actually allowed, strangely
		
		runCommonTestForInvalidFragment("typeof(foo");
		runCommonTestForInvalidFragment("typeof(.foo");
		runCommonTestForInvalidFragment("typeof(foo.bar");
		runCommonTestForInvalidFragment("typeof(foo.bar!()");
		
		runCommonTestForInvalidFragment("typeof(foo.)");
		runCommonTestForInvalidFragment("typeof(foo!().)");
	}
	
	@Test
	public void testUnsupportedSyntax() throws Exception { testUnsupportedSyntax$(); }
	public void testUnsupportedSyntax$() throws Exception {
		runCommonTestForUnsupportedFragment("typeof(int)");
		runCommonTestForUnsupportedFragment("typeof(xxx).subtpl!(int)");
		runCommonTestForUnsupportedFragment("typeof(xxx).subtpl!(int).foo");
		runCommonTestForUnsupportedFragment("typeof(x.y).subtpl!(int, foo).tpl2.foo");
	}
	
}
