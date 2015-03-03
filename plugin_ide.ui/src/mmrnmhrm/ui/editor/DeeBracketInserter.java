package mmrnmhrm.ui.editor;

import org.eclipse.swt.events.VerifyEvent;

import _org.eclipse.dltk.internal.ui.editor.BracketInserter2;
import _org.eclipse.dltk.internal.ui.editor.ScriptEditor;

public class DeeBracketInserter extends BracketInserter2 {

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
