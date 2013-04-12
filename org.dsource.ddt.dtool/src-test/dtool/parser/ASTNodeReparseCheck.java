package dtool.parser;

import static dtool.util.NewUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
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
	
	protected final String originalSource;
	
	public ASTNodeReparseCheck(String source) {
		this.originalSource = assertNotNull_(source);
	}
	
	protected String nodeSnippedSource;
	protected DeeParser snippedParser;
	
	@SuppressWarnings("deprecation")
	public Void doCheck(ASTNeoNode node) {
		assertTrue(node.getNodeType() != ASTNodeTypes.OTHER);
		
		if(DToolTests.TESTS_LITE_MODE) {
			return VOID;
		}
		
		prepSnippedParser(node);
		
		switch (node.getNodeType()) {
		
		case SYMBOL:
			if(node instanceof DefSymbol) {
				return reparseCheck(snippedParser.parseSymbol(), Symbol.class, node, false);
			}
			return reparseCheck(snippedParser.parseSymbol(), node);
		case DEF_UNIT:
			assertFail();
		
		case MODULE:
			Module module = (Module) node;
			assertTrue(module.getStartPos() == 0 && module.getEndPos() == originalSource.length());
			return VOID;
		case DECL_MODULE:
			return reparseCheck(snippedParser.parseModuleDeclaration(), node);
		case DECL_IMPORT:
			return reparseCheck(snippedParser.parseImportDeclaration(), node);
		case IMPORT_CONTENT:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment(), node);
		case IMPORT_ALIAS:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment(), node);
		case IMPORT_SELECTIVE:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportFragment(), node);
		case IMPORT_SELECTIVE_ALIAS:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection(), node);
		
		case DECL_EMTPY:
			return reparseCheck(snippedParser.parseDeclaration(), node);
		case DECL_INVALID:
			return reparseCheck(snippedParser.parseDeclaration(), node);
		case INVALID_SYNTAX:
			return reparseCheck(snippedParser.parseDeclaration(), node);
		case NODE_LIST: 
			// Dont reparse Nodelist since there are two kinds of this (single and multi) 
			// and we dont know which one to parse TODO
			return VOID;
			
		
		/* ---------------------------------- */
		
		case REF_IMPORT_SELECTION:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection(), node);
		case REF_MODULE:
			return reparseCheck(snippedParser.parseImportFragment().getModuleRef(), node);
		case REF_IDENTIFIER: {
			RefIdentifier refId = (RefIdentifier) node;
			return reparseCheck(snippedParser.parseRefIdentifier(), node, refId.name == null);
		}
		case REF_QUALIFIED: {
			RefQualified refQual = (RefQualified) node;
			if(refQual.isExpressionQualifier) {
				return reparseCheck(((ExpReference) snippedParser.parseExpression().node).ref, node);
			} else {
				return reparseCheck(snippedParser.parseTypeReference(), node);
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
			reparseCheck(snippedParser.parseTypeReference(), node);
			return VOID;
		}
		case REF_AUTO_RETURN:
			return simpleReparseCheck(node, "auto");
		
		/* ---------------------------------- */
		
		case MISSING_EXPRESSION:
			return simpleReparseCheck(node, "");
		case EXP_REF_RETURN:
			return simpleReparseCheck(node, "return");
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
			return reparseCheck(snippedParser.parseExpression(), node);
		case EXP_PARENTHESES: {
			ExpParentheses expParentheses = (ExpParentheses) node;
			if(expParentheses.isDotAfterParensSyntax) {
				prepSnippedParser(snippedSource(node) + ".foo");
				Expression reparsedFix = snippedParser.parseExpression().node;
				RefQualified ref = (RefQualified) (assertCast(reparsedFix, ExpReference.class)).ref;
				// Dont do full check
				checkNodeEquality(ref.qualifier, node);
				// TODO
				//return reparseCheck(ref.qualifier, node);
				return VOID;
			} else {
				return reparseCheck(snippedParser.parseExpression(), node);
			}
		}
		case EXP_REFERENCE: {
			Resolvable reparsed = snippedParser.parseTypeOrExpression(true).node;
			if(reparsed instanceof ExpReference) {
				reparsed = ((ExpReference) reparsed).ref;
			}
			reparseCheck(reparsed, ((ExpReference) node).ref);
			return VOID;
		}
		
		case MAPARRAY_ENTRY:
			MapArrayLiteralKeyValue mapArrayEntry = (MapArrayLiteralKeyValue) node;
			assertEquals(mapArrayEntry.getSourceRange(),
				SourceRange.srStartToEnd(mapArrayEntry.key.getStartPos(),
					(mapArrayEntry.value == null ? mapArrayEntry.key : mapArrayEntry.value).getEndPos()));
			return VOID;
			
		/* -------------------  Declarations  ------------------- */
		case DECL_LINKAGE:
			return reparseCheck(snippedParser.parseDeclarationExternLinkage(), node);
		case DECL_ALIGN:
			return reparseCheck(snippedParser.parseDeclarationAlign(), node);
		case DECL_PRAGMA:
			return reparseCheck(snippedParser.parseDeclarationPragma(), node);
		case DECL_PROTECTION:
			return reparseCheck(snippedParser.parseDeclarationProtection(), node);
		case DECL_BASIC_ATTRIB:
			return reparseCheck(snippedParser.parseDeclarationBasicAttrib(), node);
		
		
		case DECL_MIXIN_STRING:
			return reparseCheck(snippedParser.parseDeclarationMixinString(), node);
		case DECL_MIXIN:
			return reparseCheck(snippedParser.parseDeclarationMixin(), node);
		
		/* ---------------------------------- */
		
		case DEFINITION_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration(), node);
		case DEFINITION_VAR_FRAGMENT:
			return reparseCheck(snippedParser.parseVarFragment(false), node);
		case DEFINITION_AUTO_VARIABLE:
			return reparseCheck(snippedParser.parseDeclaration(true, true), node);
		case INITIALIZER_EXP:
			InitializerExp initializerExp = (InitializerExp) node;
			Resolvable initExpExp = initializerExp.exp;
			return reparseCheck(snippedParser.parseInitializer(), node, initExpExp instanceof MissingExpression);
			
		case DEFINITION_FUNCTION:
			return reparseCheck(snippedParser.parseDeclaration(), node);
		case FUNCTION_PARAMETER:
		case NAMELESS_PARAMETER:
		case VAR_ARGS_PARAMETER:
			return functionParamReparseCheck(node);
			//return simpleReparseCheck(node, "...");
			
		case DEFINITION_TEMPLATE:
			return reparseCheck(snippedParser.parseTemplateDefinition(), node);
			
		case TEMPLATE_TYPE_PARAM:
		case TEMPLATE_VALUE_PARAM:
		case TEMPLATE_ALIAS_PARAM:
		case TEMPLATE_TUPLE_PARAM:
		case TEMPLATE_THIS_PARAM:
			return templateParamReparseCheck(node);
			
		case NAMED_MIXIN:
			return reparseCheck(snippedParser.parseDeclarationMixin(), node);
			
		/* -------------------  Statements  ------------------- */
		case BLOCK_STATEMENT:
			return reparseCheck(snippedParser.parseBlockStatement_toMissing().node, node);
		case STATEMENT_EMTPY_BODY:
			return simpleReparseCheck(node, ";");
		case FUNCTION_BODY:
		case IN_OUT_FUNCTION_BODY:
			return reparseCheck(snippedParser.parseFunctionBody(), node);
		case FUNCTION_BODY_OUT_BLOCK:
			return reparseCheck(snippedParser.parseOutBlock().node, node);
			
		default:
			throw assertFail();
		}
	}
	
	public Void simpleReparseCheck(ASTNeoNode node, String expectedCode) {
		CheckSourceEquality.assertCheck(node.toStringAsCode(), expectedCode);
		return VOID;
	}
	
	protected Void functionParamReparseCheck(ASTNeoNode node) {
		return testParameter(node, true, (ASTNeoNode) resetSnippedParser().parseFunctionParameter());
	}
	
	protected Void templateParamReparseCheck(ASTNeoNode node) {
		return testParameter(node, false, snippedParser.parseTemplateParameter());
	}
	
	protected Void testParameter(ASTNeoNode node, boolean isFunction, ASTNeoNode reparsedNonAmbig) {
		reparseCheck(reparsedNonAmbig, node);
		
		Object fromAmbig = snippedParser.new ParseRule_Parameters(TplOrFnMode.AMBIG).parseParameter();
		boolean isAmbig = false;
		if(fromAmbig instanceof AmbiguousParameter) {
			isAmbig = true;
			AmbiguousParameter ambiguousParameter = (AmbiguousParameter) fromAmbig;
			fromAmbig = isFunction ? ambiguousParameter.convertToFunction() : ambiguousParameter.convertToTemplate(); 
		}
		checkNodeEquality((ASTNeoNode) fromAmbig, node);
		resetSnippedParser();
		
		ASTNeoNode paramParsedTheOtherWay = isFunction ? 
			snippedParser.parseTemplateParameter() : (ASTNeoNode) snippedParser.parseFunctionParameter();
		
		assertTrue(allSourceParsedCorrectly() ? isAmbig : true);
		if(allSourceParsedCorrectly()) {
			CheckSourceEquality.assertCheck(paramParsedTheOtherWay.toStringAsCode(), node.toStringAsCode());
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
		return originalSource.substring(node.getStartPos(), node.getEndPos());
	}
	
	public void prepSnippedParser(String snippedSource) {
		nodeSnippedSource = snippedSource;
		resetSnippedParser();
	}
	
	public DeeParser resetSnippedParser() {
		return snippedParser = new DeeParser(new DeeParserTest.DeeTestsLexer(nodeSnippedSource));
	}
	
	public Void reparseCheck(NodeResult<? extends ASTNeoNode> result, ASTNeoNode node) {
		return reparseCheck(result.getNode(), node);
	}
	public Void reparseCheck(IASTNeoNode reparsedNode, ASTNeoNode node) {
		return reparseCheck((ASTNeoNode) reparsedNode, node.getClass(), node, false);
	}
	
	public Void reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node, boolean consumesTrailingWhiteSpace) {
		return reparseCheck(reparsedNode, node.getClass(), node, consumesTrailingWhiteSpace);
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public Void reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass, final ASTNeoNode node,
		boolean consumesSurroundingWhiteSpace
	) {
		assertTrue(reparsedNode != null);
		// TODO check errors are the same?
		checkNodeEquality(reparsedNode, node);
		assertTrue(reparsedNode.getClass() == klass);
		
		// Check source ranges:
		// Must have consumed all input
		assertTrue(snippedParser.lookAhead() == DeeTokens.EOF);
		assertTrue(emptyToNull(snippedParser.lookAheadElement().ignoredPrecedingTokens) == null);
		
		assertEquals(reparsedNode.getSourceRange(), new SourceRange(0, nodeSnippedSource.length()) );
		
		assertTrue(reparsedNode.getEndPos() == snippedParser.lookAheadElement().getStartPos());
		
		assertTrue(
			snippedParser.lastLexElement().isMissingElement() ||
			snippedParser.lastLexElement().getType().isParserIgnored == false ||
			snippedParser.lastLexElement().getEndPos() == 0);
		
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				consumesSurroundingWhiteSpace = true;
			}
		}
		if(!consumesSurroundingWhiteSpace) {
			// Check that there is no trailing whitespace in the range
			assertTrue(lastElementInRange(snippedParser).getEndPos() == snippedParser.getSource().length());
			assertTrue(firstElementInRange(snippedParser.getSource()).ignoredPrecedingTokens == null);
			
			if(snippedParser.lastLexElement().isMissingElement()) {
				consumesSurroundingWhiteSpace = true;
			}
		}
		
		if(consumesSurroundingWhiteSpace) {
			// Check that the range contains all possible whitespace
			assertTrue(elementAfterSnippedRange(node).getStartPos() == 0);
			assertTrue(elementBeforeSnippedRange(node).getEndPos() == node.getStartPos());
		}
		
		resetSnippedParser();
		return VOID;
	}
	
	public LexElement elementAfterSnippedRange(ASTNeoNode node) {
		LexerElementSource afterNodeRangeParser = new LexerElementSource(originalSource.substring(node.getEndPos()));
		LexElement lookAheadElement = afterNodeRangeParser.lookAheadElement();
		return lookAheadElement;
	}
	
	public LexElement elementBeforeSnippedRange(ASTNeoNode node) {
		String beforeSource = originalSource.substring(0, node.getStartPos());
		LexerElementSource beforeNodeRangeLexer = new LexerElementSource(beforeSource);
		while(beforeNodeRangeLexer.lookAhead() != DeeTokens.EOF) {
			beforeNodeRangeLexer.consumeInput();
		}
		return beforeNodeRangeLexer.lookAheadElement();
	}
	
	public LexElement firstElementInRange(String source) {
		return (new LexerElementSource(source)).lookAheadElement();
	}
	
	public LexElement lastElementInRange(AbstractParser parser) {
		LexElement lastLexElement = snippedParser.lastLexElement();
		assertTrue(lastLexElement.isMissingElement() || lastLexElement.getType().isParserIgnored == false);
		assertTrue(lastLexElement == parser.lastNonMissingLexElement() || lastLexElement.isMissingElement());
		return parser.lastLexElement();
	}
	
	public static void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
}