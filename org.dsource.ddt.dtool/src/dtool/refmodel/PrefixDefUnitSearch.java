package dtool.refmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;
import java.util.Set;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.Expression;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefPrimitive;
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.contentassist.CompletionSession;
import dtool.contentassist.CompletionSession.ECompletionResultStatus;
import dtool.parser.DeeLexer;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.Token;
import dtool.refmodel.api.IDefUnitMatchAccepter;
import dtool.refmodel.api.IModuleResolver;
import dtool.refmodel.api.PrefixDefUnitSearchBase;
import dtool.refmodel.api.PrefixSearchOptions;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends PrefixDefUnitSearchBase {
	
	protected final IDefUnitMatchAccepter defUnitAccepter;
	protected final Set<String> addedDefUnits = new HashSet<String>();
	
	protected int relexStartPos;
	
	public PrefixDefUnitSearch(PrefixSearchOptions searchOptions, IScopeNode refScope, int refOffset,
			IDefUnitMatchAccepter defUnitAccepter, IModuleResolver moduleResolver) {
		super(refScope, refOffset, moduleResolver, searchOptions);
		this.defUnitAccepter = defUnitAccepter;
	}
	
	@Override
	public boolean matches(DefUnit defUnit) {
		return matchesName(defUnit.getName());
	}
	
	@Override
	public boolean matchesName(String defName) {
		return defName.startsWith(searchOptions.searchPrefix);
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
	
	@Override
	public void addMatch(DefUnit defUnit) {
		if(notOccluded(defUnit)) {
			defUnitAccepter.accept(defUnit, searchOptions);
		}
	}
	
	public boolean notOccluded(DefUnit newDefUnit) {
		String newDefUnitName = newDefUnit.toStringAsElement();
		
		if(addedDefUnits.contains(newDefUnitName)) {
			return false;
		}
		addedDefUnits.add(newDefUnitName);
		return true;
	};
	
	public static PrefixDefUnitSearch doCompletionSearch(CompletionSession session, String defaultModuleName,
			String source, final int offset, IModuleResolver modResolver, IDefUnitMatchAccepter defUnitAccepter) 
	{
		DeeParserResult parseResult = DeeParser.parseSource(source, defaultModuleName);
		
		return doCompletionSearch(session, parseResult, offset, modResolver, defUnitAccepter);
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(CompletionSession session, DeeParserResult parseResult,
		final int offset, IModuleResolver mr, IDefUnitMatchAccepter defUnitAccepter) {
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());
		assertTrue(session.errorMsg == null);
		session.resultCode = ECompletionResultStatus.RESULT_OK;
		
		
		Module neoModule = parseResult.getParsedModule(); 
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(neoModule, offset, true);
		ASTNode node = nodeFinder.match;
		
		// NOTE: for performance reasons we want to provide a startPos as close as possible to offset,
		// so we don't re-lex too many tokens. ASTNodeFinderExtension provides that.
		// TODO: find a way to test the above premise?
		int relexStartPos = nodeFinder.lastNodeBoundary;
		Token token = findTokenAtOffset(offset, source, relexStartPos);
		
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		IScopeNode refScope = ScopeUtil.getScopeNode(node);
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(searchOptions, refScope, offset, defUnitAccepter, mr);
		search.relexStartPos = relexStartPos;
		
		if((offset > token.getStartPos() && offset < token.getEndPos()) && 
			!(token.type == DeeTokens.WHITESPACE || token.type == DeeTokens.IDENTIFIER)) {
			CompletionSession.assignResult(session, ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
				"Invalid location (inside unmodifiable token)");
			return null;
		}
		
		/* ============================================== */
		// : Do actual completion search
		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			
			if(node instanceof RefPrimitive) {
				RefPrimitive refIdent = (RefPrimitive) node;
				String name = refIdent.getTargetSimpleName();
				setupPrefixedSearchOptions(searchOptions, offset, refIdent.getOffset(), name);
			} else if(node instanceof RefIdentifier) {
				RefIdentifier refIdent = (RefIdentifier) node;
				setupPrefixedSearchOptions(searchOptions, offset, refIdent.getOffset(), refIdent.getIdString());
			} else if(node instanceof CommonRefQualified) {
				
				int dotOffset = -1;
				if(node instanceof RefQualified) {
					dotOffset = ((RefQualified) node).dotOffset;
					if(dotOffset == -1) { // Hack for old convertion parser usage
						RefQualified refQualified = (RefQualified) node;
						String str = source.substring(refQualified.qualifier.getEndPos(), 
							refQualified.qualifiedId.getStartPos());
						dotOffset = refQualified.qualifier.getEndPos() + str.indexOf(".");
					}
				} else {
					dotOffset = node.getStartPos();
				}
				
				if(offset <= dotOffset) {
					CompletionSession.assignResult(session, ECompletionResultStatus.INVALID_REFQUAL_LOCATION, 
							"Invalid Location: before qualifier dot but not next to id.");
					return search;
				}
			} else if(node instanceof RefModule) {
				RefModule refMod = (RefModule) node;
				
				int refModEndPos = refMod.getEndPos();
				
				// We need to get exact source cause it may contains spaces, even comments
				String refModCanonicalName = refMod.toStringAsElement();
				
				String refModSource = refMod.hasSourceRangeInfo() ?
						source.substring(refMod.getStartPos(), refModEndPos) :
						refModCanonicalName;
				
				int rplLen = refModEndPos - offset;
				if(source.length() > offset && Character.isWhitespace(source.charAt(offset))) {
					rplLen = 0; // Don't replace, just append
				}
				String moduleSourceNamePrefix = refModSource.substring(0, offset-refMod.getStartPos());
				setupPrefixedSearchOptions_withCanonization(searchOptions, rplLen, moduleSourceNamePrefix);
				
			} else if (node instanceof RefImportSelection) {
				RefImportSelection refImpSel = (RefImportSelection) node;
				setupPrefixedSearchOptions(searchOptions, offset, refImpSel.getOffset(), refImpSel.name);
			} else {
				throw assertFail();
			}
			
			namedRef.doSearch(search);
		} else if(node instanceof Reference) {
			CompletionSession.assignResult(session, ECompletionResultStatus.OTHER_REFERENCE, 
					"Can't complete for node: "+node.getNodeType()+"");
			return search;
		} else {
			// Since picked node was not a reference,
			// determine appropriate scope search parameters
			IScopeNode scope;
			while(true) {
				assertNotNull(node); 
				scope = isValidCompletionScope(node);
				if(scope != null)
					break;
				
				if(isInsideNonScopeBlock(node, offset, source)) {
					scope = ScopeUtil.getScopeNode(node);
					break;
				}
				
				if(offset == node.getStartPos() || offset == node.getEndPos()) {
					node = node.getParent();
				} else {
					break;
				}
			}
			
			ReferenceResolver.findDefUnitInExtendedScope(scope, search);
		}
		
		assertTrue(session.errorMsg == null);
		return search;
	}
	
	public static class ASTNodeFinderExtension extends ASTNodeFinder {
		
		public int lastNodeBoundary = -1;
		
		public ASTNodeFinderExtension(ASTNode root, int offset, boolean inclusiveEnd) {
			super(root, offset, inclusiveEnd);
			findNodeInAST();
			assertTrue(offset >= root.getStartPos() && offset <= root.getEndPos());
			assertTrue(lastNodeBoundary >= 0);
		}
		
		@Override
		public boolean preVisit(ASTNode node) {
			if(node.getStartPos() <= offset ) {
				lastNodeBoundary = node.getStartPos();
			}
			return super.preVisit(node);
		}
		
		@Override
		public void postVisit(ASTNode node) {
			if(node.getEndPos() <= offset ) {
				lastNodeBoundary = node.getEndPos();
			}
			super.postVisit(node);
		}
		
		@Override
		public boolean findOnNode(ASTNode node) {
			return super.findOnNode(node);
		}
	}
	
	/** Find the token at given offset of given source (inclusive end).
	 * Start lexing search from startPos as an optimization, so we don't have to lex full source.
	 * startpos should correspond to a token start in source. 
	 */
	public static Token findTokenAtOffset(final int offset, String source, int startPos) {
		assertTrue(startPos <= offset);
		DeeLexer lexer = new DeeLexer(source);
		lexer.reset(startPos);
		Token token;
		while(true) {
			token = lexer.next();
			if(offset <= token.getEndPos())
				return token;
			assertTrue(token.type != DeeTokens.EOF);
		}
	}

	private static boolean isInsideNonScopeBlock(ASTNode node, int offset, String sourceStr) {
		if(!(node instanceof INonScopedBlock)) {
			return false;
		}
		INonScopedBlock nonScopedBlock = (INonScopedBlock) node;
		nonScopedBlock.getMembersIterator(); // Need proper way to determine CC context
		
		if(node instanceof DeclarationAttrib) {
			int blockContentsStart = sourceStr.indexOf(":", node.getStartPos());
			if(blockContentsStart != -1 && offset > blockContentsStart) {
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	private static void setupPrefixedSearchOptions(PrefixSearchOptions searchOptions, final int offset, int nameOffset,
			String name) {
		int namePrefixLen = offset - nameOffset;
		assertTrue(namePrefixLen >= 0);
		if(name.length() < namePrefixLen) {
			// This case shouldnt happen, but can happen due to parser source range bugs, so workaround
			namePrefixLen = name.length();
		}
		searchOptions.searchPrefix = name.substring(0, namePrefixLen);
		searchOptions.namePrefixLen = searchOptions.searchPrefix.length();
		searchOptions.rplLen = name.length() - namePrefixLen;
	}
	
	private static void setupPrefixedSearchOptions_withCanonization(PrefixSearchOptions searchOptions,
			int rplLen, String moduleSourceNamePrefix) {
		String canonicalModuleNamePrefix = "";
		for (int i = 0; i < moduleSourceNamePrefix.length(); i++) {
			char ch = moduleSourceNamePrefix.charAt(i);
			// This is not the ideal way to determine the canonical name, info should be provided by parser
			if(!Character.isWhitespace(ch)) {
				canonicalModuleNamePrefix = canonicalModuleNamePrefix + ch; 
			}
		}
		
		searchOptions.searchPrefix = canonicalModuleNamePrefix;
		searchOptions.namePrefixLen = canonicalModuleNamePrefix.length();
		searchOptions.rplLen = rplLen;
	}
	
	private static IScopeNode isValidCompletionScope(ASTNode node) {
		if(node instanceof IScopeNode) {
			return (IScopeNode) node;
		} else if(node instanceof Expression) {
			return ScopeUtil.getOuterScope(node);
		} 
		return null;
	}

}