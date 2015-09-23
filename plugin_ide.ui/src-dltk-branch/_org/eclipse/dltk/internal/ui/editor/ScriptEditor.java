/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *     xored software, Inc. - fix tab handling (Bug# 200024) (Alex Panchenko) 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import _org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import _org.eclipse.dltk.ui.PreferenceConstants;
import _org.eclipse.dltk.ui.text.folding.DelegatingFoldingStructureProvider;
import _org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import _org.eclipse.dltk.ui.text.folding.IFoldingStructureProviderExtension;
import _org.eclipse.jdt.internal.ui.text.java.hover.SourceViewerInformationControl;
import dtool.util.NewUtils;
import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.preferences.pages.DeeContentAssistPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeEditorPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeEditorTypingPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeFoldingPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeSourceColoringPreferencePage;
import mmrnmhrm.ui.preferences.pages.DeeTemplatePreferencePage;

public abstract class ScriptEditor extends AbstractLangStructureEditor {
	
	private static String[] GLOBAL_FOLDING_PROPERTIES = {
			PreferenceConstants.EDITOR_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT,
			PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES };


	private ProjectionSupport fProjectionSupport;
	private DelegatingFoldingStructureProvider fProjectionModelUpdater;
	
	/* -----------------  ----------------- */
	
	public ScriptEditor() {
		super();
	}
	
	@Override
	public void dispose() {

		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.uninstall();
			fProjectionModelUpdater = null;
		}
		
		super.dispose();
	}

	@Override
	protected AdaptedSourceViewer doCreateSourceViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {

		AdaptedSourceViewer viewer = new AdaptedSourceViewer(parent, verticalRuler, getOverviewRuler(), 
			isOverviewRulerVisible(), styles, this);
		
		installProjectionSupport(getPreferenceStore(), viewer);
		
		// ensure source viewer decoration support has been created and configured
		getSourceViewerDecorationSupport(viewer);
		
		return viewer;
	}
	
	@Override
	public AdaptedSourceViewer getSourceViewer_() {
		return (AdaptedSourceViewer) getSourceViewer();
	}
	
	@Override
	protected void handleElementContentReplaced() {
		super.handleElementContentReplaced();
		getSourceViewer_().hadnleElementContentReplaced();
	}
	
	/* ----------------- set input ----------------- */
	
	@Override
	protected void internalDoSetInput(IEditorInput input) {
		AdaptedSourceViewer sourceViewer = getSourceViewer_(); // Can be null
		
		if (sourceViewer != null && isFoldingEnabled()) {
			sourceViewer.prepareDelayedProjection();
		}

		super.internalDoSetInput(input);
	}
	
	/* -----------------  ----------------- */
	
	private boolean isFoldingEnabled() {
		return getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_FOLDING_ENABLED);
	}
	

	@Override
	public Object getAdapter(Class required) {
		if (required == IFoldingStructureProvider.class)
			return fProjectionModelUpdater;
		if (required == IFoldingStructureProviderExtension.class)
			return fProjectionModelUpdater;

		if (fProjectionSupport != null) {
			Object adapter = fProjectionSupport.getAdapter(getSourceViewer(), required);
			if (adapter != null)
				return adapter;
		}

		return super.getAdapter(required);
	}


	private ToggleFoldingRunner fFoldingRunner;

	/**
	 * Runner that will toggle folding either instantly (if the editor is
	 * visible) or the next time it becomes visible. If a runner is started when
	 * there is already one registered, the registered one is canceled as
	 * toggling folding twice is a no-op.
	 * <p>
	 * The access to the fFoldingRunner field is not thread-safe, it is assumed
	 * that <code>runWhenNextVisible</code> is only called from the UI thread.
	 * </p>
	 * 
	 * 
	 */
	protected final class ToggleFoldingRunner implements IPartListener2 {
		public ToggleFoldingRunner() {
		}

		private IWorkbenchPage fPage;

		/**
		 * Does the actual toggling of projection.
		 */
		private void toggleFolding() {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer instanceof ProjectionViewer) {
				ProjectionViewer pv = (ProjectionViewer) sourceViewer;
				if (pv.isProjectionMode() != isFoldingEnabled()) {
					if (pv.canDoOperation(ProjectionViewer.TOGGLE))
						pv.doOperation(ProjectionViewer.TOGGLE);
				}
			}
		}

		/**
		 * Makes sure that the editor's folding state is correct the next time
		 * it becomes visible. If it already is visible, it toggles the folding
		 * state. If not, it either registers a part listener to toggle folding
		 * when the editor becomes visible, or cancels an already registered
		 * runner.
		 */
		public void runWhenNextVisible() {
			// if there is one already: toggling twice is the identity
			if (fFoldingRunner != null) {
				fFoldingRunner.cancel();
				return;
			}
			IWorkbenchPartSite site = getSite();
			if (site != null) {
				IWorkbenchPage page = site.getPage();
				if (!page.isPartVisible(ScriptEditor.this)) {
					// if we're not visible - defer until visible
					fPage = page;
					fFoldingRunner = this;
					page.addPartListener(this);
					return;
				}
			}
			// we're visible - run now
			toggleFolding();
		}

		/**
		 * Remove the listener and clear the field.
		 */
		private void cancel() {
			if (fPage != null) {
				fPage.removePartListener(this);
				fPage = null;
			}
			if (fFoldingRunner == this)
				fFoldingRunner = null;
		}

		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			if (ScriptEditor.this.equals(partRef.getPart(false))) {
				cancel();
				toggleFolding();
			}
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			if (ScriptEditor.this.equals(partRef.getPart(false))) {
				cancel();
			}
		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		String property = event.getProperty();
		try {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			
			if (isFoldingPropertyEvent(property) && sourceViewer instanceof ProjectionViewer) {
				handleFoldingPropertyEvent(property);
			}
		} finally {
			super.handlePreferenceStoreChanged(event);
		}
	}

	protected void handleFoldingPropertyEvent(String property) {
		// NOTE: 'initially fold' preferences do not require handling
		if (PreferenceConstants.EDITOR_FOLDING_ENABLED.equals(property)) {
			ToggleFoldingRunner runner = new ToggleFoldingRunner();
			runner.runWhenNextVisible();
		} else {
			fProjectionModelUpdater.initialize(false);
		}
	}

	protected final boolean isFoldingPropertyEvent(String property) {
		return 
				isHandledPropertyEvent(property, GLOBAL_FOLDING_PROPERTIES) || 
				isHandledPropertyEvent(property, getFoldingEventPreferenceKeys());				
	}
	
	public static boolean isHandledPropertyEvent(String property, String[] handled) {
		return ArrayUtil.contains(handled, property);
	}

	/**
	 * Returns a string array containing the language specific folding
	 * preference keys that should be handed when a property change event is
	 * fired.
	 * 
	 * <p>
	 * Default implementation returns an empty array. Subclasses should override
	 * this method to return folding keys that are language specific.
	 * </p>
	 */
	protected String[] getFoldingEventPreferenceKeys() {
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	protected void installProjectionSupport(IPreferenceStore store, AdaptedSourceViewer viewer) {
		/*
		 * This is a performance optimization to reduce the computation of the
		 * text presentation triggered by {@link #setVisibleDocument(IDocument)}
		 */
		if (isFoldingEnabled() && (store == null || !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS))) {
			viewer.prepareDelayedProjection();
		}
		
		ProjectionViewer projectionViewer = (ProjectionViewer) viewer;
		fProjectionSupport = new ProjectionSupport(projectionViewer, getAnnotationAccess(), getSharedColors());
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		
		fProjectionSupport.setHoverControlCreator(new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell shell) {
				String statusFieldText = EditorsUI.getTooltipAffordanceString();
				return new SourceViewerInformationControl(shell, false, getOrientation(), statusFieldText);
			}
		});
		fProjectionSupport.setInformationPresenterControlCreator(new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell shell) {
				return new SourceViewerInformationControl(shell, true, getOrientation(), null);
			}
		});
		
		fProjectionSupport.install();
		
		fProjectionModelUpdater = new DelegatingFoldingStructureProvider(this);
		fProjectionModelUpdater.install(this, projectionViewer, getPreferenceStore());
	}
	
	/**
	 * Resets the foldings structure according to the folding preferences.
	 */
	public void resetProjection() {
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.initialize();
		}
	}

	/**
	 * Collapses all foldable members if supported by the folding structure provider.
	 */
	public void collapseMembers() {
		if (fProjectionModelUpdater instanceof IFoldingStructureProviderExtension) {
			IFoldingStructureProviderExtension extension = (IFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseMembers();
		}
	}

	/**
	 * Collapses all foldable comments if supported by the folding structure provider.
	 */
	public void collapseComments() {
		if (fProjectionModelUpdater instanceof IFoldingStructureProviderExtension) {
			IFoldingStructureProviderExtension extension = (IFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseComments();
		}
	}

	@Override
	protected void performRevert() {
		ProjectionViewer projectionViewer = getSourceViewer_();
		projectionViewer.setRedraw(false);
		try {

			boolean projectionMode = projectionViewer.isProjectionMode();
			if (projectionMode) {
				projectionViewer.disableProjection();
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.uninstall();
			}

			super.performRevert();

			if (projectionMode) {
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.install(this, projectionViewer, getPreferenceStore());
				projectionViewer.enableProjection();
			}

		} finally {
			projectionViewer.setRedraw(true);
		}
	}
	
	/* ----------------- bracket matcher ----------------- */
	
	@Override
	protected String[] collectContextMenuPreferencePages() {
		final List<String> result = new ArrayList<String>();
		addPages(result, ScriptEditor.getEditorPreferencePages());
		addPages(result, super.collectContextMenuPreferencePages());
		return result.toArray(new String[result.size()]);
	}
	
	public static String[] getEditorPreferencePages() {
		return new String[]{ 
				DeeEditorPreferencePage.PAGE_ID, 
				DeeContentAssistPreferencePage.PAGE_ID,
				DeeEditorTypingPreferencePage.PAGE_ID,
				DeeFoldingPreferencePage.PAGE_ID,
				DeeTemplatePreferencePage.PAGE_ID,
				DeeSourceColoringPreferencePage.PAGE_ID};
	}
	
	protected void addPages(final List<String> result, final String[] pages) {
		if (pages != null) {
			for (int i = 0; i < pages.length; ++i) {
				if (!result.contains(pages[i])) {
					result.add(pages[i]);
				}
			}
		}
	}
	
	
	/* ----------------- Folding actions ----------------- */
	
	private FoldingActionGroup fFoldingGroup;
	
	protected FoldingActionGroup getFoldingActionGroup() {
		return fFoldingGroup;
	}
	
	@Override
	protected void createActions() {
		super.createActions();

		IPreferenceStore store = LangUIPlugin.getInstance().getPreferenceStore();
		fFoldingGroup = new FoldingActionGroup(this, getSourceViewer_(), store);
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new GroupMarker(ICommonMenuConstants.GROUP_SHOW));
	}
	
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager foldingMenu = new MenuManager("F&olding", "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseMembers"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseComments"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
	}
	
}