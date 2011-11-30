package mmrnmhrm.core.search;

import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.AbstractSearchFactory;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class DeeSearchFactory extends AbstractSearchFactory {
	
	// commented since DLTK 2.0. became deprecated
//	@Override
//	public MatchLocator createMatchLocator(SearchPattern pattern, SearchRequestor requestor,
//			IDLTKSearchScope scope, SubProgressMonitor monitor) {
//		return new DeeMatchLocator(pattern, requestor, scope, monitor);
//	}
	
	@Override
	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new DeeMatchLocatorParser(locator);
		//return new RubyMatchLocatorParser(locator);
	}
	
	@Override
	public ISearchPatternProcessor createSearchPatternProcessor() {
		return DeeSearchPatterProcessor.instance;
		//return new RubySearchPatternProcessor();
	}
	
}
