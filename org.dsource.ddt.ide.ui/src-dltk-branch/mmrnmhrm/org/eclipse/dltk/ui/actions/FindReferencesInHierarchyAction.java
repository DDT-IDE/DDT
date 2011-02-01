package mmrnmhrm.org.eclipse.dltk.ui.actions;


import mmrnmhrm.core.dltk.search.DeeDefPatternLocator;
import mmrnmhrm.core.model.SourceModelUtil;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;

import dtool.ast.definitions.DefUnit;

public final class FindReferencesInHierarchyAction extends FindAction {

	public FindReferencesInHierarchyAction(ScriptEditor deeEditor) {
		super(deeEditor);
	}
	
	@Override
	void init() {
		setText(SearchMessages.Search_FindHierarchyReferencesAction_label);
		setToolTipText(SearchMessages.Search_FindHierarchyReferencesAction_tooltip);
		setImageDescriptor(DLTKPluginImages.DESC_OBJS_SEARCH_REF);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_REFERENCES_IN_HIERARCHY_ACTION);
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add helkp support here...");
		}
	}
	
	@Override
	protected void runOperation(DefUnit defunit) {
		IMember member = SourceModelUtil.getTypeHandle(defunit);
		if(member instanceof IType) {
			super.runOperation(defunit);
		} else {
			OperationsManager.openWarning(getShell(), super.SEARCH_REFS, 
				"Element is not a type");
		}
	}
	
	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
		IType type = (IType) SourceModelUtil.getTypeHandle(defunit);
		if (type == null) {
			return super.createQuery(defunit);
		}
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		IDLTKSearchScope scope = SearchEngine.createHierarchyScope(type);
		String description = factory.getHierarchyScopeDescription(type);
		
		DeeDefPatternLocator.GLOBAL_param_defunit = defunit;
		return new PatternQuerySpecification(
				defunit.getName(), 0, true, getLimitTo(), scope, description);
		//return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}
	


}