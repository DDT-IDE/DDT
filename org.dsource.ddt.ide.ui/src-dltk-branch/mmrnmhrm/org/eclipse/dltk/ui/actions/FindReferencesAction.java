package mmrnmhrm.org.eclipse.dltk.ui.actions;


import mmrnmhrm.core.dltk.search.DeeDefPatternLocator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;

import dtool.ast.definitions.DefUnit;

public final class FindReferencesAction extends FindAction {

	public FindReferencesAction(ScriptEditor deeEditor) {
		super(deeEditor);
	}
	
	@Override
	void init() {
		setText(SearchMessages.Search_FindReferencesAction_label);
		setToolTipText(SearchMessages.Search_FindReferencesAction_tooltip);
		setImageDescriptor(DLTKPluginImages.DESC_OBJS_SEARCH_REF);
		setActionDefinitionId(
				IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_WORKSPACE); 
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_REFERENCES_IN_WORKSPACE_ACTION);
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add help support here...");
		}
	}

	@Override
	public int getLimitTo() {
		return IDLTKSearchConstants.REFERENCES;
	}
	
	@Override
	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		ISourceModule element = defunit.getModuleNode().getModuleUnit();
		boolean isInsideInterpreterEnvironment = factory.isInsideInterpreter(element);

		IDLTKSearchScope scope = factory.createWorkspaceScope(isInsideInterpreterEnvironment, getLanguageToolkit());
		String description = factory.getWorkspaceScopeDescription(isInsideInterpreterEnvironment);
		
		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(
				defunit.getName(), 0, true, getLimitTo(), scope, description);
		//return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

}