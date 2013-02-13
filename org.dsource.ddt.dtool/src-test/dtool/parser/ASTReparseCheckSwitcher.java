package dtool.parser;

import static dtool.tests.CommonTestUtils.assertCast;
import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.emptyToNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTNeoAbstractVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.InvalidDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionCtor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralChar;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.InfixExpression;
import dtool.ast.expressions.InitializerArray;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.PostfixExpression;
import dtool.ast.expressions.PrefixExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.Reference;
import dtool.ast.references.TypeDelegate;
import dtool.ast.references.TypeFunction;
import dtool.ast.references.TypeTypeof;

public class ASTReparseCheckSwitcher extends ASTNeoAbstractVisitor {
	
	public static void check(ASTNeoNode node, String parseSource, Collection<ParserError> errors) {
		// Warning, this check has quadratic performance on node depth
		node.accept(new ASTReparseCheckSwitcher(parseSource, errors));
	}
	
	public static final boolean VISIT_CHILDREN = false;
	
	protected final String source;
	protected final Collection<ParserError> expectedErrors;
	
	public ASTReparseCheckSwitcher(String source, Collection<ParserError> expectedErrors) {
		this.source = assertNotNull_(source);
		this.expectedErrors = expectedErrors;
	}
	
	protected String nodeSnippedSource;
	protected DeeParser nssParser;
	
	@Override
	public boolean preVisit(ASTNeoNode node) {
		// prep for type specific switch
		nodeSnippedSource = source.substring(node.getStartPos(), node.getEndPos());
		nssParser = prepParser(nodeSnippedSource);
		return true;
	}
	
	protected DeeParser prepParser(String string) {
		nodeSnippedSource = string;
		return nssParser = new DeeParser(string);
	}
	
	/** This will test if node has a correct source range even in situations where
	 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
	 */
	public boolean reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node) {
		return reparseCheck(reparsedNode, node.getClass(), node, false);
	}
	
	public boolean reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node, boolean consumesTrailingWhiteSpace) {
		return reparseCheck(reparsedNode, node.getClass(), node, consumesTrailingWhiteSpace);
	}
	
	public boolean reparseCheck(ASTNeoNode reparsedNode, Class<? extends ASTNeoNode> klass, ASTNeoNode node,
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
		return VISIT_CHILDREN;
	}
	
	public void checkNodeEquality(ASTNeoNode reparsedNode, ASTNeoNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
	@Override
	public void postVisit(ASTNeoNode node) {
	}
	
	/* ---------------- Parsing helpers ---------------- */
	
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
	
	/* ---------------- Switcher ---------------- */
	
	@Override
	public boolean visit(ASTNeoNode node) {
		if(node instanceof NodeList2) {
			// Dont reparse Nodelist since there are two kinds of this (single and multi) 
			// and we dont know which one to parse TODO
			//return reparseCheck(nssParser.parseDeclList(null), node, consumeTrailingWhiteTokens);
			return VISIT_CHILDREN;
		}
		if(node instanceof InvalidDeclaration) {
			return reparseCheck(nssParser.parseDeclaration(false), node);
		}
		if(node instanceof InvalidSyntaxElement) {
			return reparseCheck(nssParser.parseDeclaration(false), node);
		}
		assertFail();
		return false;
	}
	
	@Override
	public boolean visit(Symbol node) {
		if(node instanceof DefSymbol) {
			return reparseCheck(nssParser.parseSymbol(), Symbol.class, node, false);
		}
		return reparseCheck(nssParser.parseSymbol(), node);
	}
	
	@Override
	public boolean visit(DefUnit node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(Module module) {
		assertTrue(module.getStartPos() == 0 && module.getEndPos() == source.length());
		return VISIT_CHILDREN;
	}
	
	@Override
	public boolean visit(DeclarationModule node) {
		return reparseCheck(nssParser.parseModuleDeclaration(), node);
	}
	
	@Override
	public boolean visit(DeclarationImport node) {
		return reparseCheck(nssParser.parseImportDeclaration(), node);
	}
	
	@Override
	public boolean visit(ImportContent node) {
		return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
	}
	
	@Override
	public boolean visit(ImportSelective node) {
		return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
	}
	
	@Override
	public boolean visit(ImportAlias node) {
		return reparseCheck((ASTNeoNode) nssParser.parseImportFragment(), node);
	}
	
	@Override
	public boolean visit(ImportSelectiveAlias node) {
		return reparseCheck((ASTNeoNode) nssParser.parseImportSelectiveSelection(), node);
	}
	
	@Override
	public boolean visit(DeclarationEmpty node) {
		return reparseCheck(nssParser.parseDeclaration(), node);
	}
	
	//-- various Declarations
	@Override
	public boolean visit(DeclarationLinkage node) {
		return reparseCheck(nssParser.parseDeclarationExternLinkage(), node);
	}
	@Override
	public boolean visit(DeclarationAlign node) {
		return reparseCheck(nssParser.parseDeclarationAlign(), node);
	}
	@Override
	public boolean visit(DeclarationPragma node) {
		return reparseCheck(nssParser.parseDeclarationPragma(), node);
	}
	@Override
	public boolean visit(DeclarationProtection node) {
		return reparseCheck(nssParser.parseDeclarationProtection(), node);
	}
	@Override
	public boolean visit(DeclarationBasicAttrib node) {
		return reparseCheck(nssParser.parseDeclarationBasicAttrib(), node);
	}
	
	//-- Aggregates
	@Override
	public boolean visit(DefinitionStruct node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionUnion node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionClass node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionInterface node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionTemplate node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionVariable node) {
		return reparseCheck(nssParser.parseDeclaration(), node);
	}
	@Override
	public boolean visit(DefinitionVarFragment node) {
		return reparseCheck(nssParser.parseVarFragment(), node);
	}
	
	@Override
	public boolean visit(InitializerExp node) {
		return reparseCheck(nssParser.parseInitializer(), node, node.exp instanceof MissingExpression);
	}
	@Override
	public boolean visit(InitializerArray node) {
		return reparseCheck(nssParser.parseInitializer(), node);
	}
	@Override
	public boolean visit(InitializerStruct node) {
		return reparseCheck(nssParser.parseInitializer(), node);
	}
	@Override
	public boolean visit(InitializerVoid node) {
		return reparseCheck(nssParser.parseInitializer(), node);
	}
	
	@Override
	public boolean visit(DefinitionEnum node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionTypedef node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionAlias node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionFunction node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DefinitionCtor node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(Resolvable node) {
		if(node instanceof MissingExpression) {
			assertEquals("", node.toStringAsCode());
			return VISIT_CHILDREN;
		}
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(Reference node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(RefIdentifier node) {
		return reparseCheck(nssParser.parseRefIdentifier(), node);
	}
	@Override
	public boolean visit(RefImportSelection node) {
		return reparseCheck((ASTNeoNode) nssParser.parseImportSelectiveSelection(), node, false);
	}
	@Override
	public boolean visit(RefQualified node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(RefModuleQualified node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(RefPrimitive node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(RefModule node) {
		return reparseCheck(nssParser.parseImportFragment().getModuleRef(), node);
	}
	
	@Override
	public boolean visit(RefTypeDynArray node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(RefTypePointer node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(TypeDelegate node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(TypeFunction node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	@Override
	public boolean visit(RefIndexing node) {
		return reparseCheck(parseReference(nssParser), node);
	}
	
	@Override
	public boolean visit(TypeTypeof node) {
		throw assertFail(); // TODO Auto-generated method stub
	}
	@Override
	public boolean visit(RefTemplateInstance node) {
		throw assertFail(); // TODO Auto-generated method stub
	}
	
	/* ---------------------------------- */
	
	public boolean expressionReparseCheck(Expression node) {
		return reparseCheck(nssParser.parseExpression(), node);
	}
	@Override public boolean visit(ExpThis node) { return expressionReparseCheck(node); }
	
	@Override public boolean visit(ExpSuper node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpNull node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpArrayLength node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpLiteralBool node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpLiteralInteger node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpLiteralString node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpLiteralFloat node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpLiteralChar node) { return expressionReparseCheck(node); }
	
	@Override public boolean visit(ExpReference node) { return expressionReparseCheck(node); }
	
	@Override public boolean visit(PrefixExpression node) { return expressionReparseCheck(node); }
	@Override public boolean visit(PostfixExpression node) { return expressionReparseCheck(node); }
	@Override public boolean visit(InfixExpression node) { return expressionReparseCheck(node); }
	@Override public boolean visit(ExpConditional node) { return expressionReparseCheck(node); }
	
	@Override
	public boolean visit(ExpLiteralFunc node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(ExpLiteralNewAnonClass node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	/* ---------------------------------- */
	
	@Override
	public boolean visit(DeclarationMixinString node) {
		return reparseCheck(nssParser.parseDeclarationMixinString(), node);
	}
	
	
	@Override
	public boolean visit(DeclarationInvariant node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DeclarationUnitTest node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean visit(DeclarationConditional node) {
		assertFail(); // TODO Auto-generated method stub
		return false;
	}
	
}
