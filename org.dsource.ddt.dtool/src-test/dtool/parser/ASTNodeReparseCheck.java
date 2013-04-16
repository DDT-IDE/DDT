package dtool.parser;

import static dtool.util.NewUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefQualified;
import dtool.parser.AbstractParser.NodeResult;
import dtool.parser.DeeParserTest.CheckSourceEquality;
import dtool.parser.DeeParser_Decls.ParseRule_Parameters.AmbiguousParameter;
import dtool.parser.DeeParser_Decls.TplOrFnMode;
import dtool.tests.DToolTests;

public class ASTNodeReparseCheck {
	
	protected static final Void VOID = null;
	
	protected final String fullSource;
	protected final ASTNeoNode nodeUnderTest;
	
	public ASTNodeReparseCheck(String source, ASTNeoNode node) {
		this.fullSource = assertNotNull_(source);
		this.nodeUnderTest = node;
	}
	
	protected String nodeSnippedSource;
	protected DeeParser snippedParser;
	
	@SuppressWarnings("deprecation")
	public Void doCheck() {
		assertTrue(nodeUnderTest.getNodeType() != ASTNodeTypes.OTHER);
		
		if(DToolTests.TESTS_LITE_MODE) {
			return VOID;
		}
		
		prepSnippedParser(nodeUnderTest);
		
		switch (nodeUnderTest.getNodeType()) {
		
		case SYMBOL:
			if(nodeUnderTest instanceof DefSymbol) {
				return reparseCheck(snippedParser.parseSymbol(), Symbol.class, false);
			}
			return reparseCheck(snippedParser.parseSymbol());
		
		case SIMPLE_LAMBDA_DEFUNIT:
		case DEF_UNIT:
			assertFail();
		
		case MODULE:
			Module module = (Module) nodeUnderTest;
			assertTrue(module.getStartPos() == 0 && module.getEndPos() == fullSource.length());
			return VOID;
		case DECL_MODULE:
			return reparseCheck(snippedParser.parseModuleDeclaration());
		case DECL_IMPORT:
			return reparseCheck(snippedParser.parseImportDeclaration());
		case IMPORT_CONTENT:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment());
		case IMPORT_ALIAS:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment());
		case IMPORT_SELECTIVE:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment());
		case IMPORT_SELECTIVE_ALIAS:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection());
		
		case DECL_EMTPY:
			return reparseCheck(snippedParser.parseDeclaration());
		case DECL_INVALID:
			return reparseCheck(snippedParser.parseDeclaration());
		case INVALID_SYNTAX:
			return reparseCheck(snippedParser.parseDeclaration());
		case NODE_LIST: 
			// Dont reparse Nodelist since there are two kinds of this (single and multi) 
			// and we dont know which one to parse TODO
			return VOID;
			
		
		/* ---------------------------------- */
		
		case REF_IMPORT_SELECTION:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection());
		case REF_MODULE:
			return reparseCheck(snippedParser.parseImportFragment().getModuleRef());
		case REF_IDENTIFIER: {
			RefIdentifier refId = (RefIdentifier) nodeUnderTest;
			return reparseCheck(snippedParser.parseRefIdentifier(), DeeParser.isMissing(refId));
		}
		case REF_QUALIFIED: {
			RefQualified refQual = (RefQualified) nodeUnderTest;
			if(refQual.isExpressionQualifier) {
				return reparseCheck(((ExpReference) snippedParser.parseExpression().node).ref);
			} else {
				return reparseCheck(snippedParser.parseTypeReference());
			}
		}
		case REF_MODULE_QUALIFIED:
		case REF_PRIMITIVE:
			
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
		case REF_TYPE_FUNCTION:
		case REF_TEMPLATE_INSTANCE:
		case REF_TYPEOF:
		case REF_MODIFIER: {
			reparseCheck(snippedParser.parseTypeReference());
			return VOID;
		}
		case REF_AUTO_RETURN:
			return simpleReparseCheck("auto");
		
		/* ---------------------------------- */
		
		case MISSING_EXPRESSION:
			return simpleReparseCheck("");
		case EXP_REF_RETURN:
			return simpleReparseCheck("return");
		case EXP_THIS:
		case EXP_SUPER:
		case EXP_NULL:
		case EXP_ARRAY_LENGTH:
		case EXP_LITERAL_BOOL:
		case EXP_LITERAL_INTEGER:
		case EXP_LITERAL_STRING:
		case EXP_LITERAL_CHAR:
		case EXP_LITERAL_FLOAT:
			
		case EXP_LITERAL_ARRAY:
		case EXP_LITERAL_MAPARRAY:
			
		case EXP_FUNCTION_LITERAL:
		case EXP_LAMBDA:
		case EXP_SIMPLE_LAMBDA:
		
		case EXP_ASSERT:
		case EXP_MIXIN_STRING:
		case EXP_IMPORT_STRING:
		case EXP_TYPEID:
		
		case EXP_INDEX:
		case EXP_SLICE:
		case EXP_CALL:
		
		case EXP_PREFIX:
		case EXP_NEW:
		case EXP_CAST:
		case EXP_CAST_QUAL:

		case EXP_POSTFIX_OP:
		case EXP_INFIX:
		case EXP_CONDITIONAL:
			return reparseCheck(snippedParser.parseExpression());
		case EXP_PARENTHESES: {
			ExpParentheses expParentheses = (ExpParentheses) nodeUnderTest;
			if(expParentheses.isDotAfterParensSyntax) {
				prepSnippedParser(snippedSource(nodeUnderTest) + ".foo");
				Expression reparsedFix = snippedParser.parseExpression().node;
				RefQualified ref = (RefQualified) (assertCast(reparsedFix, ExpReference.class)).ref;
				// Dont do full check
				checkNodeEquality(ref.qualifier, nodeUnderTest);
				// TODO
				//return reparseCheck(ref.qualifier, node);
				return VOID;
			} else {
				return reparseCheck(snippedParser.parseExpression());
			}
		}
		case EXP_REFERENCE: {
			assertEquals(nodeUnderTest.getSourceRange(), ((ExpReference) nodeUnderTest).ref.getSourceRange());
			return VOID;
		}
		
		case MAPARRAY_ENTRY:
			MapArrayLiteralKeyValue mapArrayEntry = (MapArrayLiteralKeyValue) nodeUnderTest;
			assertEquals(mapArrayEntry.getSourceRange(),
				SourceRange.srStartToEnd(mapArrayEntry.key.getStartPos(),
					(mapArrayEntry.value == null ? mapArrayEntry.key : mapArrayEntry.value).getEndPos()));
			return VOID;
			
		/* -------------------  Declarations  ------------------- */
		case DECL_LINKAGE:
			return reparseCheck(snippedParser.parseDeclarationExternLinkage());
		case DECL_ALIGN:
			return reparseCheck(snippedParser.parseDeclarationAlign());
		case DECL_PRAGMA:
			return reparseCheck(snippedParser.parseDeclarationPragma());
		case DECL_PROTECTION:
			return reparseCheck(snippedParser.parseDeclarationProtection());
		case DECL_BASIC_ATTRIB:
			return reparseCheck(snippedParser.parseDeclarationBasicAttrib());
		
		
		case DECL_MIXIN_STRING:
			return reparseCheck(snippedParser.parseDeclarationMixinString());
		case DECL_MIXIN:
			return reparseCheck(snippedParser.parseDeclarationMixin());
		
		/* ---------------------------------- */
		
		case DEFINITION_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration());
		case DEFINITION_VAR_FRAGMENT:
			return reparseCheck(snippedParser.parseVarFragment(false));
		case DEFINITION_AUTO_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration(true, true));
		case INITIALIZER_EXP:
			InitializerExp initializerExp = (InitializerExp) nodeUnderTest;
			Resolvable initExpExp = initializerExp.exp;
			return reparseCheck(snippedParser.parseInitializer(), initExpExp instanceof MissingExpression);
			
		case DEFINITION_FUNCTION:
			return reparseCheck(snippedParser.parseDeclaration());
		case FUNCTION_PARAMETER:
		case NAMELESS_PARAMETER:
		case VAR_ARGS_PARAMETER:
			return functionParamReparseCheck();
			//return simpleReparseCheck(node, "...");
			
		case DEFINITION_TEMPLATE:
			return reparseCheck(snippedParser.parseTemplateDefinition());
			
		case TEMPLATE_TYPE_PARAM:
		case TEMPLATE_VALUE_PARAM:
		case TEMPLATE_ALIAS_PARAM:
		case TEMPLATE_TUPLE_PARAM:
		case TEMPLATE_THIS_PARAM:
			return templateParamReparseCheck();
			
		case NAMED_MIXIN:
			return reparseCheck(snippedParser.parseDeclarationMixin());
			
		/* -------------------  Statements  ------------------- */
		case BLOCK_STATEMENT:
			return reparseCheck(snippedParser.parseBlockStatement_toMissing().node);
		case STATEMENT_EMTPY_BODY:
			return simpleReparseCheck(";");
		case FUNCTION_BODY:
		case IN_OUT_FUNCTION_BODY:
			return reparseCheck(snippedParser.parseFunctionBody());
		case FUNCTION_BODY_OUT_BLOCK:
			return reparseCheck(snippedParser.parseOutBlock().node);
			
		case OTHER: break;
		}
		throw assertFail();
	}
	
	public Void simpleReparseCheck(String expectedCode) {
		CheckSourceEquality.assertCheck(nodeUnderTest.toStringAsCode(), expectedCode);
		return VOID;
	}
	
	protected Void functionParamReparseCheck() {
		return testParameter(true, (ASTNeoNode) resetSnippedParser().parseFunctionParameter());
	}
	
	protected Void templateParamReparseCheck() {
		return testParameter(false, snippedParser.parseTemplateParameter());
	}
	
	protected Void testParameter(boolean isFunction, ASTNeoNode reparsedNonAmbig) {
		reparseCheck(reparsedNonAmbig);
		
		Object fromAmbig = snippedParser.new ParseRule_Parameters(TplOrFnMode.AMBIG).parseParameter();
		boolean isAmbig = false;
		if(fromAmbig instanceof AmbiguousParameter) {
			isAmbig = true;
			AmbiguousParameter ambiguousParameter = (AmbiguousParameter) fromAmbig;
			fromAmbig = isFunction ? ambiguousParameter.convertToFunction() : ambiguousParameter.convertToTemplate(); 
		}
		checkNodeEquality((ASTNeoNode) fromAmbig, nodeUnderTest);
		resetSnippedParser();
		
		ASTNeoNode paramParsedTheOtherWay = isFunction ? 
			snippedParser.parseTemplateParameter() : (ASTNeoNode) snippedParser.parseFunctionParameter();
		
		assertTrue(allSourceParsedCorrectly() ? isAmbig : true);
		if(allSourceParsedCorrectly()) {
			CheckSourceEquality.assertCheck(paramParsedTheOtherWay.toStringAsCode(), nodeUnderTest.toStringAsCode());
		}
		resetSnippedParser();
		return VOID;
	}
	
	public boolean allSourceParsedCorrectly() {
		return snippedParser.lookAhead() == DeeTokens.EOF && snippedParser.errors.isEmpty();
	}
	
	public void prepSnippedParser(ASTNeoNode node) {
		prepSnippedParser(snippedSource(node));
	}
	
	public String snippedSource(ASTNeoNode node) {
		return fullSource.substring(node.getStartPos(), node.getEndPos());
	}
	
	public void prepSnippedParser(String snippedSource) {
		nodeSnippedSource = snippedSource;
		resetSnippedParser();
	}
	
	public DeeParser resetSnippedParser() {
		return snippedParser = new DeeParser(new DeeParserTest.DeeTestsLexer(nodeSnippedSource));
	}
	
	public Void reparseCheck(NodeResult<? extends ASTNeoNode> result) {
		return reparseCheck(result.getNode());
	}
	public Void reparseCheck(IASTNeoNode reparsedNode) {
		boolean consumesTrailingWhiteSpace = consumeTrailingWhiteSpace(nodeUnderTest);
		return reparseCheck((ASTNeoNode) reparsedNode, nodeUnderTest.getClass(), consumesTrailingWhiteSpace);
	}
	
	public boolean consumeTrailingWhiteSpace(final ASTNeoNode node) {
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				return true;
			}
		}
		return false;
	}
	
	public Void reparseCheck(ASTNeoNode reparsedNode, boolean consumesTrailingWhiteSpace) {
		return reparseCheck(reparsedNode, nodeUnderTest.getClass(), consumesTrailingWhiteSpace);
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public Void reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass, 
		boolean consumesAllTrailingWhiteSpace)
	{
		// Must have consumed all input
		assertTrue(snippedParser.lookAhead() == DeeTokens.EOF);
		assertTrue(snippedParser.getLexPosition() == snippedParser.getSource().length());
		
		assertNotNull_(reparsedNode);
		assertTrue(reparsedNode.getClass() == klass);
		checkNodeEquality(reparsedNode, nodeUnderTest);
		// TODO check errors are the same?
		
		// Check source ranges:
		assertEquals(new SourceRange(0, nodeSnippedSource.length()), reparsedNode.getSourceRange());
		
		LexElement firstLexElement = firstLexElementInSource(snippedParser.getSource());
		assertTrue(firstLexElement.precedingSubChannelTokens == null || firstLexElement.token.type == DeeTokens.EOF);
		
		if(consumesAllTrailingWhiteSpace) {
			// Check that the range contains all possible whitespace
			assertTrue(lexElementAfterSnippedRange(nodeUnderTest).getStartPos() == 0);
		}
		
		resetSnippedParser();
		return VOID;
	}
	
	public static LexElement firstLexElementInSource(String source) {
		return new LexElementProducer().produceLexElement(new DeeLexer(source));
	}
	
	public LexElement lexElementAfterSnippedRange(ASTNeoNode node) {
		return firstLexElementInSource(fullSource.substring(node.getEndPos()));
	}
	
	public static void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
}