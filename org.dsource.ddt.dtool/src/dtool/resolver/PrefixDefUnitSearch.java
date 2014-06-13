package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTNode;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.ast.util.NamedElementUtil;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.IToken;
import dtool.parser.TokenListUtil;
import dtool.project.IModuleResolver;
import dtool.resolver.api.ECompletionResultStatus;
import dtool.resolver.api.PrefixSearchOptions;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions = new PrefixSearchOptions();
	
	protected final Set<String> addedDefElements = new HashSet<>();
	protected final ArrayList<INamedElement> results  = new ArrayList<>();
	
	protected ECompletionResultStatus resultCode = ECompletionResultStatus.RESULT_OK;
	
	public PrefixDefUnitSearch(Module refOriginModule, int refOffset, IModuleResolver moduleResolver) {
		super(refOriginModule, refOffset, moduleResolver);
	}
	
	public int getOffset() {
		return refOffset;
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
	public void addMatch(INamedElement namedElem) {
		String extendedName = namedElem.getExtendedName();
		
		if(addedDefElements.contains(extendedName)) {
			return;
		}
		addedDefElements.add(extendedName);
		addMatchDirectly(namedElem);
	}
	
	public void addMatchDirectly(INamedElement namedElem) {
		results.add(namedElem);
	}
	
	public ArrayList<INamedElement> getResults() {
		return results;
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(DeeParserResult parseResult, final int offset, 
		IModuleResolver mr) {
		
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());		
		
		Module module = parseResult.getModuleNode();
		
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(module, offset, mr);
		
		IToken tokenAtOffset = TokenListUtil.findTokenAtOffset(offset, parseResult);
		
		if((offset > tokenAtOffset.getStartPos() && offset < tokenAtOffset.getEndPos()) &&
			canCompleteInsideToken(tokenAtOffset)) {
			return search.assignResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
				"Invalid location (inside unmodifiable token)");
		}
		if(tokenAtOffset.getType().getGroupingToken() == DeeTokens.GROUP_FLOAT) {
			if(tokenAtOffset.getSourceValue().endsWith(".")) {
				return search.assignResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION, 
					"Invalid location (after float)");
			}
		}
		
		int correctedOffset = offset;
		String searchPrefix = "";
		if(tokenIsAlphaNumeric(tokenAtOffset)) {
			searchPrefix = tokenAtOffset.getSourceValue().substring(0, offset - tokenAtOffset.getStartPos());
			correctedOffset = tokenAtOffset.getStartPos();
		}
		int rplLen = 0;
		if(tokenIsAlphaNumeric(tokenAtOffset)) {
			rplLen = tokenAtOffset.getEndPos() - offset;
		}
		search.setupPrefixedSearchOptions(searchPrefix, rplLen);
		
		// Determine node that will be starting point to determine lookup scope.
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(module, correctedOffset, true);
		ASTNode node = nodeFinder.match;
		if(nodeFinder.matchOnLeft instanceof NamedReference) {
			NamedReference reference = (NamedReference) nodeFinder.matchOnLeft;
			if(reference.isMissingCoreReference()) {
				node = nodeFinder.matchOnLeft;
			}
		}
		
		if(node instanceof CommonRefQualified) {
			CommonRefQualified namedRef = (CommonRefQualified) node;
			if(offset <= namedRef.getDotOffset()) {
				search.assignResult(ECompletionResultStatus.INVALID_REFQUAL_LOCATION, 
						"Invalid Location: before qualifier dot but not next to id.");
				return search;
			}
			assertEquals(search.searchOptions.searchPrefix, "");
			assertEquals(search.searchOptions.namePrefixLen, 0);
			assertEquals(search.searchOptions.rplLen, 0);
		} else if(node instanceof RefModule) {
			RefModule refModule = (RefModule) node;
			// RefModule has a specialized way to setup prefix len things
			
			setupRefModuleSearchPrefix(search, tokenAtOffset, source, refModule);
		}
		
		node.performRefSearch(search);
		return search;
	}
	
	public static boolean canCompleteInsideToken(IToken token) {
		return !(token.getType() == DeeTokens.WHITESPACE || tokenIsAlphaNumeric(token));
	}
	
	public static boolean tokenIsAlphaNumeric(IToken token) {
		return token.getType() == DeeTokens.IDENTIFIER || token.getType().isKeyword();
	}
	
	public void setupPrefixedSearchOptions(String searchPrefix, int rplLen) {
		assertTrue(rplLen >= 0);
		searchOptions.searchPrefix = searchPrefix;
		searchOptions.namePrefixLen = searchOptions.searchPrefix.length();
		searchOptions.rplLen = rplLen;
	}
	
	public static void setupRefModuleSearchPrefix(PrefixDefUnitSearch search, IToken tokenAtOffset,
		String source, RefModule refModule) {
		final int offset = search.getOffset();
		
		int idEnd = refModule.getEndPos();
		if(refModule.isMissingCoreReference()) {
			if(tokenAtOffset.getType().isKeyword() && tokenAtOffset.getEndPos() > refModule.getEndPos()) {
				idEnd = tokenAtOffset.getEndPos(); // Fix for attached keyword ids
			} else {
				idEnd = refModule.moduleToken.getFullRangeStartPos();
			}
		}
		int rplLen = offset > idEnd ? 0 : idEnd - offset;
		
		// We reparse the snipped source as it's the easiest way to determine search prefix
		String moduleQualifiedNameSnippedSource = source.substring(refModule.getStartPos(), offset);
		DeeParser parser = new DeeParser(moduleQualifiedNameSnippedSource);
		String moduleQualifiedNameCanonicalPrefix = parser.parseRefModule().toStringAsCode();
		DeeTokens lookAhead = parser.lookAhead();
		if(lookAhead != DeeTokens.EOF) {
			assertTrue(lookAhead.isKeyword());
			moduleQualifiedNameCanonicalPrefix += lookAhead.getSourceValue();
		}
		search.setupPrefixedSearchOptions(moduleQualifiedNameCanonicalPrefix, rplLen);
		search.searchOptions.isImportModuleSearch = true;
	}
	
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "searchPrefix: " + searchOptions.searchPrefix +"\n";
		str += "----- Results: -----\n";
		str += StringUtil.iterToString(results, "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return NamedElementUtil.getElementTypedQualification(obj); 
			}
		});
		return str;
	}
}