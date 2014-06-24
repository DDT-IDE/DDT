package mmrnmhrm.core.search;

import java.nio.file.Path;
import java.util.Collection;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.engine_client.DToolClient_Bad;
import mmrnmhrm.core.model_elements.DeeModelEngine;
import mmrnmhrm.lang.core.search.CommonLangPatternMatcher;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.engine.modules.IModuleResolver;

final class DeeNameNodeMatcher extends AbstractNodePatternMatcher {
	
	protected final CommonLangPatternMatcher patternMatcherHelper; // helps with wildcard string matching
	
	public final char[] simpleName;
	public final char[] qualification;
	
	public boolean matchTypes;
	public boolean matchVars;
	public boolean matchFunctions;

	
	public DeeNameNodeMatcher(DeeMatchLocator deeMatchLocator, SearchPattern pattern,
			boolean matchDefinitions, char[] simpleName, char[] qualification) {
		super(deeMatchLocator, matchDefinitions, !matchDefinitions);
		
		this.patternMatcherHelper = new CommonLangPatternMatcher(pattern);
		this.simpleName = simpleName;
		this.qualification = qualification;
	}
	
	
	@Override
	public boolean match(ASTNode node, ISourceModule sourceModule, Path filePath) {
		if(matchDeclarations) {
			if(node instanceof DefUnit) {
				matchDefUnit((DefUnit) node, sourceModule);
				return true;
			}
		}
		
		if(matchReferences && node instanceof NamedReference) {
			matchReference((NamedReference) node, sourceModule, filePath);
			return true;
		}
		return true;
	}
	
	
	public void matchReference(NamedReference node, ISourceModule sourceModule, Path filePath) {
		// don't match qualifieds, the match will be made in its children
		if(node instanceof CommonRefQualified)
			return;
		
		if(patternMatcherHelper.matchesName(simpleName, node.getCoreReferenceName().toCharArray())) {
			
			IModuleResolver mr = DToolClient_Bad.getResolverFor(filePath);
			Collection<INamedElement> defUnits = node.findTargetDefElements(mr, false);
			
			int matched = 0;
			int notMatched = 0;
			if(defUnits != null) {
				for (INamedElement defUnit : defUnits) {
					
					String[] qualificationArray = DeeModelEngine.getQualification(defUnit);
					char[] nodeQualification = StringUtil.collToString(qualificationArray, "$").toCharArray();
					
					if(patternMatcherHelper.matchesName(this.qualification, nodeQualification)) {
						matched++;
					} else {
						notMatched++;
					}
				}
			}
			
			if(matched > 0) {
				int accuracy = notMatched > 0 ? PatternLocator.POSSIBLE_MATCH : PatternLocator.ACCURATE_MATCH;
				deeMatchLocator.addMatch(node, accuracy, sourceModule);
			}
				
		}
	}
	
	public void matchDefUnit(DefUnit node, ISourceModule sourceModule) {
		
		if(patternMatcherHelper.matchesName(simpleName, node.getName().toCharArray())) {
			
			String[] qualificationArray = DeeModelEngine.getQualification(node);
			char[] nodeQualification = StringUtil.collToString(qualificationArray, "$").toCharArray();
			
			if(patternMatcherHelper.matchesName(this.qualification, nodeQualification)) {
				addMatch(node, PatternLocator.ACCURATE_MATCH, sourceModule);
			}
		}
		
	}
	
}