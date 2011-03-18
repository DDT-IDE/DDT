package mmrnmhrm.ui.editor;

import melnorme.swtutil.SWTUtilExt;
import melnorme.utilbox.tree.IElement;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.DeeParserUtil;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import descent.internal.compiler.parser.ast.ASTNode;
import dtool.Logg;


public class DeeOutlinePage extends ScriptOutlinePage {
	
	public DeeOutlinePage(ScriptEditor editor, IPreferenceStore store) {
		super(editor, store);
	}
	
	public final class DeeOutlinePageContentProvider extends DeeOutlineContentProvider {
		
		private final class ElementChangedListener implements IElementChangedListener {
			@Override
			public void elementChanged(org.eclipse.dltk.core.ElementChangedEvent event) {
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
		
		DeeOutlinePageContentProvider(DeeOutlinePage deeOutlinePage) {
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
			Logg.main.println("OutlinePageContentProvider disposing:" + this);
			if (fListener != null) {
				Logg.main.println("OutlinePageContentProvider remove listener:" + this);
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
			//super.dispose();
			Logg.main.println("OutlinePageContentProvider disposed:" + this);
			//ModelManager.getModelManager().deltaState.elementChangedListeners.clone();
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
				DeeModuleDeclaration moduleDec = DeeParserUtil.getParsedDeeModule(sourceModule);
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
	
	public static class DeeOutlineLabelDecorator extends DeeOutlineLabelProvider
			implements ILabelDecorator {
		@Override
		public Image decorateImage(Image image, Object element) {
			if(element instanceof IElement)
				return getImage(element);
			return null;
		}
		@Override
		public String decorateText(String text, Object element) {
			if(element instanceof IElement)
				return getText(element);
			return null;
		}
	}
	
	@Override
	protected ILabelDecorator getLabelDecorator() {
		return new DeeOutlineLabelDecorator(); 
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		fOutlineViewer.setComparator(null);
		fOutlineViewer.setContentProvider(new DeeOutlinePageContentProvider(this));
		fOutlineViewer.setLabelProvider(new DeeOutlineLabelDecorator());
		fOutlineViewer.expandAll();
	}
	
	@Override
	public void dispose() {
		Logg.main.println("OutlinePage disposing:" + this);
		super.dispose();
		Logg.main.println("OutlinePage disposed:" + this);
	}
	
	
	
	//TODO convert
/*
	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		// TODO: help support

		IToolBarManager toolBarManager = actionBars.getToolBarManager();

		MemberFilterActionGroup fMemberFilterActionGroup = new MemberFilterActionGroup(
				fOutlineViewer, fStore); //$NON-NLS-1$

		String title, helpContext;
		ArrayList actions = new ArrayList(3);

		// Hide variables
		title = ActionMessages.MemberFilterActionGroup_hide_variables_label;

		helpContext = "";// IDLTKHelpContextIds.FILTER_FIELDS_ACTION;
		MemberFilterAction hideVariables = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.FIELD), helpContext, true);
		hideVariables
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_variables_description);
		hideVariables
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_variables_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideVariables,
				"filter_fields.gif"); //$NON-NLS-1$
		actions.add(hideVariables);

		// Hid functions
		title = ActionMessages.MemberFilterActionGroup_hide_functions_label;
		helpContext = "";// IDLTKHelpContextIds.FILTER_STATIC_ACTION;
		MemberFilterAction hideProcedures = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.METHOD), helpContext, true);
		hideProcedures
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_functions_description);
		hideProcedures
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_functions_tooltip);
		// TODO: add correct icon
		DLTKPluginImages.setLocalImageDescriptors(hideProcedures,
				"filter_methods.gif"); //$NON-NLS-1$
		actions.add(hideProcedures);

		// Hide classes
		title = ActionMessages.MemberFilterActionGroup_hide_classes_label;
		helpContext = "";// IDLTKHelpContextIds.FILTER_PUBLIC_ACTION;
		MemberFilterAction hideNamespaces = new MemberFilterAction(
				fMemberFilterActionGroup, title, new ModelElementFilter(
						IModelElement.TYPE), helpContext, true);
		hideNamespaces
				.setDescription(ActionMessages.MemberFilterActionGroup_hide_classes_description);
		hideNamespaces
				.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_classes_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(hideNamespaces,
				"filter_classes.gif"); //$NON-NLS-1$
		actions.add(hideNamespaces);

		// Adding actions to toobar
		MemberFilterAction[] fFilterActions = (MemberFilterAction[]) actions
				.toArray(new MemberFilterAction[actions.size()]);

		fMemberFilterActionGroup.setActions(fFilterActions);
		fMemberFilterActionGroup.contributeToToolBar(toolBarManager);
	}*/
	
	
}
