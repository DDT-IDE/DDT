package mmrnmhrm.ui.editor;

import static melnorme.utilbox.core.CoreUtil.areEqual;
import mmrnmhrm.org.eclipse.dltk.ui.actions.ReferencesSearchGroup;

import org.dsource.ddt.lang.ui.editor.ScriptEditorLangExtension;
import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

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
		
		// This will deactivate the keybindings for these actions
		setAction("OpenTypeHierarchy", null);
		setAction("OpenCallHierarchy", null);
		
		fReferencesGroup = new ReferencesSearchGroup(this, this.getLanguageToolkit());
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		menu.remove("OpenEditor");
		menu.remove("OpenTypeHierarchy");
		menu.remove("OpenCallHierarchy");
		
		menu.remove(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY); // This is quick hierarchy action
		menu.remove("org.eclipse.dltk.ui.refactoring.menu");
		
		IContributionItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			if (areEqual(item.getId(), ITextEditorActionConstants.GROUP_FIND) && item instanceof IMenuManager) {
				menu.remove(item);
				break;
			}
		}
		
		fReferencesGroup.fillContextMenu(menu);
	}
	
}

