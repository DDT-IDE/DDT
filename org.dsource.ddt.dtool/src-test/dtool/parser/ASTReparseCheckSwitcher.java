package dtool.parser;

import static dtool.tests.CommonTestUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.InvalidDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
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
			return reparseCheck(snippedParser.parseDeclaration(false), node);
		case INVALID_SYNTAX:
			return reparseCheck(snippedParser.parseDeclaration(false), node);
		case NODE_LIST: 
			// Dont reparse Nodelist since there are two kinds of this (single and multi) 
			// and we dont know which one to parse TODO
			return VOID;
			
		//-- various Declarations
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
			return reparseCheck(snippedParser.parseVarFragment(), node);
		case INITIALIZER_EXP:
			InitializerExp initializerExp = (InitializerExp) node;
			Resolvable initExpExp = initializerExp.exp;
			return reparseCheck(snippedParser.parseInitializer(), node, initExpExp instanceof MissingExpression);
		
		/* ---------------------------------- */
		
		case REF_IMPORT_SELECTION:
			return reparseCheck((ASTNeoNode) snippedParser.parseImportSelectiveSelection(), node, false);
		case REF_MODULE:
			return reparseCheck(snippedParser.parseImportFragment().getModuleRef(), node);
		case REF_IDENTIFIER:
			return reparseCheck(snippedParser.parseRefIdentifier(), node);
		
		case REF_QUALIFIED:
		case REF_MODULE_QUALIFIED:
		case REF_PRIMITIVE:
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
			return reparseCheck(parseReference(snippedParser), node);
		
		/* ---------------------------------- */
		
		case MISSING_EXPRESSION:
			assertEquals("", node.toStringAsCode());
			return VOID;
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
		default:
			throw assertFail();
		}
	}
	
	public void prepNodeSnipedParser(ASTNeoNode node) {
		nodeSnippedSource = originalSource.substring(node.getStartPos(), node.getEndPos());
		snippedParser = new DeeParser(new DeeParserTest.DeeTestsLexer(nodeSnippedSource));
	}
	
	protected static Reference parseReference(DeeParser nodeRangeSourceParser) {
		ASTNeoNode decl = nodeRangeSourceParser.parseDeclaration();
		if(decl instanceof InvalidSyntaxElement) {
			return (Reference) assertCast(decl, InvalidSyntaxElement.class).node;
		} else if(decl instanceof InvalidDeclaration) {
			return (Reference) assertCast(decl, InvalidDeclaration.class).node;
		} else {
			assertTrue(decl == null);
			return null;
		}
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public Void reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node) {
		return reparseCheck(reparsedNode, node.getClass(), node, false);
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