package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;

public class DeeSourceParserFactory implements ISourceParserFactory {
	
	@Override
	public ISourceParser createSourceParser() {
		return new DeeSourceParser();
	}
	
}