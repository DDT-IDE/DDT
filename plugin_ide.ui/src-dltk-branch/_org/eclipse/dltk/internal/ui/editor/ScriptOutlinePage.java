/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.dltk.annotations.NonNull;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.actions.AbstractToggleLinkingAction;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.IScriptEditor;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IContextMenuConstants;
import org.eclipse.dltk.ui.MembersOrderPreferenceCache;
import org.eclipse.dltk.ui.ModelElementSorter;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.ProblemsLabelDecorator.ProblemsLabelChangedEvent;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.actions.CustomFiltersActionGroup;
import org.eclipse.dltk.ui.actions.MemberFilterActionGroup;
import org.eclipse.dltk.ui.actions.OpenViewActionGroup;
import org.eclipse.dltk.ui.actions.SearchActionGroup;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.dltk.ui.viewsupport.DecoratingModelLabelProvider;
import org.eclipse.dltk.ui.viewsupport.SourcePositionSorter;
import org.eclipse.dltk.ui.viewsupport.StatusBarUpdater;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import _org.eclipse.dltk.internal.ui.actions.CompositeActionGroup;

/**
 * The content outline page of the Java editor. The viewer implements a
 * proprietary update mechanism based on Java model deltas. It does not react on
 * domain changes. It is specified to show the content of ICompilationUnits and
 * IClassFiles. Publishes its context menu under
 * <code>JavaPlugin.getDefault().getPluginId() + ".outline"</code>.
 */
public class ScriptOutlinePage extends Page implements IContentOutlinePage,
		IAdaptable, IPostSelectionProvider {

	/**
	 * Content provider for the children of an ICompilationUnit or an IClassFile
	 * 
	 * @see ITreeContentProvider
	 */
	protected class ChildrenProvider implements ITreeContentProvider {

		// private Object[] NO_CLASS = new Object[] { new NoClassElement() };
		private ElementChangedListener fListener;

		@Override
		public void dispose() {
			if (fListener != null) {
				DLTKCore.removeElementChangedListener(fListener);
				fListener = null;
			}
		}

		protected IModelElement[] filter(IModelElement[] children) {
			boolean initializers = false;
			for (int i = 0; i < children.length; i++) {
				if (matches(children[i])) {
					initializers = true;
					break;
				}
			}

			if (!initializers) {
				return children;
			}

			List<IModelElement> v = new ArrayList<IModelElement>();
			for (int i = 0; i < children.length; i++) {
				if (matches(children[i])) {
					continue;
				}
				v.add(children[i]);
			}
			return v.toArray(new IModelElement[v.size()]);
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c = (IParent) parent;
				try {
					return filter(c.getChildren());
				} catch (ModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (DLTKCore.DEBUG || !x.isDoesNotExist()) {
						DLTKUIPlugin.log(x);
					}
				}
			}
			return ScriptOutlinePage.NO_CHILDREN;
		}

		@Override
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof IModelElement) {
				IModelElement e = (IModelElement) child;
				return e.getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof IParent) {
				IParent c = (IParent) parent;
				try {
					IModelElement[] children = filter(c.getChildren());
					return (children != null && children.length > 0);
				} catch (ModelException x) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
					// don't log NotExist exceptions as this is a valid case
					// since we might have been posted and the element
					// removed in the meantime.
					if (DLTKUIPlugin.isDebug() || !x.isDoesNotExist()) {
						DLTKUIPlugin.log(x);
					}
				}
			}
			return false;
		}

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

		protected boolean matches(IModelElement element) {
			if (element.getElementType() == IModelElement.METHOD) {
				String name = element.getElementName();
				return (name != null && name.indexOf('<') >= 0);
			}
			return false;
		}
	}

	/**
	 * The element change listener of the java outline viewer.
	 * 
	 * @see IElementChangedListener
	 */
	protected class ElementChangedListener implements IElementChangedListener {

		@Override
		public void elementChanged(final ElementChangedEvent e) {

			if (getControl() == null) {
				return;
			}

			Display d = getControl().getDisplay();
			if (d != null) {
				d.asyncExec(new Runnable() {
					@Override
					public void run() {
						ISourceModule cu = (ISourceModule) fInput;
						IModelElement base = cu;

						IModelElementDelta delta = findElement(base, e.getDelta());
						if (delta != null && fOutlineViewer != null) {
							fOutlineViewer.reconcile(delta);
						}
					}
				});
			}
		}

		protected IModelElementDelta findElement(IModelElement unit,
				IModelElementDelta delta) {

			if (delta == null || unit == null) {
				return null;
			}

			IModelElement element = delta.getElement();

			if (unit.equals(element)) {
				if (isPossibleStructuralChange(delta)) {
					return delta;
				}
				return null;
			}

			if (element.getElementType() > IModelElement.SOURCE_MODULE) {
				return null;
			}

			IModelElementDelta[] children = delta.getAffectedChildren();
			if (children == null || children.length == 0) {
				return null;
			}

			for (int i = 0; i < children.length; i++) {
				IModelElementDelta d = findElement(unit, children[i]);
				if (d != null) {
					return d;
				}
			}

			return null;
		}

		private boolean isPossibleStructuralChange(IModelElementDelta cuDelta) {
			if (cuDelta.getKind() != IModelElementDelta.CHANGED) {
				return true; // add or remove
			}
			int flags = cuDelta.getFlags();
			if ((flags & IModelElementDelta.F_CHILDREN) != 0) {
				return true;
			}
			return (flags & (IModelElementDelta.F_CONTENT | IModelElementDelta.F_FINE_GRAINED)) == IModelElementDelta.F_CONTENT;
		}
	}

	/**
	 * Empty selection provider.
	 * 
	 * @since 3.2
	 */
	private static final class EmptySelectionProvider implements ISelectionProvider {
		
		@Override
		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
		}

		@Override
		public ISelection getSelection() {
			return StructuredSelection.EMPTY;
		}

		@Override
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
		}

		@Override
		public void setSelection(ISelection selection) {
		}
		
	}

	/**
	 * The tree viewer used for displaying the outline.
	 * 
	 * @see TreeViewer
	 */
	protected class ScriptOutlineViewer extends TreeViewer {

		public ScriptOutlineViewer(Tree tree) {
			super(tree);
			setAutoExpandLevel(ALL_LEVELS);
			setUseHashlookup(true);
		}

		private IResource getUnderlyingResource() {
			Object input = getInput();
			if (input instanceof ISourceModule) {
				ISourceModule cu = (ISourceModule) input;
				cu = cu.getPrimary();
				return cu.getResource();
			} /*
			 * else if (input instanceof IClassFile) { return ((IClassFile)
			 * input).getResource(); }
			 */
			return null;
		}

		@Override
		protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
			Object input = getInput();
			if (event instanceof ProblemsLabelChangedEvent) {
				ProblemsLabelChangedEvent e = (ProblemsLabelChangedEvent) event;
				if (e.isMarkerChange() && input instanceof ISourceModule) {
					return; // marker changes can be ignored
				}
			}
			// look if the underlying resource changed
			Object[] changed = event.getElements();
			if (changed != null) {
				IResource resource = getUnderlyingResource();
				if (resource != null) {
					for (int i = 0; i < changed.length; i++) {
						if (changed[i] != null && changed[i].equals(resource)) {
							// change event to a full refresh
							event = new LabelProviderChangedEvent((IBaseLabelProvider) event.getSource());
							break;
						}
					}
				}
			}
			super.handleLabelProviderChanged(event);
		}

		@Override
		protected void internalExpandToLevel(Widget node, int level) {
			if (node instanceof Item) {
				Item i = (Item) node;
				if (i.getData() instanceof IModelElement) {
					IModelElement je = (IModelElement) i.getData();
					if (collapseInitially(je)) {
						setExpanded(i, false);
						return;
					}
				}
			}
			super.internalExpandToLevel(node, level);
		}

		@Override
		public boolean isExpandable(Object element) {
			if (hasFilters()) {
				return getFilteredChildren(element).length > 0;
			}
			return super.isExpandable(element);
		}

		/**
		 * Investigates the given element change event and if affected
		 * incrementally updates the Java outline.
		 * 
		 * @param delta
		 *            the Java element delta used to reconcile the Java outline
		 */
		public void reconcile(IModelElementDelta delta) {
			refresh(true);
		}

	}

	class LexicalSortingAction extends Action {

		private static final String LEXICAL_SORTING_ACTION_IS_CHECKED = "LexicalSortingAction.isChecked"; //$NON-NLS-1$

		private ModelElementSorter fComparator = new ModelElementSorter();
		private SourcePositionSorter fSourcePositonComparator = new SourcePositionSorter();

		public LexicalSortingAction() {
			super();
			// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
			// IJavaHelpContextIds.LEXICAL_SORTING_OUTLINE_ACTION);
			setText(DLTKEditorMessages.ScriptOutlinePage_Sort_label);
			DLTKPluginImages.setLocalImageDescriptors(this,
					"alphab_sort_co.gif"); //$NON-NLS-1$
			setToolTipText(DLTKEditorMessages.ScriptOutlinePage_Sort_tooltip);
			setDescription(DLTKEditorMessages.ScriptOutlinePage_Sort_description);

			boolean checked = fStore
					.getBoolean(LEXICAL_SORTING_ACTION_IS_CHECKED);
			valueChanged(checked, false);
		}

		@Override
		public void run() {
			valueChanged(isChecked(), true);
		}

		private void valueChanged(final boolean on, boolean store) {
			setChecked(on);
			BusyIndicator.showWhile(fOutlineViewer.getControl().getDisplay(),
					new Runnable() {
						@Override
						public void run() {
							if (on) {
								fOutlineViewer.setComparator(fComparator);
							} else {
								fOutlineViewer.setComparator(fSourcePositonComparator);
							}
						}
					});

			if (store) {
				fStore.setValue(LEXICAL_SORTING_ACTION_IS_CHECKED, on);
			}
		}
	}

	/**
	 * This action toggles whether this Java Outline page links its selection to
	 * the active editor.
	 * 
	 * @since 3.0
	 */
	public class ToggleLinkingAction extends AbstractToggleLinkingAction {

		ScriptOutlinePage fJavaOutlinePage;

		/**
		 * Constructs a new action.
		 * 
		 * @param outlinePage
		 *            the Java outline page
		 */
		public ToggleLinkingAction(ScriptOutlinePage outlinePage) {
			boolean isLinkingEnabled = fStore
					.getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE);
			setChecked(isLinkingEnabled);
			fJavaOutlinePage = outlinePage;
		}

		/**
		 * Runs the action.
		 */
		@Override
		public void run() {
			fStore.setValue(
					PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE,
					isChecked());
			if (isChecked() && fEditor != null) {
				fEditor.synchronizeOutlinePage(fEditor
						.computeHighlightRangeSourceReference(), false);
			}
		}

	}

	static final Object[] NO_CHILDREN = new Object[0];

	/** A flag to show contents of top level type only */
	// private boolean fTopLevelTypeOnly;
	private IModelElement fInput;
	// private String fContextMenuID;
	private Menu fMenu;
	protected ScriptOutlineViewer fOutlineViewer;
	private IScriptEditor fEditor;
	protected IPreferenceStore fStore;
	private MemberFilterActionGroup fMemberFilterActionGroup;

	private ListenerList fSelectionChangedListeners = new ListenerList(
			ListenerList.IDENTITY);
	private ListenerList fPostSelectionChangedListeners = new ListenerList(
			ListenerList.IDENTITY);
	private Hashtable<String, IAction> fActions = new Hashtable<String, IAction>();

	private ToggleLinkingAction fToggleLinkingAction;

	private CompositeActionGroup fActionGroups;

	private IPropertyChangeListener fPropertyChangeListener;
	/**
	 * Custom filter action group.
	 * 
	 * @since 3.0
	 */
	private CustomFiltersActionGroup fCustomFiltersActionGroup;


	/**
	 * @since 2.0
	 */
	public ScriptOutlinePage(IScriptEditor editor, IPreferenceStore store) {
		super();

		Assert.isNotNull(editor);

		// fContextMenuID = "#CompilationUnitOutlinerContext";// contextMenuID;
		fEditor = editor;
		fStore = store;

		fPropertyChangeListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				doPropertyChange(event);
			}
		};
		fStore.addPropertyChangeListener(fPropertyChangeListener);
	}

	/**
	 * Convenience method to add the action installed under the given actionID
	 * to the specified group of the menu.
	 * 
	 * @param menu
	 *            the menu manager
	 * @param group
	 *            the group to which to add the action
	 * @param actionID
	 *            the ID of the new action
	 */
	protected void addAction(IMenuManager menu, String group, String actionID) {
		IAction action = getAction(actionID);
		if (action != null) {
			if (action instanceof IUpdate) {
				((IUpdate) action).update();
			}

			if (action.isEnabled()) {
				IMenuManager subMenu = menu.findMenuUsingPath(group);
				if (subMenu != null) {
					subMenu.add(action);
				} else {
					menu.appendToGroup(group, action);
				}
			}
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.IPostSelectionProvider#addPostSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addPostSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (fOutlineViewer != null) {
			fOutlineViewer.addPostSelectionChangedListener(listener);
		} else {
			fPostSelectionChangedListeners.add(listener);
		}
	}

	/*
	 * @see
	 * ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (fOutlineViewer != null) {
			fOutlineViewer.addSelectionChangedListener(listener);
		} else {
			fSelectionChangedListeners.add(listener);
		}
	}

	protected void contextMenuAboutToShow(IMenuManager menu) {

		// DLTKUIPlugin.createStandardGroups(menu);
		if (menu.isEmpty()) {
			// menu.add(new Separator(IContextMenuConstants.GROUP_NEW));
			menu.add(new GroupMarker(IContextMenuConstants.GROUP_GOTO));
			menu.add(new Separator(IContextMenuConstants.GROUP_OPEN));
			menu.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
			menu.add(new Separator(ICommonMenuConstants.GROUP_EDIT));
			menu.add(new Separator(IContextMenuConstants.GROUP_REORGANIZE));
			// menu.add(new Separator(IContextMenuConstants.GROUP_GENERATE));
			menu.add(new Separator(IContextMenuConstants.GROUP_SEARCH));
			// menu.add(new Separator(IContextMenuConstants.GROUP_BUILD));
			menu.add(new Separator(IContextMenuConstants.GROUP_ADDITIONS));
			// menu.add(new
			// Separator(IContextMenuConstants.GROUP_VIEWER_SETUP));
			menu.add(new Separator(IContextMenuConstants.GROUP_PROPERTIES));
		}

		IStructuredSelection selection = (IStructuredSelection) getSelection();
		fActionGroups.setContext(new ActionContext(selection));
		fActionGroups.fillContextMenu(menu);
	}

	protected ILabelDecorator getLabelDecorator() {
		return null;
	}

	@Override
	public void createControl(Composite parent) {

		Tree tree = new Tree(parent, SWT.MULTI);

		AppearanceAwareLabelProvider lprovider = new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
						| ScriptElementLabels.F_APP_TYPE_SIGNATURE
						| ScriptElementLabels.ALL_CATEGORY
						| ScriptElementLabels.M_APP_RETURNTYPE,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS, fStore);

		ILabelDecorator ldecorator = getLabelDecorator();
		if (ldecorator != null) {
			lprovider.addLabelDecorator(ldecorator);
		}

		fOutlineViewer = new ScriptOutlineViewer(tree);
		
		fOutlineViewer.setContentProvider(new ChildrenProvider());
		fOutlineViewer.setLabelProvider(new DecoratingModelLabelProvider(lprovider));

		Object[] listeners = fSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			fSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer.addSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		listeners = fPostSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			fPostSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer
					.addPostSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		MenuManager manager = new MenuManager(DLTKUIPlugin.getPluginId()
				+ ".outline", DLTKUIPlugin.getPluginId() + ".outline"); //$NON-NLS-1$ //$NON-NLS-2$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager m) {
				contextMenuAboutToShow(m);
			}
		});
		fMenu = manager.createContextMenu(tree);
		tree.setMenu(fMenu);

		IPageSite site = getSite();
		site.registerContextMenu(
						DLTKUIPlugin.getPluginId() + ".outline", manager, fOutlineViewer); //$NON-NLS-1$

		updateSelectionProvider(site);

		IDLTKLanguageToolkit toolkit = fEditor.getLanguageToolkit();
		// we must create the groups after we have set the selection provider to
		// the site
		fActionGroups = new CompositeActionGroup(new ActionGroup[] {
				new OpenViewActionGroup(this),
				new SearchActionGroup(this, toolkit) });

		// register global actions
		IActionBars actionBars = site.getActionBars();
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.UNDO,
				fEditor.getAction(ITextEditorActionConstants.UNDO));
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.REDO,
				fEditor.getAction(ITextEditorActionConstants.REDO));

		IAction action;
		action = fEditor.getAction(ITextEditorActionConstants.NEXT);
		actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_NEXT_ANNOTATION, action);
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.NEXT, action);
		
		action = fEditor.getAction(ITextEditorActionConstants.PREVIOUS);
		actionBars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_PREVIOUS_ANNOTATION, action);
		actionBars.setGlobalActionHandler(ITextEditorActionConstants.PREVIOUS, action);
		
		fActionGroups.fillActionBars(actionBars);

		IStatusLineManager statusLineManager = actionBars
				.getStatusLineManager();
		if (statusLineManager != null) {
			StatusBarUpdater updater = new StatusBarUpdater(statusLineManager);
			fOutlineViewer.addPostSelectionChangedListener(updater);
		}
		// Custom filter group
		fCustomFiltersActionGroup = new CustomFiltersActionGroup(
				"org.eclipse.dltk.ui.ScriptOutlinePage", fOutlineViewer); //$NON-NLS-1$

		registerToolbarActions(actionBars);

		fOutlineViewer.setInput(fInput);
	}

	@Override
	public void dispose() {

		if (fEditor == null) {
			return;
		}

		if (fMemberFilterActionGroup != null) {
			fMemberFilterActionGroup.dispose();
			fMemberFilterActionGroup = null;
		}

		// if (fCategoryFilterActionGroup != null) {
		// fCategoryFilterActionGroup.dispose();
		// fCategoryFilterActionGroup = null;
		// }

		if (fCustomFiltersActionGroup != null) {
			fCustomFiltersActionGroup.dispose();
			fCustomFiltersActionGroup = null;
		}

		fEditor.outlinePageClosed();
		fEditor = null;

		fSelectionChangedListeners.clear();
		fSelectionChangedListeners = null;

		fPostSelectionChangedListeners.clear();
		fPostSelectionChangedListeners = null;

		if (fPropertyChangeListener != null) {
			fStore.removePropertyChangeListener(fPropertyChangeListener);
			fPropertyChangeListener = null;
		}

		if (fMenu != null && !fMenu.isDisposed()) {
			fMenu.dispose();
			fMenu = null;
		}

		if (fActionGroups != null) {
			fActionGroups.dispose();
		}

		fOutlineViewer = null;

		super.dispose();
	}

	private void doPropertyChange(PropertyChangeEvent event) {
		if (fOutlineViewer != null) {
			if (MembersOrderPreferenceCache.isMemberOrderProperty(event
					.getProperty())) {
				fOutlineViewer.refresh(false);
			}
		}
	}

	public IAction getAction(String actionID) {
		Assert.isNotNull(actionID);
		return fActions.get(actionID);
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class key) {
		if (key == IShowInSource.class) {
			return getShowInSource();
		}
		if (key == IShowInTargetList.class) {
			return new IShowInTargetList() {
				@Override
				public String[] getShowInTargetIds() {
					return new String[] { DLTKUIPlugin.ID_SCRIPT_EXPLORER };
				}

			};
		}
		if (key == IShowInTarget.class) {
			return getShowInTarget();
		}

		return null;
	}

	@Override
	public Control getControl() {
		if (fOutlineViewer != null) {
			return fOutlineViewer.getControl();
		}
		return null;
	}

	/**
	 * Returns the <code>JavaOutlineViewer</code> of this view.
	 * 
	 * @return the {@link ScriptOutlineViewer}
	 * @since 3.3
	 */
	protected final ScriptOutlineViewer getOutlineViewer() {
		return fOutlineViewer;
	}

	/*
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		if (fOutlineViewer == null) {
			return StructuredSelection.EMPTY;
		}
		return fOutlineViewer.getSelection();
	}

	/**
	 * Returns the <code>IShowInSource</code> for this view.
	 * 
	 * @return the {@link IShowInSource}
	 */
	protected IShowInSource getShowInSource() {
		return new IShowInSource() {
			@Override
			public ShowInContext getShowInContext() {
				return new ShowInContext(null, getSite().getSelectionProvider()
						.getSelection());
			}
		};
	}

	/**
	 * Returns the <code>IShowInTarget</code> for this view.
	 * 
	 * @return the {@link IShowInTarget}
	 */
	protected IShowInTarget getShowInTarget() {
		return new IShowInTarget() {
			@Override
			public boolean show(ShowInContext context) {
				ISelection sel = context.getSelection();
				if (sel instanceof ITextSelection) {
					ITextSelection tsel = (ITextSelection) sel;
					int offset = tsel.getOffset();
					IModelElement element = fEditor.getElementAt(offset);
					if (element != null) {
						setSelection(new StructuredSelection(element));
						return true;
					}
				} else if (sel instanceof IStructuredSelection) {
					setSelection(sel);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
	}

	/**
	 * Answers if a given model element should be collapsed initially in the
	 * Outline view.
	 * 
	 * @param element
	 *            the model element
	 * @return <code>true</code> iff the given element should be initially
	 *         collapsed
	 */
	protected boolean collapseInitially(@NonNull IModelElement element) {
		final int elementType = element.getElementType();
		if (elementType == IModelElement.IMPORT_CONTAINER) {
			return true;
		} else if (elementType == IModelElement.TYPE) {
			// collapse if inner type
			final IModelElement parent = element.getParent();
			return parent != null
					&& parent.getElementType() != IModelElement.SOURCE_MODULE;
		} else if (elementType == IModelElement.METHOD
				|| elementType == IModelElement.FIELD) {
			final IModelElement parent = element.getParent();
			if (parent != null && parent.getElementType() == IModelElement.TYPE) {
				// collapse methods/fields of a type if no nested types
				try {
					if (!containsTypes((IMember) element)) {
						return true;
					}
				} catch (ModelException e) {
					DLTKUIPlugin.log(e);
				}
			}
		}
		return false;
	}

	private boolean containsTypes(IMember element) throws ModelException {
		for (IModelElement child : ((IParent) element).getChildren()) {
			if (child.getElementType() == IModelElement.TYPE
					|| child instanceof IMember
					&& containsTypes((IMember) child)) {
				return true;
			}
		}
		return false;
	}

	protected void registerSpecialToolbarActions(IActionBars actionBars) {
		// derived classes could implement it
	}

	private void registerToolbarActions(IActionBars actionBars) {
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(new LexicalSortingAction());

		fMemberFilterActionGroup = new MemberFilterActionGroup(fOutlineViewer, fStore);
		fMemberFilterActionGroup.contributeToToolBar(toolBarManager);

		fCustomFiltersActionGroup.fillActionBars(actionBars);

		registerSpecialToolbarActions(actionBars);

		IMenuManager viewMenuManager = actionBars.getMenuManager();
		viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$

		fToggleLinkingAction = new ToggleLinkingAction(this);
		// viewMenuManager.add(new ClassOnlyAction());
		viewMenuManager.add(fToggleLinkingAction);

		// fCategoryFilterActionGroup = new CategoryFilterActionGroup(
		// fOutlineViewer,
		// "org.eclipse.jdt.ui.JavaOutlinePage", new IModelElement[] { fInput
		// }); //$NON-NLS-1$
		// fCategoryFilterActionGroup.contributeToViewMenu(viewMenuManager);
	}

	/*
	 * @seeorg.eclipse.jface.text.IPostSelectionProvider#
	 * removePostSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removePostSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (fOutlineViewer != null) {
			fOutlineViewer.removePostSelectionChangedListener(listener);
		} else {
			fPostSelectionChangedListeners.remove(listener);
		}
	}

	/*
	 * @see
	 * ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener
	 * )
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (fOutlineViewer != null) {
			fOutlineViewer.removeSelectionChangedListener(listener);
		} else {
			fSelectionChangedListeners.remove(listener);
		}
	}

	public void select(ISourceReference reference) {
		if (fOutlineViewer != null) {

			ISelection s = fOutlineViewer.getSelection();
			if (s instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) s;
				List<?> elements = ss.toList();
				if (!elements.contains(reference)) {
					s = (reference == null ? StructuredSelection.EMPTY
							: new StructuredSelection(reference));
					fOutlineViewer.setSelection(s, true);
				}
			}
		}
	}

	public void setAction(String actionID, IAction action) {
		Assert.isNotNull(actionID);
		if (action == null) {
			fActions.remove(actionID);
		} else {
			fActions.put(actionID, action);
		}
	}

	/*
	 * @see Page#setFocus()
	 */
	@Override
	public void setFocus() {
		if (fOutlineViewer != null) {
			fOutlineViewer.getControl().setFocus();
		}
	}

	public void setInput(IModelElement inputElement) {
		fInput = inputElement;
		if (fOutlineViewer != null) {
			fOutlineViewer.setInput(fInput);
			updateSelectionProvider(getSite());
		}
		// if (fCategoryFilterActionGroup != null)
		// fCategoryFilterActionGroup.setInput(new IModelElement[] { fInput });
	}

	/*
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (fOutlineViewer != null) {
			fOutlineViewer.setSelection(selection);
		}
	}

	/*
	 * @since 3.2
	 */
	private void updateSelectionProvider(IPageSite site) {
		ISelectionProvider provider = fOutlineViewer;
		if (fInput != null) {
			ISourceModule cu = (ISourceModule) fInput.getAncestor(IModelElement.SOURCE_MODULE);
			if (cu != null && !ScriptModelUtil.isPrimary(cu)) {
				provider = new EmptySelectionProvider();
			}
		}
		site.setSelectionProvider(provider);
	}
}
