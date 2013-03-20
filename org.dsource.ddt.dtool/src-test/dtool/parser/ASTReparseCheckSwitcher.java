package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefQualified;
import dtool.parser.AbstractParser.LexElement;

public class ASTReparseCheckSwitcher {
	
	protected static final Void VOID = null;
	
	protected final String originalSource;
	
	public ASTReparseCheckSwitcher(String source) {
		this.originalSource = assertNotNull_(source);
	}
	
	protected String nodeSnippedSource;
	protected DeeParser snippedParser;
	
	public Void doCheck(ASTNeoNode node) {
		prepNodeSnipedParser(node);
		
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
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection(), node, false);
		case REF_MODULE:
			return reparseCheck(snippedParser.parseImportFragment().getModuleRef(), node);
		case REF_IDENTIFIER:
			return reparseCheck(snippedParser.parseRefIdentifier(), node);
		
		case REF_QUALIFIED: {
			RefQualified refQual = (RefQualified) node;
			if(RefQualified.getRootNode(refQual) instanceof Expression) {
				return reparseCheck(((ExpReference) snippedParser.parseExpression()).ref, node);
			} else {
				return reparseCheck(snippedParser.parseReference(), node);
			}
		}
		case REF_MODULE_QUALIFIED:
		case REF_PRIMITIVE:
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
		case REF_TEMPLATE_INSTANCE:
		case REF_TYPEOF:
		case REF_MODIFIER:
			return reparseCheck(snippedParser.parseReference(), node);
		
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
		
		case EXP_REFERENCE:
		case EXP_PARENTHESES:
		
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

		case EXP_POSTFIX:
		case EXP_INFIX:
		case EXP_CONDITIONAL:
			return reparseCheck(snippedParser.parseExpression(), (Expression) node);
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
			return reparseCheck((ASTNeoNode) snippedParser.parseFunctionParameter(), node);
		case NAMELESS_PARAMETER:
			ASTNeoNode fnParam = (ASTNeoNode) snippedParser.parseFunctionParameter();
			if(fnParam == null) {
				return simpleReparseCheck(node, "");
			}
			return reparseCheck(fnParam, node);
		case VAR_ARGS_PARAMETER:
			return simpleReparseCheck(node, "...");
			
		/* -------------------  Statements  ------------------- */
		case BLOCK_STATEMENT:
			return reparseCheck(snippedParser.parseBlockStatement_toMissing().result, node);
		case STATEMENT_EMTPY_BODY:
			return simpleReparseCheck(node, ";");
		case FUNCTION_BODY:
		case IN_OUT_FUNCTION_BODY:
			return reparseCheck(snippedParser.parseFunctionBody(), node);
		case FUNCTION_BODY_OUT_BLOCK:
			return reparseCheck(snippedParser.parseOutBlock().result, node);
			
		default:
			throw assertFail();
		}
	}
	
	public Void simpleReparseCheck(ASTNeoNode node, String expectedCode) {
		DeeParserTest.checkSourceEquality(node, expectedCode);
		return VOID;
	}
	
	public void prepNodeSnipedParser(ASTNeoNode node) {
		nodeSnippedSource = originalSource.substring(node.getStartPos(), node.getEndPos());
		snippedParser = new DeeParser(new DeeParserTest.DeeTestsLexer(nodeSnippedSource));
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public Void reparseCheck(IASTNeoNode reparsedNode, ASTNeoNode node) {
		return reparseCheck((ASTNeoNode) reparsedNode, node.getClass(), node, false);
	}
	
	public Void reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node, boolean consumesTrailingWhiteSpace) {
		return reparseCheck(reparsedNode, node.getClass(), node, consumesTrailingWhiteSpace);
	}
	
	public Void reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass, final ASTNeoNode node,
		boolean consumesSurroundingWhiteSpace
	) {
		// Must have consumed all input
		assertTrue(reparsedNode != null && snippedParser.lookAhead() == DeeTokens.EOF);
		assertTrue(emptyToNull(snippedParser.lookAheadElement().ignoredPrecedingTokens) == null);
		
		assertEquals(reparsedNode.getSourceRange(), new SourceRange(0, nodeSnippedSource.length()) );
		assertTrue(reparsedNode.getClass() == klass);
		
		assertTrue(reparsedNode.getEndPos() == snippedParser.lookAheadElement().getStartPos());
		
		assertTrue(snippedParser.lastLexElement.getType().isParserIgnored == false
			|| snippedParser.lastLexElement.getEndPos() == 0);
		
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				consumesSurroundingWhiteSpace = true;
			}
		}
		if(!consumesSurroundingWhiteSpace) {
			// Check that there is no trailing whitespace in the range
			assertTrue(lastElementInRange(snippedParser).getEndPos() == snippedParser.getSource().length());
			assertTrue(firstElementInRange(snippedParser).ignoredPrecedingTokens == null);
			
			if(snippedParser.lastLexElement.isMissingElement()) {
				consumesSurroundingWhiteSpace = true;
			}
		}
		
		if(consumesSurroundingWhiteSpace) {
			// Check that the range contains all possible whitespace
			assertTrue(elementAfterSnippedRange(node).getStartPos() == 0);
			assertTrue(elementBeforeSnippedRange(node).getEndPos() == node.getStartPos());
		}
		
		// TODO check errors are the same?
		checkNodeEquality(reparsedNode, node);
		return VOID;
	}
	
	public LexElement elementAfterSnippedRange(ASTNeoNode node) {
		DeeParser afterNodeRangeParser = new DeeParser(originalSource.substring(node.getEndPos()));
		LexElement lookAheadElement = afterNodeRangeParser.lookAheadElement();
		return lookAheadElement;
	}
	
	public LexElement elementBeforeSnippedRange(ASTNeoNode node) {
		DeeParser beforeNodeRangeParser = new DeeParser(originalSource.substring(0, node.getStartPos()));
		while(beforeNodeRangeParser.lookAhead() != DeeTokens.EOF) {
			beforeNodeRangeParser.consumeInput();
		}
		return beforeNodeRangeParser.lookAheadElement();
	}
	
	public LexElement firstElementInRange(DeeParser parser) {
		return (new DeeParser(parser.getSource())).lookAheadElement();
	}
	
	public LexElement lastElementInRange(AbstractParser parser) {
		assertTrue(parser.lastLexElement.getType().isParserIgnored == false);
		assertTrue(parser.lastLexElement == parser.lastNonMissingLexElement
			|| snippedParser.lastLexElement.isMissingElement());
		return parser.lastLexElement;
	}
	
	public void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
}