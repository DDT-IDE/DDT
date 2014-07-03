package mmrnmhrm.core.search;

import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.AbstractSearchFactory;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class DeeSearchFactory extends AbstractSearchFactory {
	
	@Override
	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new DeeMatchLocator.DeeMatchLocatorParser(locator);
	}
	
	@Override
	public ISearchPatternProcessor createSearchPatternProcessor() {
		return DeeSearchPatternProcessor.instance;
	}
	
}
