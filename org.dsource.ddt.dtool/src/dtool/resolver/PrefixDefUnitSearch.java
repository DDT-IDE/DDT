package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;
import java.util.Set;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonRefIdentifier;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.RefModule;
import dtool.parser.DeeLexer;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.Token;
import dtool.resolver.api.IDefUnitMatchAccepter;
import dtool.resolver.api.IModuleResolver;
import dtool.resolver.api.PrefixDefUnitSearchBase;

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
	
	public PrefixDefUnitSearch(ASTNode originNode, int refOffset,
			IDefUnitMatchAccepter defUnitAccepter, IModuleResolver moduleResolver) {
		super(originNode, refOffset, moduleResolver);
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
	
	public static PrefixDefUnitSearch doCompletionSearch(DeeParserResult parseResult, final int offset, 
		IModuleResolver mr, IDefUnitMatchAccepter defUnitAccepter) {
		
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());		
		
		Module neoModule = parseResult.getParsedModule(); 
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(neoModule, offset, true);
		ASTNode node = nodeFinder.match;
		
		// NOTE: for performance reasons we want to provide a startPos as close as possible to offset,
		// so we don't re-lex too many tokens. ASTNodeFinderExtension provides that.
		int relexStartPos = nodeFinder.lastNodeBoundary;
		// TODO: reuse relexStartPos
		
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(node, offset, defUnitAccepter, mr);
		search.relexStartPos = relexStartPos;
		
		Token tokenAtOffset = findTokenAtOffset(offset, source, 0);
		Token tokenAfterOffset = tokenAtOffset.getEndPos() > offset ? 
			tokenAtOffset : 
			findTokenAtOffset(offset, source, tokenAtOffset.getEndPos());
		
		if((offset > tokenAtOffset.getStartPos() && offset < tokenAtOffset.getEndPos()) &&
			canCompleteInsideToken(tokenAtOffset)) {
			return search.assignResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
				"Invalid location (inside unmodifiable token)");
		}
		
		String searchPrefix = "";
		if(tokenIsAlphaNumeric(tokenAtOffset)) {
			searchPrefix = tokenAtOffset.getSourceValue().substring(0, offset - tokenAtOffset.getStartPos());
		}
		int rplLen = 0;
		if(tokenIsAlphaNumeric(tokenAfterOffset)) {
			rplLen = tokenAfterOffset.getEndPos() - offset;
		}
		search.setupPrefixedSearchOptions(searchPrefix, rplLen);
		
		
		if(node instanceof CommonRefIdentifier) {
			CommonRefIdentifier namedRef = (CommonRefIdentifier) node;
			
			namedRef.doSearch(search);
			return search;
		} else if(node instanceof CommonRefQualified) {
			CommonRefQualified namedRef = (CommonRefQualified) node;
			if(search.getOffset() <= namedRef.getDotOffset()) {
				search.assignResult(ECompletionResultStatus.INVALID_REFQUAL_LOCATION, 
						"Invalid Location: before qualifier dot but not next to id.");
				return search;
			}
			assertEquals(search.searchOptions.searchPrefix, "");
			assertEquals(search.searchOptions.namePrefixLen, 0);
			assertEquals(search.searchOptions.rplLen, 0);
			
			namedRef.doSearch(search);
			return search;
		} else if(node instanceof RefModule) {
			RefModule namedRef = (RefModule) node;
			// RefModule has a specialized way to setup prefix len things
			namedRef.setupPrefixSearchParams(search, source);
			namedRef.doSearch(search);
			return search;
		}
		
		ReferenceResolver.resolveSearchInFullLexicalScope(node, search);
		return search;
	}
	
	/** Find the first token at given offset of given source (inclusive end).
	 * Initialize the lexer start position to given startPos position.
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
	
	public static boolean canCompleteInsideToken(Token token) {
		return !(token.type == DeeTokens.WHITESPACE || tokenIsAlphaNumeric(token));
	}
	
	public static boolean tokenIsAlphaNumeric(Token token) {
		return token.type == DeeTokens.IDENTIFIER || token.type.isKeyword();
	}
	
	public void setupPrefixedSearchOptions(String searchPrefix, int rplLen) {
		assertTrue(rplLen >= 0);
		searchOptions.searchPrefix = searchPrefix;
		searchOptions.namePrefixLen = searchOptions.searchPrefix.length();
		searchOptions.rplLen = rplLen;
	}
	
}