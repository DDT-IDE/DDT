package dtool.parser;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.references.Reference;

/**
 * Providea a test skeleton for common source test scenarios.
 * Test should be very customizable with inheritance extensions/modifications.
 */
public abstract class Parser_Reference_CommonTest extends Parser__CommonTest {
	
	protected final static Boolean DONT_CHECK_ERRORS = null;
	protected final static boolean NO_ERRORS = false;
	protected final static boolean SYNTAX_ERRORS = true;
	
	protected static enum RefFragmentDesc {
		TYPE(false),
		EXP(false),
		ANY(false),
		INVALIDSYNTAX(true),
		;
		
		final boolean isInvalid;
		
		private RefFragmentDesc(boolean isInvalid) {
			this.isInvalid = isInvalid;
		}
		public boolean isInvalidSyntax() {
			return isInvalid;
		}
	}
	
	/** This signals syntax that is invalid, but that makes sense semantically (if it's orthogonal for example)
	 * and could easily be supported in other future versions of the language. */
	protected final static boolean UNSUPPORTED_SYNTAX = SYNTAX_ERRORS;
	
	protected RefFragmentDesc fragmentDesc;
	protected boolean checkToStringAsElement = true; // This should be part of a checker class
	
	
	protected void runCommonTestForInvalidFragment(String refCodeFragment) {
		runCommonTest(refCodeFragment, RefFragmentDesc.INVALIDSYNTAX);
	}
	protected void runCommonTestForUnsupportedFragment(String refCodeFragment) {
		runCommonTest(refCodeFragment, RefFragmentDesc.INVALIDSYNTAX);
	}
	protected final void runCommonTest(String refCodeFragment) {
		runCommonTest(refCodeFragment, RefFragmentDesc.ANY);
	}
	protected void runCommonTest(String refCodeFragment, RefFragmentDesc fragmentDesc) {
		testCommonScenarios(refCodeFragment, fragmentDesc);
	}
	
	
	protected final DeeParserSession parseCode(String testCode) {
		return parseCode(testCode, fragmentDesc);
	}

	protected final DeeParserSession parseCode(String testCode, RefFragmentDesc fragmentDesc) {
		// If source has errors, don't expect it
		Boolean expectedErrors = (fragmentDesc.isInvalidSyntax()) ? null : false;
		return parseCode(testCode, expectedErrors);
	}
	
	protected DeeParserSession parseCode(String testCode, Boolean expectedErrors) {
		return testParseDo(testCode, expectedErrors);
	}
	
	/** Test the refCodeFragment in a variety of contexts where a ref can appear */
	protected void testCommonScenarios(String refCodeFragment, RefFragmentDesc fragmentDesc) {
		this.fragmentDesc = fragmentDesc;
		testCommonScenarios(refCodeFragment);
	}
	protected void testCommonScenarios(String refCodeFragment) {
		if(fragmentDesc != RefFragmentDesc.EXP) {
			testCaseA(refCodeFragment);
			testCaseAF(refCodeFragment);
			testCaseFnParam(refCodeFragment);
			testCaseFnParam2(refCodeFragment);
		}
		testCaseExp(refCodeFragment);
		testCaseMiscOthers(refCodeFragment);
		
		testAggregate(refCodeFragment);
	}
	
	protected void testCaseA(final String refCodeFragment) {
		Module module = parseCode(refCodeFragment+" myvar;").getParsedModule();
		if(!fragmentDesc.isInvalidSyntax()) {
			checkTestA(refCodeFragment, module.getChildren()[0]);
		}
	}
	protected void testCaseAF(final String refCodeFragment) {
		Module module = parseCode(wrapFunction(refCodeFragment+" myvar;")).getParsedModule();
		if(!fragmentDesc.isInvalidSyntax()) {
			checkTestAF(refCodeFragment, module.getChildren()[0]);
		}
	}
	protected void testCaseExp(final String refCodeFragment) {
		Module module = parseCode("auto dummy = "+refCodeFragment+" * 2; ").getParsedModule();
		if(!fragmentDesc.isInvalidSyntax()) {
			checkTestExp(refCodeFragment, module.getChildren()[0]);
		}
	}
	protected void testCaseFnParam(final String refCodeFragment) {
		Module module = parseCode("void func("+refCodeFragment+" asParameter) {  }").getParsedModule();
		if(!fragmentDesc.isInvalidSyntax()) {
			checkTestFnParam(refCodeFragment, module.getChildren()[0]);
		}
	}
	protected void testCaseFnParam2(final String refCodeFragment) {
		Module module = parseCode("void func("+refCodeFragment+") {  }").getParsedModule();
		if(!fragmentDesc.isInvalidSyntax()) {
			checkTestFnParam2(refCodeFragment, module.getChildren()[0]);
		}
	}
	
	protected void testCaseMiscOthers(final String refCodeFragment) {
		parseCodeWithFnWrap("int myvar = 1 + "+refCodeFragment+";", fragmentDesc.isInvalidSyntax());
		parseCodeWithFnWrap(""+refCodeFragment+"!(int) myvar;", DONT_CHECK_ERRORS);
		parseCodeWithFnWrap("typeof(" +refCodeFragment+ ") myvar;", fragmentDesc.isInvalidSyntax());
		parseCodeWithFnWrap("int myvar = 1 + typeof("+refCodeFragment+").counter;", fragmentDesc.isInvalidSyntax());
		parseCodeWithFnWrap("mixin(" + refCodeFragment + ");", fragmentDesc.isInvalidSyntax());
	}
	
	
	protected static String wrapFunction(String string) {
		return "void func() { " + string + " } ";
	}
	
	private final DeeParserSession parseCodeWithFnWrap(String testCode, Boolean expectedErrors) {
		parseCode(wrapFunction(testCode), expectedErrors);
		return parseCode(testCode, expectedErrors);
	}
	
	protected void checkReference(Reference ref, String nodeCode) {
		if(checkToStringAsElement) {
			assertAreEqual(ref.toStringAsElement(), nodeCode);
		}
		ref.accept(new ASTCommonSourceRangeChecker.ASTSourceRangeChecker(ref.getStartPos()));
	}
	
	protected void checkTestA(final String nodeCode, IASTNode neoNode) {
		DefinitionVariable child = downCast(neoNode);
		checkReference(child.type, nodeCode);
	}
	protected void checkTestAF(final String nodeCode, IASTNode neoNode) {
		DefinitionFunction fn = downCast(neoNode);
		checkTestA(nodeCode, fn.fbody.getChildren()[0]);
	}
	
	protected void checkTestExp(final String nodeCode, IASTNode neoNode) {
		DefinitionVariable child = downCast(neoNode);
		ExpReference expReference = downCast(
			downCast(downCast(child.init, InitializerExp.class).exp, ExpInfix.class).leftExp
		, ExpReference.class);
		checkReference(expReference.ref, nodeCode);
	}
	
	protected void checkTestFnParam(final String nodeCode, IASTNode neoNode) {
		DefinitionFunction fn = downCast(neoNode);
		FunctionParameter fnParam = downCast(fn.params.get(0));
		assertEquals(fnParam.defname.name, "asParameter");
		checkReference(fnParam.type, nodeCode);
	}
	
	protected void checkTestFnParam2(final String nodeCode, IASTNode neoNode) {
		DefinitionFunction fn = downCast(neoNode);
		NamelessParameter fnParam = downCast(fn.params.get(0));
		checkReference(fnParam.type, nodeCode);
	}
	
	protected void testAggregate(final String refCodeFragment) {
		if(fragmentDesc == RefFragmentDesc.EXP)
			return; // Not applicable
		
		String commonTestAggregateString = 
			refCodeFragment+" myvar;" +
			wrapFunction(refCodeFragment+" myvar;") +
			"auto dummy = "+refCodeFragment+" * 2; " + 
			"void func("+refCodeFragment+" asParameter) {  }" + 
			"void func("+refCodeFragment+") {  }" +
			""
		;
		DeeParserSession parseResult = parseCode(commonTestAggregateString);
		if(fragmentDesc != RefFragmentDesc.INVALIDSYNTAX) {
			checkAggregate(refCodeFragment, parseResult.getParsedModule());
		}
	}
	
	protected void checkAggregate(final String refCodeFragment, Module module) {
		checkTestA(refCodeFragment, module.getChildren()[0]);
		checkTestAF(refCodeFragment, module.getChildren()[1]);
		checkTestExp(refCodeFragment, module.getChildren()[2]);
		checkTestFnParam(refCodeFragment, module.getChildren()[3]);
		checkTestFnParam2(refCodeFragment, module.getChildren()[4]);
	}
	
}