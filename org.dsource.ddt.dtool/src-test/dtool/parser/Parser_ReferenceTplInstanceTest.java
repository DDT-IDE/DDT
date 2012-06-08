package dtool.parser;


import org.junit.Test;

import dtool.DToolBundle;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.MixinContainer;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.NamedMixin;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public class Parser_ReferenceTplInstanceTest extends Parser_Reference_CommonTest {
	
	private static final String TI_ARGS1_STRING = "!(int, pack.bar.other)";
	private static final Reference[] TI_ARGS1 = array(reference("int"), reference("pack","bar","other"));
	
	protected String rawTplName;
	protected Reference[] args;
	protected String argsString;
	
	public Parser_ReferenceTplInstanceTest() {
		// defaults
		argsString = TI_ARGS1_STRING;
		args = TI_ARGS1;
	}
	
	protected void runCommonTestWithFixtureTplArgs(final String rawTplCode) {
		this.rawTplName = rawTplCode;
		runCommonTest(rawTplCode + argsString);
	}
	
	@Override
	protected void checkReference(Reference ref, String nodeCode) {
		super.checkReference(ref, nodeCode);
		RefTemplateInstance refTplInst = downCast(ref, RefTemplateInstance.class);
		if(checkToStringAsElement) {
			assertEquals(refTplInst.refRawTemplate.toStringAsElement(), rawTplName);
		}
		checkEqualAsElement(refTplInst.tiargs.getInternalArray(), args);
		checkParent(refTplInst, refTplInst.refRawTemplate);
		checkParent(refTplInst, refTplInst.tiargs.getInternalArray());
	}
	
	@Override
	protected void testCommonScenarios(String refCodeFragment) {
		super.testCommonScenarios(refCodeFragment);
		commonMixinsTest(refCodeFragment); // Should we pull this up into ReferenceCommon?
	}
	
	protected void commonMixinsTest(String refCodeFragment) {
		if(fragmentDesc == RefFragmentDesc.TYPE) 
			return; // Not applicable
		
		DeeParserSession parseResult = parseCode(
			"mixin "+refCodeFragment+";" +
			"mixin "+refCodeFragment+" myMixin; " +
			wrapFunction("mixin "+refCodeFragment+";") +
			wrapFunction("mixin "+refCodeFragment+" myMixin;") +
			""
		);
		
		if(!fragmentDesc.isInvalidSyntax()) {
			checkCommonMixinsTest(refCodeFragment, parseResult.getParsedModule());
		}
	}
	
	protected void checkCommonMixinsTest(String refCodeFragment, Module module) {
		MixinContainer mixin = downCast(module.getChildren()[0]);
		DefinitionFunction fn = downCast(module.getChildren()[0+2]);
		checkReference(mixin.type, refCodeFragment);
		mixin = downCast(fn.fbody.getChildren()[0]);
		checkReference(mixin.type, refCodeFragment);
		
		checkNamedMixin(refCodeFragment, module);
	}
	
	protected void checkNamedMixin(String refCodeFragment, Module module) {
		NamedMixin namedMixin = downCast(module.getChildren()[1]);
		DefinitionFunction fn = downCast(module.getChildren()[1+2]);
		checkReference(namedMixin.type, refCodeFragment);
		namedMixin = downCast(fn.fbody.getChildren()[0]);
		checkReference(namedMixin.type, refCodeFragment);
	}
	
	@Test
	public void testParseBasic() {
		runCommonTestWithFixtureTplArgs("foo");
		runCommonTestWithFixtureTplArgs("pack.foo");
		runCommonTestWithFixtureTplArgs("pack.subpack.foo");
	}
	
	@Test
	public void testParseQualifiedTplInstances() {
		runCommonTestWithFixtureTplArgs("pack.subtpl!(int).foo");
		runCommonTestWithFixtureTplArgs("subtpl!(int, argTpl!()).tpl2!(int).foo");
		runCommonTestWithFixtureTplArgs("subtpl!(argTpl!(int), foo).tpl2.foo");
		runCommonTestWithFixtureTplArgs("subtpl!(argTpl!(foo), int).tpl2.foo");
		runCommonTestWithFixtureTplArgs("subtpl!(int).foo");
	}
	
	@Test
	public void testParseEmptyargs() {
		argsString = "!()";
		args = new Reference[0];
		testParseBasic();
		testParseQualifiedTplInstances();
	}
	
	@Test
	public void test_ShortArgSyntax() throws Exception { test_ShortArgSyntax$(); }
	public void test_ShortArgSyntax$() throws Exception {
		runShortArgSyntaxTest("foo", reference("int"), "foo ! int", "foo!(int)");
		runShortArgSyntaxTest("foo", reference("bar"), "foo!bar", "foo!(bar)");
		if(DToolBundle.UNSUPPORTED_DMD_FUNCTIONALITY){
			runShortArgSyntaxTest("pack.foo", reference("bar"), "pack.foo!bar", "pack.foo!(bar)");
		}
	}
	protected static void runShortArgSyntaxTest(String rawTplRef, Reference tiArg, String refCodeFragment, 
			final String codeToString) {
		Parser_ReferenceTplInstanceTest test = new Parser_ReferenceTplInstanceTest() {
			@Override
			protected void checkReference(Reference ref, String nodeCode) {
				super.checkReference(ref, codeToString);
			}
		};
		test.rawTplName = rawTplRef;
		test.args = array(tiArg);
		test.runCommonTest(refCodeFragment);
	}
	
	@Test
	public void test_CombinedSamples() throws Exception { test_CombinedSamples$(); }
	public void test_CombinedSamples$() throws Exception {
		Parser_ReferenceTplInstanceTest xTest = new Parser_ReferenceTplInstanceTest() {
			@Override
			protected void runCommonTestWithFixtureTplArgs(String rawTplCode) {
				checkToStringAsElement = true;
				for (String refCodeFragment : Parser_Reference_AllTest.sampleStaticRefs) {
					super.runCommonTestWithFixtureTplArgs(refCodeFragment + "." + rawTplCode);
				}
				checkToStringAsElement = false;
				for (String refCodeFragment : Parser_Reference_AllTest.sampleStaticRefs_nonCanonical) {
					super.runCommonTestWithFixtureTplArgs(refCodeFragment + "." + rawTplCode);
				}
			}
			
			@Override
			protected DeeParserSession parseCode(String testCode, Boolean expectedSyntaxErrors) {
				DeeParserSession parseResult = super.parseCode(testCode, DONT_CHECK_ERRORS);
				if(parseResult.hasSyntaxErrors()){
					// We don't know if the resulting fragment is valid or not, so just take the word of the parser
					fragmentDesc = RefFragmentDesc.INVALIDSYNTAX;
				}
				return parseResult;
			}
		};
		xTest.testParseBasic();
		xTest.testParseQualifiedTplInstances();
		xTest.testParseEmptyargs();
	}
	
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		runCommonTestForInvalidFragment("subtpl!(int");
		runCommonTestForInvalidFragment("subtpl!(int, foo).");
		runCommonTestForInvalidFragment(".subtpl!(int, foo).");
		runCommonTestForInvalidFragment("subtpl!(subtpl!(foo)");
		runCommonTestForInvalidFragment("subtpl!subtpl!foo)");
	}
	
}
