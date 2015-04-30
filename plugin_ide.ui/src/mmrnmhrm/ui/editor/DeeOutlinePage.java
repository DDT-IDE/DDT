package mmrnmhrm.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.LangUIPlugin;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.actions.MemberFilterActionGroup;
import org.eclipse.dltk.ui.viewsupport.MemberFilterAction;
import org.eclipse.dltk.ui.viewsupport.ModelElementFilter;
import org.eclipse.dltk.ui.viewsupport.ModelElementFlagsFilter;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import _org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;

/**
 * TODO: DLTK request: we would like {@link #isInnerType()}  to be protected so we can extends
 */
public class DeeOutlinePage extends ScriptOutlinePage {
	
	public DeeOutlinePage(ScriptEditor editor) {
		super(editor, LangUIPlugin.getInstance().getPreferenceStore());
	}
	
	@Override
	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		// XXX: DLTK TCL 3.0 copied and modified code: 
		
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		MemberFilterActionGroup fMemberFilterActionGroup = new MemberFilterActionGroup(
				getTreeViewer(), fStore);
		List<MemberFilterAction> actions = new ArrayList<MemberFilterAction>(3);
		
		// variables
		// TODO help support IDLTKHelpContextIds.FILTER_FIELDS_ACTION;
		MemberFilterAction hideVariables = new MemberFilterAction(
				fMemberFilterActionGroup,
				ActionMessages.MemberFilterActionGroup_hide_variables_label,
				new ModelElementFilter(IModelElement.FIELD), null, true);
		hideVariables.setDescription(ActionMessages.MemberFilterActionGroup_hide_variables_description);
		hideVariables.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_variables_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideVariables, "filter_fields.gif"); //$NON-NLS-1$
		actions.add(hideVariables);

		// procedures
		// TODO help support IDLTKHelpContextIds.FILTER_STATIC_ACTION;
		MemberFilterAction hideProcedures = new MemberFilterAction(
				fMemberFilterActionGroup,
				ActionMessages.MemberFilterActionGroup_hide_functions_label,
				new ModelElementFilter(IModelElement.METHOD), null, true);
		hideProcedures.setDescription(ActionMessages.MemberFilterActionGroup_hide_functions_description);
		hideProcedures.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_functions_tooltip);
		// TODO: add correct icon
		DLTKPluginImages.setLocalImageDescriptors(hideProcedures,
				"filter_methods.gif"); //$NON-NLS-1$
		actions.add(hideProcedures);

		// private
		MemberFilterAction hidePrivate = new MemberFilterAction(
				fMemberFilterActionGroup,
				ActionMessages.MemberFilterActionGroup_hide_private_label,
				new ModelElementFlagsFilter(Modifiers.AccPrivate), null, true);
		hidePrivate.setDescription(ActionMessages.MemberFilterActionGroup_hide_private_description);
		hidePrivate.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_private_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hidePrivate,
				"filter_private.gif"); //$NON-NLS-1$
		actions.add(hidePrivate);

		// order corresponds to ordeutilusr in toolbar
		MemberFilterAction[] fFilterActions = actions.toArray(new MemberFilterAction[actions.size()]);
		fMemberFilterActionGroup.setActions(fFilterActions);
		fMemberFilterActionGroup.contributeToToolBar(toolBarManager);
	}
	
}