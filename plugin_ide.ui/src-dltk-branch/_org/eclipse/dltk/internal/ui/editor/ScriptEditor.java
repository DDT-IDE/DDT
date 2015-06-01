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

import melnorme.lang.ide.ui.EditorSettings_Actual.EditorPrefConstants;
import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;
import melnorme.lang.ide.ui.templates.TemplateRegistry;
import mmrnmhrm.ui.DeeUILanguageToolkit;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptLanguageProvider;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IWorkingCopyManager;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProviderExtension;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.templates.ITemplatesPage;

import _org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import _org.eclipse.dltk.ui.text.folding.DelegatingFoldingStructureProvider;
import _org.eclipse.jdt.internal.ui.text.java.hover.SourceViewerInformationControl;

/* FIXME: need to review this class */
public abstract class ScriptEditor extends AbstractLangStructureEditor implements IScriptLanguageProvider {
	
	/** Preference key for matching brackets */
	protected final static String MATCHING_BRACKETS = PreferenceConstants.EDITOR_MATCHING_BRACKETS;
	/** Preference key for matching brackets color */
	protected final static String MATCHING_BRACKETS_COLOR = PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;

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
		setDocumentProvider(DLTKUIPlugin.getDefault().getSourceModuleDocumentProvider());
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
	protected void alterCombinedPreferenceStores_beforeEditorsUI(List<IPreferenceStore> stores) {
		stores.add(new PreferencesAdapter(DLTKCore.getDefault().getPluginPreferences()));
	}
	
	@Override
	protected AdaptedSourceViewer doCreateSourceViewer(Composite parent, IVerticalRuler verticalRuler, int styles) {

		AdaptedSourceViewer viewer = new AdaptedSourceViewer(parent, verticalRuler, getOverviewRuler(), 
			isOverviewRulerVisible(), styles, this);
		
		IPreferenceStore store = getPreferenceStore();
		installProjectionSupport(store, viewer);
		
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
	
	/**
	 * The templates page.
	 * 
	 * @since 3.0
	 */
	private ScriptTemplatesPage fTemplatesPage;

	/**
	 * Creates the templates page used with this editor.
	 * 
	 * @return the created script templates page
	 * @since 3.0
	 */
	protected ScriptTemplatesPage createTemplatesPage() {
		final TemplateRegistry templateAccess = LangUIPlugin.getTemplateRegistry();
		try {
			return new ScriptTemplatesPage(this, templateAccess);
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		if (ITemplatesPage.class.equals(required)) {
			if (fTemplatesPage == null)
				fTemplatesPage = createTemplatesPage();
			return fTemplatesPage;
		}
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


	/**
	 * The folding runner.
	 * 
	 * 
	 */
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

		/**
		 * The workbench page we registered the part listener with, or
		 * <code>null</code>.
		 */
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

		/*
		 * @seeorg.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
		 * IWorkbenchPartReference)
		 */
		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			if (ScriptEditor.this.equals(partRef.getPart(false))) {
				cancel();
				toggleFolding();
			}
		}

		/*
		 * @seeorg.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
		 * IWorkbenchPartReference)
		 */
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
		if (isHandledPropertyEvent(property, GLOBAL_FOLDING_PROPERTIES)) {
			return true;
		}

		if (isHandledPropertyEvent(property, getFoldingEventPreferenceKeys())) {
			return true;
		}

		return false;
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
		return CharOperation.NO_STRINGS;
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
	 * Collapses all foldable members if supported by the folding structure
	 * provider.
	 * 
	 * 
	 */
	public void collapseMembers() {
		if (fProjectionModelUpdater instanceof IFoldingStructureProviderExtension) {
			IFoldingStructureProviderExtension extension = (IFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseMembers();
		}
	}

	/**
	 * Collapses all foldable comments if supported by the folding structure
	 * provider.
	 * 
	 * 
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
	
	@Deprecated
	@Override
	public DeeLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	protected DeeUILanguageToolkit getUILanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}


	/*
	 * @see AbstractTextEditor#doSave(IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {

		IDocumentProvider p = getDocumentProvider();
		if (p == null) {
			// editor has been closed
			return;
		}

		if (p.isDeleted(getEditorInput())) {

			if (isSaveAsAllowed()) {

				/*
				 * 1GEUSSR: ITPUI:ALL - User should never loose changes made in
				 * the editors. Changed Behavior to make sure that if called
				 * inside a regular save (because of deletion of input element)
				 * there is a way to report back to the caller.
				 */
				performSaveAs(progressMonitor);

			} else {

				/*
				 * 1GF5YOX: ITPJUI:ALL - Save of delete file claims it's still
				 * there Missing resources.
				 */
				Shell shell = getSite().getShell();
				MessageDialog
						.openError(
								shell,
								DLTKEditorMessages.SourceModuleEditor_error_saving_title1,
								DLTKEditorMessages.SourceModuleEditor_error_saving_message1);
			}

		} else {

			setStatusLineErrorMessage(null);

			updateState(getEditorInput());
			validateState(getEditorInput());

			IWorkingCopyManager manager = DLTKUIPlugin.getDefault()
					.getWorkingCopyManager();
			ISourceModule unit = manager.getWorkingCopy(getEditorInput());

			if (unit != null) {
				// synchronized (unit) {
				performSave(false, progressMonitor);
				// }
			} else
				performSave(false, progressMonitor);
		}
	}
	
	@Override
	protected void configureBracketMatcher(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(fBracketMatcher);
		
		// TODO: use our own preferences
		support.setMatchingCharacterPainterPreferenceKeys(
			MATCHING_BRACKETS, 
			MATCHING_BRACKETS_COLOR, 
			EditorPrefConstants.HIGHLIGHT_BRACKET_AT_CARET_LOCATION, 
			EditorPrefConstants.ENCLOSING_BRACKETS);
	}
	
	@Override
	protected String[] collectContextMenuPreferencePages() {
		final List<String> result = new ArrayList<String>();
		final DeeUILanguageToolkit uiToolkit = getUILanguageToolkit();
		addPages(result, uiToolkit.getEditorPreferencePages());
		addPages(result, super.collectContextMenuPreferencePages());
		return result.toArray(new String[result.size()]);
	}

	private void addPages(final List<String> result, final String[] pages) {
		if (pages != null) {
			for (int i = 0; i < pages.length; ++i) {
				if (!result.contains(pages[i])) {
					result.add(pages[i]);
				}
			}
		}
	}

	private boolean isHandledPropertyEvent(String property, String[] handled) {
		for (int i = 0; i < handled.length; i++) {
			if (handled[i].equals(property)) {
				return true;
			}
		}

		return false;
	}

	/* public access */
	public final String getFontPropertyPreferenceKey_() {
		return getFontPropertyPreferenceKey();
	}
	
	
	/* ----------------- Folding actions ----------------- */
	
	private FoldingActionGroup fFoldingGroup;
	
	FoldingActionGroup getFoldingActionGroup() {
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
		IMenuManager foldingMenu = new MenuManager(
				DLTKEditorMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
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