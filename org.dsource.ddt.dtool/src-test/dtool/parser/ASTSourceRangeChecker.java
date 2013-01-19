package dtool.parser;

import static dtool.tests.CommonTestUtils.assertCast;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.ListIterator;

import dtool.ast.ASTCommonSourceRangeChecker;
import dtool.ast.ASTNeoAbstractVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.NodeUtil;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationStorageClass;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelectiveAlias;
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
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefNative;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public class ASTSourceRangeChecker extends ASTCommonSourceRangeChecker {
	
	protected final String source;
	protected final ArrayList<ParserError> expectedErrors;
	protected final ListIterator<ParserError> errorIterator;

	public ASTSourceRangeChecker(String source, ArrayList<ParserError> expectedErrors) {
		super(0);
		this.source = source;
		this.expectedErrors = expectedErrors;
		int tempCursor = 0;
		for (ParserError parserError : expectedErrors) {
			assertTrue(parserError.sourceRange.getStartPos() >= tempCursor);
			tempCursor = parserError.sourceRange.getStartPos();
		}
		errorIterator = expectedErrors.listIterator();
	}
	
	@Override
	protected void handleSourceRangeEndPosBreach(ASTNeoNode elem) {
		assertFail();
	}
	
	@Override
	protected boolean handleSourceRangeNoInfo(ASTNeoNode elem) {
		throw assertFail();
	}
	
	@Override
	protected boolean handleSourceRangeStartPosBreach(ASTNeoNode elem) {
		throw assertFail();
	}
	
	@Override
	public boolean preVisit(ASTNeoNode elem) {
		boolean preVisit = super.preVisit(elem);
		
		if(source !=  null ) {
			assertTrue(elem.getStartPos() <= source.length() && elem.getEndPos() <= source.length());
			
			// Warning, this can have quadratic performance on node depth
			elem.accept(new ASTReparseCheckSwitcher());
		}
		
		return preVisit;
	}
	
	public class ASTReparseCheckSwitcher extends ASTNeoAbstractVisitor {
		public static final boolean DONT_VISIT_CHILDREN = false; // This visitor shouldn't iterate to children
		
		protected String nodeRangeSource;
		protected DeeParser nodeRangeSourceParser;
		
		@Override
		public boolean preVisit(ASTNeoNode node) {
			nodeRangeSource = source.substring(node.getStartPos(), node.getEndPos()); 
			nodeRangeSourceParser = new DeeParser(nodeRangeSource);
			
			return true;
		}
		
		public boolean areThereMissingTokenErrorsInNode(ASTNeoNode node) {
			for (ParserError error : expectedErrors) {
				
				switch (error.errorType) {
				case EXPECTED_TOKEN: if(error.msgObj2 != DeeTokens.IDENTIFIER) break;
				case MALFORMED_TOKEN:
				case INVALID_TOKEN_CHARACTERS:
				case SYNTAX_ERROR:
				case EXPECTED_RULE:
					continue;
				case INVALID_EXTERN_ID: break;
				}
				
				// Then there is an EXPECTED_TOKEN error in error.originNode
				assertNotNull(error.originNode);
				
				if(NodeUtil.isContainedIn(error.originNode, node)) {
					return true;
				}
				if(error.sourceRange.getStartPos() >= node.getEndPos()) {
					return false;
				}
			}
			return false;
		}
		
		protected DeeParser parser(String string) {
			nodeRangeSourceParser = new DeeParser(string);
			return nodeRangeSourceParser;
		}
		
		@Override
		public void postVisit(ASTNeoNode node) {
			if(!(node instanceof Module)) {
				if(!areThereMissingTokenErrorsInNode(node)) {
					DeeParserTest.checkSourceEquality(nodeRangeSource, node.toStringAsCode(), true);
				}
			}
		}
		
		/** This will test if node has a correct source range even in situations where
		 * {@link #postVisit} cannot do a test using {@link DeeParserTest#checkSourceEquality }
		 */
		public boolean reparseCheck(ASTNeoNode reparsedNode, ASTNeoNode node) {
			assertTrue(reparsedNode != null && nodeRangeSourceParser.lookAhead() == DeeTokens.EOF);
			assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
			return DONT_VISIT_CHILDREN;
		}
		
		@Override
		public boolean visit(ASTNeoNode node) {
			if(node instanceof MiscDeclaration) {
				return reparseCheck(MiscDeclaration.parseMiscDeclaration(nodeRangeSourceParser), node);
			}
			if(node instanceof NodeList2) {
				return reparseCheck(nodeRangeSourceParser.parseDeclList(null), node);
			}
			assertFail();
			return false;
		}
		
		@Override
		public boolean visit(Symbol symbol) {
			assertTrue(areThereMissingTokenErrorsInNode(symbol) == false);
			return DONT_VISIT_CHILDREN;
		}
		
		@Override
		public boolean visit(DefUnit node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(Module module) {
			assertTrue(module.getStartPos() == 0 && module.getEndPos() == source.length());
			return DONT_VISIT_CHILDREN;
		}
		
		@Override
		public boolean visit(DeclarationModule node) {
			return reparseCheck(nodeRangeSourceParser.parseModuleDeclaration(), node);
		}
		
		@Override
		public boolean visit(DeclarationImport node) {
			return reparseCheck(nodeRangeSourceParser.parseImportDecl(), node);
		}
		
		@Override
		public boolean visit(ImportContent node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseImportFragment(), node);
		}
		
		@Override
		public boolean visit(ImportSelective node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseImportFragment(), node);
		}
		
		@Override
		public boolean visit(ImportAlias node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseImportFragment(), node);
		}
		
		@Override
		public boolean visit(ImportSelectiveAlias node) {
			IImportFragment importFragment = parser("foo : " +nodeRangeSource).parseImportFragment();
			return reparseCheck(assertCast(importFragment, ImportSelective.class).impSelFrags.get(0), node);
		}
		
		@Override
		public boolean visit(DeclarationEmpty node) {
			assertEquals(nodeRangeSource, ";");
			return DONT_VISIT_CHILDREN;
		}
		
		//-- various Declarations
		@Override
		public boolean visit(DeclarationLinkage node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseDeclarationExternLinkage(), node);
		}
		@Override
		public boolean visit(DeclarationAlign node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseDeclarationAlign(), node);
		}
		@Override
		public boolean visit(DeclarationPragma node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseDeclarationPragma(), node);
		}
		@Override
		public boolean visit(DeclarationProtection node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseDeclarationProtection(), node);
		}
		@Override
		public boolean visit(DeclarationStorageClass node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseDeclarationBasicAttrib(), node);
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
			assertFail(); // TODO Auto-generated method stub
			return false;
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
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(Reference node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(CommonRefNative node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(NamedReference node) {
			assertTrue(areThereMissingTokenErrorsInNode(node) == false);
			return false;
		}
		
		@Override
		public boolean visit(CommonRefQualified node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(RefIdentifier node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean visit(RefTemplateInstance node) {
			assertFail(); // TODO Auto-generated method stub
			return false;
		}
		
		
		@Override
		public boolean visit(DeclarationMixinString node) {
			return reparseCheck((ASTNeoNode) nodeRangeSourceParser.parseMixinStringDeclaration(), node);
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
		
	}
	
}