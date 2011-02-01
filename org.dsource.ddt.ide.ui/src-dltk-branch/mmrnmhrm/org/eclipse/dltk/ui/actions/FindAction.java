package mmrnmhrm.org.eclipse.dltk.ui.actions;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.dltk.search.DeeDefPatternLocator;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchQuery;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.internal.ui.search.SearchUtil;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;

public abstract class FindAction extends SelectionDispatchAction {

	protected static final String SEARCH_REFS = "References";

	protected final ScriptEditor deeEditor;
	protected IWorkbenchSite fSite;

	public FindAction(ScriptEditor deeEditor) {
		super(deeEditor.getSite());
		this.deeEditor = deeEditor;
		this.fSite = deeEditor.getSite();
		init();
	}
	
	abstract void init();

	
	@Override
	public Shell getShell() {
		return fSite.getShell();
	}
	
	@Override
	public void run() {
		Module neoModule = EditorUtil.getNeoModuleFromEditor(deeEditor);

		TextSelection sel = EditorUtil.getSelection(deeEditor);
		int offset = sel.getOffset();
		ASTNeoNode elem = ASTNodeFinder.findNeoElement(neoModule, offset, false);
		run(elem);
	}

	private void run(ASTNeoNode elem) {
		if(elem instanceof DefSymbol) {
			DefSymbol defSymbol = (DefSymbol) elem;
			runOperation(defSymbol.getDefUnit());
		} else if(elem instanceof Reference) {
			Reference ref = (Reference) elem;
			DefUnit defunit = ref.findTargetDefUnit();
			if(defunit == null) {
				OperationsManager.openWarning(getShell(), SEARCH_REFS, 
				"No DefUnit found when resolving reference.");
			} else {
				runOperation(defunit);
			}
		} else {
			OperationsManager.openWarning(getShell(), SEARCH_REFS, 
					"Element is not a Definition nor a Reference");
		}
	}

	protected void runOperation(final DefUnit defUnit) {
		OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				performNewSearch(defUnit);
			}
		}, SEARCH_REFS);
	}

	protected void performNewSearch(DefUnit defunit) throws ModelException {
		assertNotNull(defunit);
		DLTKSearchQuery query= new DLTKSearchQuery(createQuery(defunit));
		if (query.canRunInBackground()) {
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the Interpreter verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case ISearchQuery results in Search plug-in being loaded).
			 */
			SearchUtil.runQueryInBackground(query);
		} else {
			IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
			/*
			 * This indirection with Object as parameter is needed to prevent the loading
			 * of the Search plug-in: the Interpreter verifies the method call and hence loads the
			 * types used in the method signature, eventually triggering the loading of
			 * a plug-in (in this case it would be ISearchQuery).
			 */
			IStatus status= NewSearchUI.runQueryInForeground(progressService, (ISearchQuery)query);
			if (status.matches(IStatus.ERROR | IStatus.INFO | IStatus.WARNING)) {
				ErrorDialog.openError(getShell(), SearchMessages.Search_Error_search_title, SearchMessages.Search_Error_search_message, status); 
			}
		}
	}

	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
		DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
		IDLTKSearchScope scope= factory.createWorkspaceScope(true, getLanguageToolkit());
		String description= factory.getWorkspaceScopeDescription(true);
		
		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(
				defunit.getName(), 0, true, getLimitTo(), scope, description);
		//return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return deeEditor.getLanguageToolkit();
	}
	
	protected int getLimitTo() {
		return IDLTKSearchConstants.REFERENCES;
	}
	
	String getOperationUnavailableMessage() {
		return "This operation is not available for the selected element."; 
	}
}