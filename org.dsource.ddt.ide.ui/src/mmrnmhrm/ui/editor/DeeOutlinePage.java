package mmrnmhrm.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.swtutil.SWTUtilExt;
import melnorme.utilbox.tree.IElement;

import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.actions.MemberFilterActionGroup;
import org.eclipse.dltk.ui.viewsupport.MemberFilterAction;
import org.eclipse.dltk.ui.viewsupport.ModelElementFilter;
import org.eclipse.dltk.ui.viewsupport.ModelElementFlagsFilter;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import descent.internal.compiler.parser.ast.ASTNode;

/**
 * TODO: DLTK request: we would like {@link #isInnerType()}  to be protected so we can extends
 */
public class DeeOutlinePage extends ScriptOutlinePage {
	
	public DeeOutlinePage(ScriptEditor editor, IPreferenceStore store) {
		super(editor, store);
	}
	
	@Override
	protected ILabelDecorator getLabelDecorator() {
		return null;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		fOutlineViewer.setContentProvider(new DeeOutlinePageContentProvider(this));
		
//		fOutlineViewer.expandAll();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Deprecated
	protected final class DeeOutlinePageContentProvider extends DeeOutlineContentProvider {
		
		private final class ElementChangedListener implements IElementChangedListener {
			@Override
			public void elementChanged(ElementChangedEvent event) {
				SWTUtilExt.runInSWTThread(new Runnable() {
					@Override
					public void run() {
						if(getControl() == null || fOutlineViewer == null)
							return; // may have been disposed meanwhile
						deeOutlinePage.getControl().setRedraw(false);
						deeOutlinePage.fOutlineViewer.refresh();
						deeOutlinePage.fOutlineViewer.expandAll();
						deeOutlinePage.getControl().setRedraw(true);
					}
				});
			}
		}
		
		private final DeeOutlinePage deeOutlinePage;
		
		protected DeeOutlinePageContentProvider(DeeOutlinePage deeOutlinePage) {
			this.deeOutlinePage = deeOutlinePage;
		}
		
		private IElementChangedListener fListener;
		
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			boolean isCU = (newInput instanceof ISourceModule);
			
			if (isCU && fListener == null) {
				fListener = new ElementChangedListener();
				DLTKCore.addElementChangedListener(fListener);
			} else if (!isCU && fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}
		
		@Override
		public void dispose() {
			if (fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}
		
		@Override
		public boolean hasChildren(Object element) {
			return super.hasChildren(element);
		}
		
		
		@Override
		public Object[] getChildren(Object element) {
			return super.getChildren(element);
		}
		
		@Override
		public Object[] getElements(Object parent) {
			if(parent instanceof ISourceModule) {
				ISourceModule sourceModule = (ISourceModule) parent;
				DeeModuleDeclaration moduleDec = DeeModelUtil.getParsedDeeModule(sourceModule);
				if(moduleDec != null)
					return super.getElements(moduleDec.neoModule);
			}
			return ASTNode.NO_ELEMENTS;
		}
		
		@Override
		public Object getParent(Object element) {
			if(element instanceof IElement)
				return super.getParent(element);
			return null;
		}
		
	}
	
	@Override
	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		// XXX: DLTK TCL 2.0 copied code: 
		
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		MemberFilterActionGroup fMemberFilterActionGroup = new MemberFilterActionGroup(
				fOutlineViewer, fStore);
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
