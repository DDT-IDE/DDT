package mmrnmhrm.ui.internal.text;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class LangWhitespaceDetector implements IWhitespaceDetector {
	
	@Override
	public boolean isWhitespace(char character) {
		return Character.isWhitespace(character);
	}
}
