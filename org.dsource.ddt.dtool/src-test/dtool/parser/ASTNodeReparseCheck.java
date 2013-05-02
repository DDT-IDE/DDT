package dtool.parser;

import static dtool.util.NewUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.DefinitionEnum.NoEnumBody;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpParentheses;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InitializerArray.ArrayInitEntry;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct.StructInitEntry;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.parser.AbstractParser.NodeResult;
import dtool.parser.AbstractParser.ParseHelper;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
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
	
	
	public Void basicSourceRangeCheck() {
		
		LexElement firstLexElement = firstLexElementInSource(fullSource.substring(nodeUnderTest.getStartPos()));
		assertTrue(firstLexElement.precedingSubChannelTokens == null || canBeginWithEmptySpace(nodeUnderTest));
		
		if(nodeConsumesTrailingWhiteSpace(nodeUnderTest)) {
			// Check that the range contains all possible whitespace
			assertTrue(lexElementAfterSnippedRange(nodeUnderTest).getStartPos() == 0);
		}
		
		return VOID;
	}
	
	public static LexElement firstLexElementInSource(String source) {
		return new LexElementProducer().produceLexElement(new DeeLexer(source));
	}
	
	public LexElement lexElementAfterSnippedRange(ASTNeoNode node) {
		return firstLexElementInSource(fullSource.substring(node.getEndPos()));
	}
	
	public static boolean canBeginWithEmptySpace(final ASTNeoNode node) {
		if(node instanceof Module) {
			return true;
		}
		else if(node instanceof NodeList2) {
			return true;
		}
		else if(node instanceof RefIdentifier || node instanceof RefImportSelection) {
			return DeeParser.isMissing((Reference) node);
		} 
		else if(node instanceof MissingExpression) {
			return true;
		} 
		else if(node instanceof InitializerExp) {
			return ((InitializerExp) node).exp instanceof MissingExpression;
		} 
		else if(node instanceof StructInitEntry) {
			StructInitEntry initEntry = (StructInitEntry) node;
			return canBeginWithEmptySpace(initEntry.member != null ? initEntry.member : initEntry.value);
		} 
		else if(node instanceof ArrayInitEntry) {
			ArrayInitEntry initEntry = (ArrayInitEntry) node;
			return canBeginWithEmptySpace(initEntry.index != null ? initEntry.index : initEntry.value);
		}
		else if(node instanceof MapArrayLiteralKeyValue) {
			MapArrayLiteralKeyValue mapArrayEntry = (MapArrayLiteralKeyValue) node;
			return canBeginWithEmptySpace(mapArrayEntry.key);
		}
		else if(node instanceof MissingParenthesesExpression) {
			return true;
		}
		else if(node instanceof BlockStatement) {
			BlockStatement blockStatement = (BlockStatement) node;
			return blockStatement.statements == null;
		}
		
		return false;
	}
	
	
	public static boolean nodeConsumesTrailingWhiteSpace(final ASTNeoNode node) {
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				return true;
			}
		}
		if(node instanceof MissingExpression) {
			//return true; // TODO, require TypeOrExp parse changes
		}
		if(node instanceof RefIdentifier) {
			RefIdentifier refId = (RefIdentifier) node;
			return DeeParser.isMissing(refId); 
		}
		if(node instanceof InitializerExp) {
			InitializerExp initializerExp = (InitializerExp) node;
			return initializerExp.exp instanceof MissingExpression;
		}
		if(node instanceof DefSymbol) {
			return false;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public Void doCheck() {
		assertTrue(nodeUnderTest.getNodeType() != ASTNodeTypes.OTHER);
		
		basicSourceRangeCheck();
		
		if(DToolTests.TESTS_LITE_MODE) {
			return VOID;
		}
		
		prepSnippedParser(nodeUnderTest);
		
		switch (nodeUnderTest.getNodeType()) {
		
		case SYMBOL:
			if(nodeUnderTest instanceof DefSymbol) {
				return reparseCheck(snippedParser.parseSymbol(), Symbol.class);
			}
			return reparseCheck(snippedParser.parseSymbol());
		
		case SIMPLE_LAMBDA_DEFUNIT:
			return VOID; // dont reparse test
		
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
			return reparseCheck(snippedParser.parseRefIdentifier());
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
			if(nodeUnderTest instanceof MissingParenthesesExpression) {
				ParseHelper parse = snippedParser.new ParseHelper();
				return reparseCheck(snippedParser.parseExpressionAroundParentheses(parse, true));
			}
			return reparseCheck(snippedParser.parseExpression_toMissing());
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
		case EXP_NEW_ANON_CLASS:
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
		case DECL_LINKAGE: return reparseCheck(snippedParser.parseDeclarationExternLinkage());
		case DECL_ALIGN: return reparseCheck(snippedParser.parseDeclarationAlign());
		case DECL_PRAGMA: return reparseCheck(snippedParser.parseDeclarationPragma());
		case DECL_PROTECTION: return reparseCheck(snippedParser.parseDeclarationProtection());
		case DECL_BASIC_ATTRIB: return reparseCheck(snippedParser.parseDeclarationBasicAttrib());
		
		case DECL_MIXIN_STRING: return reparseCheck(snippedParser.parseDeclarationMixinString());
		case DECL_MIXIN: return reparseCheck(snippedParser.parseDeclarationMixin());
		
		case DECL_ALIAS_THIS: return reparseCheck(snippedParser.parseDeclarationAliasThis());
		
		case DECLARATION_INVARIANT: return reparseCheck(snippedParser.parseDeclarationInvariant_start());
		case DECLARATION_ALLOC_DEALLOC: return reparseCheck(snippedParser.parseDeclarationAllocators());

		
		/* ---------------------------------- */
		
		case DEFINITION_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration());
		case DEFINITION_VAR_FRAGMENT:
			return reparseCheck(snippedParser.parseVarFragment(false));
		case DEFINITION_AUTO_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration(true, true));
		case INITIALIZER_EXP:
			return reparseCheck(snippedParser.parseInitializer().node);
		case INITIALIZER_VOID:
		case INITIALIZER_ARRAY:
			return reparseCheck(snippedParser.parseInitializer());
		case INITIALIZER_STRUCT:
			return reparseCheck(snippedParser.parseStructInitializer());
		case STRUCT_INIT_ENTRY:
			return reparseCheck(snippedParser.new ParseStructInitEntry().parseElement(true));
		case ARRAY_INIT_ENTRY:
			return reparseCheck(snippedParser.new ParseArrayInitEntry().parseElement(true));
			
		case DEFINITION_FUNCTION:
			return reparseCheck(snippedParser.parseDeclaration());
		case FUNCTION_PARAMETER:
		case NAMELESS_PARAMETER:
		case VAR_ARGS_PARAMETER:
			return functionParamReparseCheck();
			//return simpleReparseCheck(node, "...");
		case DEFINITION_ENUM:
			return reparseCheck(snippedParser.parseDefinitionEnum_start());
		case DECLARATION_ENUM:
			return reparseCheck(snippedParser.parseDeclarationEnum_start());
		case ENUM_BODY:
			if(nodeUnderTest instanceof NoEnumBody) return VOID;
			return reparseCheck(snippedParser.parseEnumBody());
		case ENUM_MEMBER:
			return reparseCheck(snippedParser.new ParseEnumMember().parseElement(true));

		case DEFINITION_STRUCT:
			return reparseCheck(snippedParser.parseDefinitionStruct());
		case DEFINITION_UNION:
			return reparseCheck(snippedParser.parseDefinitionUnion());
		case DEFINITION_CLASS:
			return reparseCheck(snippedParser.parseDefinitionClass());
		case DEFINITION_INTERFACE:
			return reparseCheck(snippedParser.parseDefinitionInterface());

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
		case DEFINITION_ALIAS:
		case DEFINITION_ALIAS_DECL:
			return reparseCheck(snippedParser.parseAliasDefinition());
		case DEFINITION_ALIAS_FRAGMENT:
			return reparseCheck(snippedParser.parseAliasFragment());
			
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
	
	
	public static void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
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

	

	
	public Void simpleReparseCheck(String expectedCode) {
		SourceEquivalenceChecker.assertCheck(nodeUnderTest.toStringAsCode(), expectedCode);
		return VOID;
	}
	
	protected Void functionParamReparseCheck() {
		return testParameter(true, snippedParser.parseFunctionParameter().asNode());
	}
	
	protected Void templateParamReparseCheck() {
		return testParameter(false, snippedParser.parseTemplateParameter());
	}
	
	protected Void testParameter(boolean isFunction, ASTNeoNode reparsedNonAmbig) {
		reparseCheck(reparsedNonAmbig);
		
		Object fromAmbig = new DeeParser_RuleParameters(snippedParser, TplOrFnMode.AMBIG).parseParameter();
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
		
		boolean hasFullyParsedCorrectly = allSourceParsedCorrectly(snippedParser, paramParsedTheOtherWay);
		
		assertTrue(hasFullyParsedCorrectly ? isAmbig : true);
		if(hasFullyParsedCorrectly) {
			String expectedSource = nodeUnderTest.toStringAsCode();
			SourceEquivalenceChecker.assertCheck(paramParsedTheOtherWay.toStringAsCode(), expectedSource);
		}
		resetSnippedParser();
		return VOID;
	}
	
	public boolean allSourceParsedCorrectly(DeeParser parser, ASTNeoNode resultNode) {
		return parser.lookAhead() == DeeTokens.EOF && resultNode.getData().hasErrors();
	}
	
	public Void reparseCheck(NodeResult<? extends ASTNeoNode> result) {
		return reparseCheck(result.node);
	}
	public Void reparseCheck(IASTNeoNode reparsedNode) {
		assertNotNull_(reparsedNode);
		return reparseCheck(reparsedNode.asNode(), nodeUnderTest.getClass());
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public Void reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass)
	{
		// Must have consumed all input
		assertTrue(snippedParser.lookAhead() == DeeTokens.EOF);
		assertTrue(snippedParser.getLexPosition() == snippedParser.getSource().length());
		
		assertNotNull_(reparsedNode);
		assertTrue(reparsedNode.getClass() == klass);
		// Check source ranges:
		assertEquals(new SourceRange(0, nodeSnippedSource.length()), reparsedNode.getSourceRange());
		checkNodeEquality(reparsedNode, nodeUnderTest);
		// TODO check errors are the same?

		resetSnippedParser();
		return VOID;
	}
	
}