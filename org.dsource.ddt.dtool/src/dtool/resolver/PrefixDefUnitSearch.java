package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;
import java.util.Set;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
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
		Token token = findTokenAtOffset(offset, source, relexStartPos);
		
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(node, offset, defUnitAccepter, mr);
		search.relexStartPos = relexStartPos;
		
		if((offset > token.getStartPos() && offset < token.getEndPos()) && 
			!(token.type == DeeTokens.WHITESPACE || token.type == DeeTokens.IDENTIFIER)) {
			/*BUG here needs to be identifier token*/
			return search.assignResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
				"Invalid location (inside unmodifiable token)");
		}
		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			namedRef.performPrefixSearch(search, source);
		} else if(node instanceof Reference) {
			search.assignResult(ECompletionResultStatus.OTHER_REFERENCE, 
				"Can't complete for node: "+node.getNodeType()+"");
			return search;
		} else {
			ReferenceResolver.resolveSearchInFullLexicalScope(node, search);
		}
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
	
	public void setupPrefixedSearchOptions(int nameOffset, String name) {
		int offset = getOffset();
		assertTrue(offset >= nameOffset);
		assertTrue(offset <= nameOffset + name.length() || name.isEmpty());
		// empty name is a special case
		int namePrefixLen = name.isEmpty() ? 0 : offset - nameOffset;
		
		int rplLen = name.length() - namePrefixLen;
		String searchPrefix = name.substring(0, namePrefixLen);
		setupPrefixedSearchOptions(searchPrefix, rplLen);
	}
	
	public void setupPrefixedSearchOptions(String searchPrefix, int rplLen) {
		assertTrue(rplLen >= 0);
		searchOptions.searchPrefix = searchPrefix;
		searchOptions.namePrefixLen = searchOptions.searchPrefix.length();
		searchOptions.rplLen = rplLen;
	}
	
}