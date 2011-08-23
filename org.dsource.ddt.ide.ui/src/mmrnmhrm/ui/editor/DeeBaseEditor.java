package mmrnmhrm.ui.editor;

import mmrnmhrm.org.eclipse.dltk.ui.actions.ReferencesSearchGroup;

import org.dsource.ddt.lang.ui.editor.ScriptEditorLangExtension;
import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionGroup;

public abstract class DeeBaseEditor extends ScriptEditorLangExtension {
	
	protected BracketInserter fBracketInserter = new DeeBracketInserter(this);
	private ActionGroup fReferencesGroup;
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		boolean closeBrackets = true;
		boolean closeStrings = true;
		boolean closeAngularBrackets = false;
		
		fBracketInserter.setCloseBracketsEnabled(closeBrackets);
		fBracketInserter.setCloseStringsEnabled(closeStrings);
		fBracketInserter.setCloseAngularBracketsEnabled(closeAngularBrackets);
		
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(fBracketInserter);
		}
	}
	
	@Override
	public void dispose() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) sourceViewer).removeVerifyKeyListener(fBracketInserter);
		}
		super.dispose();
	}
	
	@Override
	protected void doSelectionChanged(SelectionChangedEvent event) {
		// This is the normal path
		super.doSelectionChanged(event);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		
		setAction("OpenTypeHierarchy", null);
		setAction("OpenCallHierarchy", null);
		
		Action dummyAction = new Action() { };
		setAction(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY, dummyAction);
		
		fReferencesGroup = new ReferencesSearchGroup(this, this.getLanguageToolkit());
		
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		menu.getItems();
		menu.remove("OpenTypeHierarchy");
		menu.remove("OpenCallHierarchy");
		
		menu.remove("org.eclipse.dltk.ui.refactoring.menu");
		
		menu.remove(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY);
		
		fReferencesGroup.fillContextMenu(menu);
		
	}
	
}

