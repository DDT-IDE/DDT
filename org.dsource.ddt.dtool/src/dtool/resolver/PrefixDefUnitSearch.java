package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;
import java.util.Set;

import dtool.ast.ASTNode;
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
import dtool.parser.DeeLexer;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.Token;
import dtool.resolver.api.IDefUnitMatchAccepter;
import dtool.resolver.api.IModuleResolver;
import dtool.resolver.api.PrefixDefUnitSearchBase;
import dtool.resolver.api.PrefixSearchOptions;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends PrefixDefUnitSearchBase {
	
	protected final IDefUnitMatchAccepter defUnitAccepter;
	protected final Set<String> addedDefUnits = new HashSet<String>();
	
	protected ECompletionResultStatus resultCode = ECompletionResultStatus.RESULT_OK;
	
	protected int relexStartPos; // for tests only
	
	public PrefixDefUnitSearch(PrefixSearchOptions searchOptions, ASTNode originNode, int refOffset,
			IDefUnitMatchAccepter defUnitAccepter, IModuleResolver moduleResolver) {
		super(originNode, refOffset, moduleResolver, searchOptions);
		this.defUnitAccepter = defUnitAccepter;
	}
	
	public ECompletionResultStatus getResultCode() {
		return resultCode;
	}
	
	public PrefixDefUnitSearch assignResult(ECompletionResultStatus resultCode, 
		@SuppressWarnings("unused") String errorMsg) {
		this.resultCode = resultCode;
		return this;
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
		String defUnitExtendedName = defUnit.getExtendedName();
		
		if(addedDefUnits.contains(defUnitExtendedName)) {
			return;
		}
		addedDefUnits.add(defUnitExtendedName);
		defUnitAccepter.accept(defUnit, searchOptions);
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(DeeParserResult parseResult, int offset, IModuleResolver mr, 
		IDefUnitMatchAccepter defUnitAccepter) {
		
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());
		
		
		Module neoModule = parseResult.getParsedModule(); 
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(neoModule, offset, true);
		ASTNode node = nodeFinder.match;
		
		// NOTE: for performance reasons we want to provide a startPos as close as possible to offset,
		// so we don't re-lex too many tokens. ASTNodeFinderExtension provides that.
		int relexStartPos = nodeFinder.lastNodeBoundary;
		Token token = findTokenAtOffset(offset, source, relexStartPos);
		
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		final PrefixDefUnitSearch search = 
			new PrefixDefUnitSearch(searchOptions, node, offset, defUnitAccepter, mr);
		search.relexStartPos = relexStartPos;
		
		if((offset > token.getStartPos() && offset < token.getEndPos()) && 
			!(token.type == DeeTokens.WHITESPACE || token.type == DeeTokens.IDENTIFIER)) {
			return search.assignResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
				"Invalid location (inside unmodifiable token)");
		}
		
		/* ============================================== */
		// : Do actual completion search
		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			
			if(node instanceof RefPrimitive) {
				RefPrimitive refPrim = (RefPrimitive) node;
				setupPrefixedSearchOptions(searchOptions, offset, refPrim.getOffset(), refPrim.getTargetSimpleName());
			} else if(node instanceof RefIdentifier) {
				RefIdentifier refIdent = (RefIdentifier) node;
				setupPrefixedSearchOptions(searchOptions, offset, refIdent.getOffset(), refIdent.getIdString());
			} else if(node instanceof RefImportSelection) {
				RefImportSelection refImpSel = (RefImportSelection) node;
				setupPrefixedSearchOptions(searchOptions, offset, refImpSel.getOffset(), refImpSel.getIdString());
			} else if(node instanceof CommonRefQualified) {
				
				int dotOffset = -1;
				if(node instanceof RefQualified) {
					dotOffset = ((RefQualified) node).dotOffset;
				} else {
					dotOffset = node.getStartPos();
				}
				
				if(offset <= dotOffset) {
					return search.assignResult(ECompletionResultStatus.INVALID_REFQUAL_LOCATION, 
							"Invalid Location: before qualifier dot but not next to id.");
				}
			} else if(node instanceof RefModule) {
				RefModule refMod = (RefModule) node;
				
				int refModEndPos = refMod.getEndPos();
				
				// We need to get exact source cause it may contains spaces, even comments
				String refModSource = source.substring(refMod.getStartPos(), refModEndPos);
				
				int rplLen = refModEndPos - offset;
				if(source.length() > offset && Character.isWhitespace(source.charAt(offset))) {
					rplLen = 0; // Don't replace, just append
				}
				String moduleSourceNamePrefix = refModSource.substring(0, offset-refMod.getStartPos());
				setupPrefixedSearchOptions_withCanonization(searchOptions, rplLen, moduleSourceNamePrefix);
				
			} else {
				throw assertFail();
			}
			
			namedRef.doSearch(search);
		} else if(node instanceof Reference) {
			return search.assignResult(ECompletionResultStatus.OTHER_REFERENCE, 
					"Can't complete for node: "+node.getNodeType()+"");
		} else {
			// Since picked node was not a reference, determine appropriate lexical starting scope
			// TODO: this code is a mess, need to cleanup and simplify
			// See also ReferenceResolver.getStartingScope(refSingle);
			IScope scope;
			while(true) {
				assertNotNull(node); 
				scope = isValidCompletionScope(node);
				if(scope != null)
					break;
				
				if(isInsideNonScopeBlock(node, offset, source)) {
					scope = ScopeUtil.getScopeNode(node);
					break;
				}
				
				node = node.getParent();
			}
			assertNotNull(scope);
			
			ReferenceResolver.findDefUnitInExtendedScope(scope, search);
		}
		
		assertTrue(search.resultCode == ECompletionResultStatus.RESULT_OK);
		return search;
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
	
	private static void setupPrefixedSearchOptions(PrefixSearchOptions searchOptions, int offset, int nameOffset,
			String name) {
		assertTrue(offset >= nameOffset);
		assertTrue(offset <= nameOffset + name.length() || name.isEmpty());
		// empty name is a special case
		int namePrefixLen = name.isEmpty() ? 0 : offset - nameOffset;
		
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
		searchOptions.namePrefixLen = searchOptions.searchPrefix.length();
		searchOptions.rplLen = rplLen;
	}
	
	private static IScope isValidCompletionScope(ASTNode node) {
		if(node instanceof IScope) {
			return (IScope) node;
		} else if(node instanceof Expression) {
			return ScopeUtil.getOuterScope(node);
		} 
		return null;
	}

}