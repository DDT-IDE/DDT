package mmrnmhrm.ui.editor;

import java.util.Iterator;

import mmrnmhrm.org.eclipse.dltk.ui.actions.ReferencesSearchGroup;

import org.dsource.ddt.ide.core.model.ASTUtils;
import org.dsource.ddt.lang.ui.editor.ScriptEditorLangExtension;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.internal.ui.editor.BracketInserter;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.actions.OpenViewActionGroup;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionGroup;

import dtool.ast.ASTNeoNode;

public abstract class DeeBaseEditor extends ScriptEditorLangExtension {
	
	protected BracketInserter fBracketInserter = new DeeBracketInserter(this);
	
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
		// XXX: DLTK copy 0.9
		ISourceReference reference = null;
		ISelection selection = event.getSelection();
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof ASTNeoNode) {
				reference = ASTUtils.adaptNodeToReference((ASTNeoNode)obj);
				break;
			}
		}
		if (!isActivePart() && DLTKUIPlugin.getActivePage() != null)
			DLTKUIPlugin.getActivePage().bringToTop(this);
		setSelection(reference, !isActivePart());
	}
	
	@SuppressWarnings("restriction")
	@Override
	protected void createActions() {
		super.createActions();
		//ActionGroup oeg = new OpenEditorActionGroup(this);
		ActionGroup ovg = new OpenViewActionGroup(this);
		//ActionGroup dsg = new SearchActionGroup(this);
		
		ActionGroup fReferencesGroup= new ReferencesSearchGroup(this, this.getLanguageToolkit());
		//fReadAccessGroup= new ReadReferencesSearchGroup(fEditor);
		//fWriteAccessGroup= new WriteReferencesSearchGroup(fEditor);
		//ActionGroup fDeclarationsGroup= new DeclarationsSearchGroup(this, this.getLanguageToolkit());
		
		
		fActionGroups = new org.eclipse.dltk.internal.ui.actions.
		CompositeActionGroup(new ActionGroup[] { 
				//oeg, 
				ovg, //dsg,
				fReferencesGroup, //fDeclarationsGroup
		});
		
		fContextMenuGroup = new org.eclipse.dltk.internal.ui.actions.
		CompositeActionGroup(new ActionGroup[] { 
				//oeg, 
				ovg, //dsg,
				fReferencesGroup, //fDeclarationsGroup
		});
	}
	
}

