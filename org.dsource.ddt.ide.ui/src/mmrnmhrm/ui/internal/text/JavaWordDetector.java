package mmrnmhrm.ui.internal.text;

import org.eclipse.jface.text.rules.IWordDetector;

public class JavaWordDetector implements IWordDetector {
	
	@Override
	public boolean isWordPart(char character) {
		return Character.isJavaIdentifierPart(character);
	}
	
	@Override
	public boolean isWordStart(char character) {
		return Character.isJavaIdentifierPart(character);
	}
}
