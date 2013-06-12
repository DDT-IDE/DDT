package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNode;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.InitializerArray.ArrayInitEntry;
import dtool.ast.expressions.InitializerStruct.StructInitEntry;
import dtool.ast.expressions.MissingParenthesesExpression;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.ast.statements.CommonStatementList;
import dtool.ast.statements.ForeachRangeExpression;
import dtool.parser.DeeParsingChecks.DeeParsingNodeCheck;

public class DeeParsingSourceRangeChecks extends DeeParsingNodeCheck {
	
	public static void runParsingSourceRangeChecks(ASTNode node, final String fullSource) {
		new DeeParsingSourceRangeChecks(fullSource, node).doCheck();
	}
	
	public DeeParsingSourceRangeChecks(String source, ASTNode node) {
		super(source, node);
	}
	
	public void doCheck() {
		
		switch (nodeUnderTest.getNodeType()) {
		case MODULE: {
			Module module = (Module) nodeUnderTest;
			int endPos = module.getEndPos();
			assertTrue(module.getStartPos() == 0 && (endPos == fullSource.length() || 
				fullSource.charAt(endPos) == 0x00 || fullSource.charAt(endPos) == 0x1A));
			return;
		}
		
		case MISSING_EXPRESSION:
			if(nodeUnderTest instanceof MissingParenthesesExpression) {
				SourceEquivalenceChecker.assertCheck(nodeUnderTest.toStringAsCode(), "");
			}
			return;
			
		default: 
			basicSourceRangeCheck();
			
			return;
		}
	}
	
	public void basicSourceRangeCheck() {
		if(!canBeginWithEmptySpace(nodeUnderTest)) {
			LexElement firstLexElement = firstLexElementInNode();
			assertTrue(firstLexElement.getFullRangeStartPos() == firstLexElement.getStartPos()
				|| isDocComment(firstLexElement, nodeUnderTest));
		}
		
		if(nodeConsumesTrailingWhiteSpace(nodeUnderTest)) {
			// Check that the range contains all possible whitespace
			assertTrue(lexElementAfterNode(nodeUnderTest).getStartPos() == 0);
		}
	}
	
	public static boolean isDocComment(LexElement firstLexElement, ASTNode node) {
		return (node instanceof DefUnit || node instanceof DeclarationModule) &&
			DeeTokenSemantics.tokenIsDocComment(firstLexElement.precedingSubChannelTokens[0]);
	}
	
	public LexElement firstLexElementInNode() {
		return firstLexElementInSource(fullSource.substring(nodeUnderTest.getStartPos()));
	}
	
	public LexElement lexElementAfterNode(ASTNode node) {
		return firstLexElementInSource(fullSource.substring(node.getEndPos()));
	}
	
	public static LexElement firstLexElementInSource(String source) {
		return new LexElementProducer().produceLexElement(new DeeLexer(source));
	}
	
	public static boolean canBeginWithEmptySpace(final ASTNode node) {
		switch (node.getNodeType()) {
		case DECL_LIST:
		case SCOPED_STATEMENT_LIST:
		case CSTYLE_ROOT_REF:
		case MISSING_EXPRESSION:
			return true;

		case REF_IDENTIFIER:
		case REF_IMPORT_SELECTION:
			return DeeParser.isMissing((Reference) node);
		case STRUCT_INIT_ENTRY: {
			StructInitEntry initEntry = (StructInitEntry) node;
			return canBeginWithEmptySpace(initEntry.member != null ? initEntry.member : (ASTNode) initEntry.value);
		}
		case ARRAY_INIT_ENTRY: {
			ArrayInitEntry initEntry = (ArrayInitEntry) node;
			return canBeginWithEmptySpace(initEntry.index != null ? initEntry.index : (ASTNode) initEntry.value);
		}
		case MAPARRAY_ENTRY: {
			MapArrayLiteralKeyValue mapArrayEntry = (MapArrayLiteralKeyValue) node;
			return canBeginWithEmptySpace(mapArrayEntry.key);
		}
		case FOREACH_RANGE_EXPRESSION: {
			ForeachRangeExpression fre = (ForeachRangeExpression) node;
			return canBeginWithEmptySpace(fre.lower);
		}
		
		case BLOCK_STATEMENT:
		case BLOCK_STATEMENT_UNSCOPED: {
			return ((CommonStatementList) node).statements == null;
		}
		
		default:
			return false;
		}
	}
	
	
	public static boolean nodeConsumesTrailingWhiteSpace(final ASTNode node) {
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			if(declAttrib.bodySyntax == AttribBodySyntax.COLON) {
				return true;
			}
		}
		if(node instanceof RefIdentifier) {
			RefIdentifier refId = (RefIdentifier) node;
			return DeeParser.isMissing(refId); 
		}
		if(node instanceof DefSymbol) {
			return false;
		}
		
		return false;
	}
	
}