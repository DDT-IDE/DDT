package mmrnmhrm.org.eclipse.dltk.ui.actions;


import melnorme.lang.ide.ui.actions.UIUserInteractionsHelper;
import mmrnmhrm.core.model_elements.DeeModelEngine;
import mmrnmhrm.core.search.DeeDefPatternLocator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.internal.ui.search.SearchMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;
import dtool.ast.definitions.DefUnit;
import dtool.engine.common.IDeeNamedElement;

public final class FindReferencesInHierarchyAction extends FindAction {

	public FindReferencesInHierarchyAction(ScriptEditor2 deeEditor) {
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
	
//	@Override
//	protected void runOperation(INamedElement defunit) {
//		if(defunit.getArcheType().isType()) {
//			super.runOperation(defunit);
//		} else {
//			UIUserInteractionsHelper.openWarning(getShell(), super.SEARCH_REFS, 
//				"Element is not a type");
//		}
//	}
	
	protected QuerySpecification createQuery(DefUnit defunit) throws ModelException {
		ISourceModule sourceModule = EditorUtility.getEditorInputModelElement(deeEditor, false);
		IType type = (IType) DeeModelEngine.findCorrespondingModelElement(defunit, sourceModule);
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