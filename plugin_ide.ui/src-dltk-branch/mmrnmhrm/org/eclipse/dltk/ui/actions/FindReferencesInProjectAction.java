package mmrnmhrm.org.eclipse.dltk.ui.actions;


import melnorme.lang.tooling.symbols.INamedElement;
import mmrnmhrm.core.search.DeeDefPatternLocator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;

public final class FindReferencesInProjectAction extends FindAction {

	public FindReferencesInProjectAction(ScriptEditor2 deeEditor) {
		super(deeEditor);
	}
	
	@Override
	void init() {
		setText(SearchMessages.Search_FindReferencesInProjectAction_label); 
		setToolTipText(SearchMessages.Search_FindReferencesInProjectAction_tooltip); 
		setImageDescriptor(DLTKPluginImages.DESC_OBJS_SEARCH_REF);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_REFERENCES_IN_PROJECT_ACTION);
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add help support here...");
		}
	}

	@Override
	protected QuerySpecification createQuery(INamedElement defunit) throws ModelException {
		DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
		ScriptEditor2 editor= deeEditor;
		
		IScriptProject scriptProject = deeEditor.getInputModelElement().getScriptProject();
		
		IDLTKSearchScope scope;
		String description;
		boolean isInsideInterpreterEnvironment = isInsideInterpreterEnv(defunit, factory);
		if (editor != null) {
			scope= factory.createProjectSearchScope(editor.getEditorInput(), isInsideInterpreterEnvironment);
			description= factory.getProjectScopeDescription(editor.getEditorInput(), isInsideInterpreterEnvironment);
		} else {
			scope= factory.createProjectSearchScope(scriptProject, isInsideInterpreterEnvironment);
			description=  factory.getProjectScopeDescription(scriptProject, isInsideInterpreterEnvironment);
		}
		
		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(defunit.getName(), 0, true, getLimitTo(), scope, description);
	}
	
}