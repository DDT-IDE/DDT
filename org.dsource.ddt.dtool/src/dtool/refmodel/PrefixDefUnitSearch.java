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
import dtool.ast.references.RefQualified;
import dtool.ast.references.Reference;
import dtool.contentassist.CompletionSession;
import dtool.contentassist.CompletionSession.ECompletionSessionResults;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.refmodel.pluginadapters.IModuleResolver;

/** 
 * Class that does a scoped name lookup for matches that start with a given prefix name. 
 * TODO: The matches with the same name as matches in a scope with higher 
 * priority are not added.
 */
public class PrefixDefUnitSearch extends CommonDefUnitSearch {
	
	public final PrefixSearchOptions searchOptions;
	private final IDefUnitMatchAccepter defUnitAccepter;
	
	private final Set<String> addedDefUnits = new HashSet<String>();
	
	public PrefixDefUnitSearch(PrefixSearchOptions searchOptions, IScopeNode refScope, int refOffset,
			IDefUnitMatchAccepter defUnitAccepter, IModuleResolver moduleResolver) {
		super(refScope, refOffset, moduleResolver);
		this.searchOptions = searchOptions;
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
	
	public static interface IDefUnitMatchAccepter {
		void accept(DefUnit defUnit, PrefixSearchOptions searchOptions);
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(CompletionSession session, String defaultModuleName,
			String source, final int offset, IModuleResolver modResolver, IDefUnitMatchAccepter defUnitAccepter) 
	{
		DeeParserResult parseResult = DeeParser.parseSource(source, defaultModuleName);
		
		return doCompletionSearch(session, parseResult, offset, modResolver, defUnitAccepter);
	}
	
	public static PrefixDefUnitSearch doCompletionSearch(CompletionSession session, DeeParserResult parseResult,
		final int offset, IModuleResolver modResolver, IDefUnitMatchAccepter defUnitAccepter) {
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());
		assertTrue(session.errorMsg == null);
		session.resultCode = ECompletionSessionResults.RESULT_OK;
		
		
		if(parseResult == null) {
			/*BUG here TODO: need to reimplement, check token list*/
			CompletionSession.assignResult(session, ECompletionSessionResults.INVALID_LOCATION_INTOKEN, 
				"Invalid location (inside token)");
			return null;
		}
		
		Module neoModule = parseResult.getParsedModule(); 
		
		/* ============================================== */
		// : Do actual completion search
		
		
		ASTNode node = ASTNodeFinder.findElement(neoModule, offset);
		assertNotNull(node);
		
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		IScopeNode refScope = ScopeUtil.getScopeNode(node);
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(searchOptions, refScope, offset, defUnitAccepter,
				modResolver);
		
		
		if(node instanceof NamedReference)  {
			NamedReference namedRef = (NamedReference) node;
			
			if(node instanceof RefIdentifier) {
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
					CompletionSession.assignResult(session, ECompletionSessionResults.WEIRD_LOCATION_REFQUAL, 
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
			CompletionSession.assignResult(session, ECompletionSessionResults.NOTIMPLEMENTED, 
					"Don't know how to complete for node: "+node+" (DDT TODO)");
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
					CompletionSession.assignResult(session, ECompletionSessionResults.INVALID_LOCATION_INSCOPE, 
							"Invalid location in scope");
					return search;
				}
			}
			
			ReferenceResolver.findDefUnitInExtendedScope(scope, search);
		}
		
		assertTrue(session.errorMsg == null);
		return search;
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