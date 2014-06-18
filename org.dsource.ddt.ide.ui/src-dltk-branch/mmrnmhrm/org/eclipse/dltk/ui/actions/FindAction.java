package mmrnmhrm.org.eclipse.dltk.ui.actions;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.search.DeeDefPatternLocator;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.actions.AbstractEditorOperation;
import mmrnmhrm.ui.actions.UIUserInteractionsHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
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

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.resolver.ResolverUtil;
import dtool.resolver.ResolverUtil.ModuleNameDescriptor;

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
		TextSelection sel = EditorUtil.getSelection(deeEditor);
		final int offset = sel.getOffset();
		
		new FindReferencesOperation(offset).executeHandled();
		
	}
	
	protected class FindReferencesOperation extends AbstractEditorOperation {
		
		protected final int offset;
		
		protected INamedElement defunit;
		protected String errorMessage;
		
		public FindReferencesOperation(int offset) {
			super(SEARCH_REFS, deeEditor);
			this.offset = offset;
		}
		
		@Override
		protected void performOperation_do() throws ModelException {
			if(errorMessage != null) {
				UIUserInteractionsHelper.openWarning(getShell(), SEARCH_REFS, errorMessage);
			}
			if(defunit != null) {
				startNewSearch(defunit);
			}
		}
		
		@Override
		protected void performLongRunningComputation_do() {
			Module neoModule = DToolClient.getDefault().getModuleNodeOrNull(sourceModule);
			ASTNode elem = ASTNodeFinder.findElement(neoModule, offset);
			if(elem instanceof DefSymbol) {
				DefSymbol defSymbol = (DefSymbol) elem;
				defunit = defSymbol.getDefUnit();
			} else if(elem instanceof Reference) {
				Reference ref = (Reference) elem;
				IScriptProject scriptProject = deeEditor.getInputModelElement().getScriptProject();
				defunit = ref.findTargetDefElement(new DeeProjectModuleResolver(scriptProject));
				if(defunit == null) {
					errorMessage = "No DefUnit found when resolving reference.";
				}
			} else {
				errorMessage = "Element is not a Definition nor a Reference";
			}
		}
	}
	
	protected void startNewSearch(INamedElement defunit) throws ModelException {
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

	protected QuerySpecification createQuery(INamedElement defunit) throws ModelException {
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
	
	protected boolean isInsideInterpreterEnv(INamedElement defunit, DLTKSearchScopeFactory factory) throws ModelException {
		IScriptProject scriptProject = deeEditor.getInputModelElement().getScriptProject();
		DeeProjectModuleResolver mr = new DeeProjectModuleResolver(scriptProject);
		
		boolean isInsideInterpreterEnvironment;
		String moduleFQName = defunit.getModuleFullyQualifiedName();
		if(moduleFQName == null) {
			isInsideInterpreterEnvironment = false;
		} else {
			ModuleNameDescriptor nameDescriptor = ResolverUtil.getNameDescriptor(moduleFQName);
			ISourceModule element = mr.findModuleUnit(nameDescriptor.packages, nameDescriptor.moduleName, null);
			// review this
			isInsideInterpreterEnvironment = element == null? false : factory.isInsideInterpreter(element);
		}
		return isInsideInterpreterEnvironment;
	}
	
}