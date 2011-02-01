package mmrnmhrm.org.eclipse.dltk.ui.actions;


import mmrnmhrm.core.dltk.search.DeeDefPatternLocator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.callhierarchy.SearchUtil;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;
import org.eclipse.ui.IWorkingSet;

import dtool.ast.definitions.DefUnit;

public final class FindReferencesInWorkingSetAction extends FindAction {

	private IWorkingSet[] fWorkingSets;
	
	public FindReferencesInWorkingSetAction(ScriptEditor deeEditor) {
		super(deeEditor);
	}
	
	public FindReferencesInWorkingSetAction(ScriptEditor deeEditor, IWorkingSet[] workingSets) {
		super(deeEditor);
		fWorkingSets= workingSets;
	}
	
	@Override
	void init() {
		setText(SearchMessages.Search_FindReferencesInWorkingSetAction_label); 
		setToolTipText(SearchMessages.Search_FindReferencesInWorkingSetAction_tooltip); 
		setImageDescriptor(DLTKPluginImages.DESC_OBJS_SEARCH_REF);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_REFERENCES_IN_WORKING_SET_ACTION);
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add help supprot here...");
		}
	}

	
	@Override
	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
	//QuerySpecification createQuery(IModelElement element) throws ModelException {
		DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
		
		IWorkingSet[] workingSets= fWorkingSets;
		if (fWorkingSets == null) {
			workingSets= factory.queryWorkingSets();
			if (workingSets == null)
				return null;
		}
		SearchUtil.updateLRUWorkingSets(workingSets);
		IDLTKSearchScope scope= factory.createSearchScope(workingSets, true, getLanguageToolkit());
		String description= factory.getWorkingSetScopeDescription(workingSets, true);

		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(
				defunit.getName(), 0, true, getLimitTo(), scope, description);
	}


}