package mmrnmhrm.org.eclipse.dltk.ui.actions;


import mmrnmhrm.core.dltk.search.DeeDefPatternLocator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;

import dtool.ast.definitions.DefUnit;

public final class FindReferencesInProjectAction extends FindAction {

	public FindReferencesInProjectAction(ScriptEditor deeEditor) {
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
	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
		DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
		ScriptEditor editor= deeEditor;
		
		ISourceModule element = defunit.getModuleNode().getModuleUnit();

		IDLTKSearchScope scope;
		String description;
		boolean isInsideInterpreterEnvironment= factory.isInsideInterpreter(element);
		if (editor != null) {
			scope= factory.createProjectSearchScope(editor.getEditorInput(), isInsideInterpreterEnvironment);
			description= factory.getProjectScopeDescription(editor.getEditorInput(), isInsideInterpreterEnvironment);
		} else {
			scope= factory.createProjectSearchScope(element.getScriptProject(), isInsideInterpreterEnvironment);
			description=  factory.getProjectScopeDescription(element.getScriptProject(), isInsideInterpreterEnvironment);
		}
		
		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(
				defunit.getName(), 0, true, getLimitTo(), scope, description);
	}


}