package mmrnmhrm.ui.editor;

import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.swt.events.VerifyEvent;

public class DeeBracketInserter extends BracketInserter {

	public DeeBracketInserter(ScriptEditor editor) {
		super(editor);
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		// early pruning to slow down normal typing as little as possible
		if (!event.doit
				|| this.editor.getInsertMode() != ScriptEditor.SMART_INSERT)
			return;
		switch (event.character) {
		case '(':
		case '<':
		case '[':
		case '\'':
		case '\"':
			break;
		default:
			return;
		}
		// TODO DTLK  DeeBracketInserter
	}

}
