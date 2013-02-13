package dtool.parser;

import static dtool.tests.CommonTestUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.InvalidDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.references.Reference;

public class ASTReparseCheckSwitcher {
	
	public static void check(ASTNeoNode node, String parseSource, Collection<ParserError> errors) {
		// Warning, this check has quadratic performance on node depth
		new ASTReparseCheckSwitcher(parseSource, errors).doCheck(node);
	}
	
	protected static final Void VOID = null;
	
	protected final String source;
	protected final Collection<ParserError> expectedErrors;
	
	public ASTReparseCheckSwitcher(String source, Collection<ParserError> expectedErrors) {
		this.source = assertNotNull_(source);
		this.expectedErrors = expectedErrors;
	}
	
	protected String nodeSnippedSource;
	protected DeeParser nssParser;
	
	public Void doCheck(ASTNeoNode node) {
		// prep for type specific switch
		nodeSnippedSource = source.substring(node.getStartPos(), node.getEndPos());
		nssParser = new DeeParser(nodeSnippedSource);
		
		switch (node.getNodeType()) {
		
		case SYMBOL:
			if(node instanceof DefSymbol) {
				return reparseCheck(nssParser.parseSymbol(), Symbol.class, node, false);
			}
			return reparseCheck(nssParser.parseSymbol(), node);
		case DEF_UNIT:
			assertFail();
		
		case MODULE:
			Module module = (Module) node;
			assertTrue(module.getStartPos() == 0 && module.getEndPos() == source.length());
			return VOID;
		case DECL_MODULE:
			return reparseCheck(nssParser.parseModuleDeclaration(), node);
		case DECL_IMPORT:
			return reparseCheck(nssParser.parseImportDeclaration(), node);
		case IMPORT_CONTENT:
			return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
		case IMPORT_ALIAS:
			return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
		case IMPORT_SELECTIVE:
			return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
		case IMPORT_SELECTIVE_ALIAS:
			return reparseCheck((ASTNeoNode) nssParser.parseImportSelectiveSelection(), node);
		
		case DECL_EMTPY:
			return reparseCheck(nssParser.parseDeclaration(), node);
		case DECL_INVALID:
			return reparseCheck(nssParser.parseDeclaration(false), node);
		case INVALID_SYNTAX:
			return reparseCheck(nssParser.parseDeclaration(false), node);
		case NODE_LIST: 
			// Dont reparse Nodelist since there are two kinds of this (single and multi) 
			// and we dont know which one to parse TODO
			return VOID;
			
		//-- various Declarations
		case DECL_LINKAGE:
			return reparseCheck(nssParser.parseDeclarationExternLinkage(), node);
		case DECL_ALIGN:
			return reparseCheck(nssParser.parseDeclarationAlign(), node);
		case DECL_PRAGMA:
			return reparseCheck(nssParser.parseDeclarationPragma(), node);
		case DECL_PROTECTION:
			return reparseCheck(nssParser.parseDeclarationProtection(), node);
		case DECL_BASIC_ATTRIB:
			return reparseCheck(nssParser.parseDeclarationBasicAttrib(), node);
		
		
		case DECL_MIXIN_STRING:
			return reparseCheck(nssParser.parseDeclarationMixinString(), node);
		
		/* ---------------------------------- */
		
		case DEFINITION_VARIABLE:
			return reparseCheck(nssParser.parseDeclaration(), node);
		case DEFINITION_VAR_FRAGMENT:
			return reparseCheck(nssParser.parseVarFragment(), node);
		case INITIALIZER_EXP:
			InitializerExp initializerExp = (InitializerExp) node;
			return reparseCheck(nssParser.parseInitializer(), node, initializerExp.exp instanceof MissingExpression);
		
		/* ---------------------------------- */
		
		case REF_IMPORT_SELECTION:
			return reparseCheck((ASTNeoNode) nssParser.parseImportSelectiveSelection(), node, false);
		case REF_MODULE:
			return reparseCheck(nssParser.parseImportFragment().getModuleRef(), node);
		case REF_IDENTIFIER:
			return reparseCheck(nssParser.parseRefIdentifier(), node);
		
		case REF_QUALIFIED:
		case REF_MODULE_QUALIFIED:
		case REF_PRIMITIVE:
		case REF_TYPE_DYN_ARRAY:
		case REF_TYPE_POINTER:
		case REF_INDEXING:
			return reparseCheck(parseReference(nssParser), node);
		
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
		
		case EXP_REFERENCE:
		
		case EXP_PREFIX:
		case EXP_POSTFIX:
		case EXP_INFIX:
		case EXP_CONDITIONAL:
			return expressionReparseCheck((Expression) node);
			
		default:
			throw assertFail();
		}
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
	
	public Void expressionReparseCheck(Expression node) {
		return reparseCheck(nssParser.parseExpression(), node);
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
	
	public Void reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass, ASTNeoNode node,
		boolean consumesTrailingWhiteSpace
	) {
		// Must have consumed all input
		assertTrue(reparsedNode != null && nssParser.lookAhead() == DeeTokens.EOF);
		assertTrue(emptyToNull(nssParser.lookAheadElement().ignoredPrecedingTokens) == null);
		
		assertEquals(reparsedNode.getSourceRange(), new SourceRange(0, nodeSnippedSource.length()) );
		assertTrue(reparsedNode.getClass() == klass);
		
		assertTrue(reparsedNode.getEndPos() == nssParser.lookAheadElement().getStartPos());
		
		assertTrue(nssParser.lastLexElement.getType().isParserIgnored == false);
		
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				consumesTrailingWhiteSpace = true;
			}
		}
		if(!consumesTrailingWhiteSpace) {
			// Check that there is no trailing whitespace in the range
			assertTrue(nssParser.lastLexElement.getEndPos() == nssParser.lookAheadElement().getStartPos());
			
			if(nssParser.lastLexElement.isMissingElement()) {
				consumesTrailingWhiteSpace = true;
			}
		}
		
		if(consumesTrailingWhiteSpace) {
			// Check that the range contains all possible whitespace
			DeeParser afterNodeRangeParser = new DeeParser(source.substring(node.getEndPos()));
			assertTrue(emptyToNull(afterNodeRangeParser.lookAheadElement().ignoredPrecedingTokens) == null);
		}
		
		// TODO check errors are the same?
		checkNodeEquality(reparsedNode, node);
		return VOID;
	}
	
	public void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
}