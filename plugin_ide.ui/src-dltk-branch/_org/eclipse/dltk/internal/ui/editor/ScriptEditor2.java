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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import melnorme.lang.ide.core.LangNature;
import mmrnmhrm.ui.DeeUIPlugin;

import org.dsource.ddt.ide.core.DeeLanguageToolkit;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IImportDeclaration;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IPackageDeclaration;
import org.eclipse.dltk.core.IScriptLanguageProvider;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.BrowserInformationControl;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ISavePolicy;
import org.eclipse.dltk.internal.ui.editor.IScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ISourceModuleDocumentProvider;
import org.eclipse.dltk.internal.ui.editor.ScriptAnnotationIterator;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.IScriptReconcilingListener;
import org.eclipse.dltk.internal.ui.text.hover.ScriptExpandHover;
import org.eclipse.dltk.internal.ui.text.hover.SourceViewerInformationControl;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.EclipsePreferencesAdapter;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.IWorkingCopyManager;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.FoldingProviderManager;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProviderExtension;
import org.eclipse.dltk.ui.text.templates.ITemplateAccess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.templates.ITemplatesPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import _org.eclipse.dltk.internal.ui.editor.SourceModuleDocumentProvider.SourceModuleAnnotationModel;
import _org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlightingManager;

public abstract class ScriptEditor2 extends AbstractDecoratedTextEditor
		implements IScriptReconcilingListener, IScriptLanguageProvider,
		IScriptEditor {
	
	/** The editor's save policy */
	protected ISavePolicy fSavePolicy = null;

	/** Preference key for matching brackets */
	protected final static String MATCHING_BRACKETS = PreferenceConstants.EDITOR_MATCHING_BRACKETS;
	/** Preference key for matching brackets color */
	protected final static String MATCHING_BRACKETS_COLOR = PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;

	private OccurrencesFinder2 occurrencesFinder;

	private static String[] GLOBAL_FOLDING_PROPERTIES = {
			PreferenceConstants.EDITOR_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT,
			PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES };

	/**
	 * Text operation code for requesting common prefix completion.
	 */
	public static final int CONTENTASSIST_COMPLETE_PREFIX = 60;
	
	/**
	 * Updates the selection in the editor's widget with the selection of the
	 * outline page.
	 */
	class OutlineSelectionChangedListener extends
			AbstractSelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			doSelectionChanged(event);
		}
	}

	private ScriptOutlinePage fOutlinePage;

	private ProjectionSupport fProjectionSupport;

	/**
	 * This editor's projection model updater
	 */
	private IFoldingStructureProvider fProjectionModelUpdater;


	/** The information presenter. */
	private InformationPresenter fInformationPresenter;

	private AbstractSelectionChangedListener fOutlineSelectionChangedListener = new OutlineSelectionChangedListener();

	/**
	 * Updates the script outline page selection and this editor's range indicator.
	 */
	private class EditorSelectionChangedListener extends AbstractSelectionChangedListener {
		/*
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
		 * (org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// XXX: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=56161
			ScriptEditor2.this.selectionChanged();
		}
	}

	/**
	 * The editor selection changed listener.
	 */
	private EditorSelectionChangedListener fEditorSelectionChangedListener;

	public ScriptEditor2() {
		super();
		setDocumentProvider(DLTKUIPlugin.getDefault().getSourceModuleDocumentProvider());
	}
	
	
	@Override
	public void dispose() {

		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.uninstall();
			fProjectionModelUpdater = null;
		}
		if (occurrencesFinder != null) {
			occurrencesFinder.dispose();
			occurrencesFinder = null;
		}

		// ISourceViewer sourceViewer= getSourceViewer();
		// if (sourceViewer instanceof ITextViewerExtension)
		// ((ITextViewerExtension)
		// sourceViewer).removeVerifyKeyListener(fBracketInserter);

		// if (fCorrectionCommands != null) {
		// fCorrectionCommands.deregisterCommands();
		// fCorrectionCommands= null;
		// }
		if (fSemanticManager != null) {
			fSemanticManager.uninstall();
			fSemanticManager = null;
		}
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		occurrencesFinder = new OccurrencesFinder2(this);
		if (!occurrencesFinder.isValid()) {
			occurrencesFinder = null;
		}
		IPreferenceStore store = createCombinedPreferenceStore(null);
		setPreferenceStore(store);
		ScriptTextTools textTools = getTextTools();
		if (textTools != null) {
			setSourceViewerConfiguration(textTools
					.createSourceViewerConfiguraton(store, this));
		}
	}

	/**
	 * Creates and returns the preference store for this editor with the given
	 * input.
	 * 
	 * @param input
	 *            The editor input for which to create the preference store
	 * @return the preference store for this editor
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		final List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(8);
		final IScriptProject project = EditorUtility.getScriptProject(input);
		final IDLTKLanguageToolkit toolkit = getLanguageToolkit();
		final String preferenceQualifier = toolkit.getPreferenceQualifier();
		if (project != null) {
			if (preferenceQualifier != null) {
				stores.add(new EclipsePreferencesAdapter(new ProjectScope(
						project.getProject()), preferenceQualifier));
			}
			stores.add(new EclipsePreferencesAdapter(new ProjectScope(project
					.getProject()), DLTKCore.PLUGIN_ID));
		}
		stores.add(getScriptPreferenceStore());
		if (preferenceQualifier != null) {
			stores.add(new EclipsePreferencesAdapter(InstanceScope.INSTANCE,
					preferenceQualifier));
			stores.add(new EclipsePreferencesAdapter(DefaultScope.INSTANCE,
					preferenceQualifier));
		}
		stores.add(new PreferencesAdapter(DLTKCore.getDefault().getPluginPreferences()));
		stores.add(EditorsUI.getPreferenceStore());
		stores.add(PlatformUI.getPreferenceStore());
		return new ChainedPreferenceStore(stores.toArray(new IPreferenceStore[stores.size()]));
	}
	
	public IPreferenceStore getScriptPreferenceStore() {
		return DeeUIPlugin.getInstance().getPreferenceStore();
	}
	
	@Deprecated
	public ScriptTextTools getTextTools() {
		return DeeUIPlugin.getDefault().getTextTools();
	}
	
	protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
	}
	
	@Override
	protected final AdaptedSourceViewer createSourceViewer(Composite parent, IVerticalRuler verticalRuler, 
			int styles) {

		IPreferenceStore store = getPreferenceStore();
		AdaptedSourceViewer viewer = new AdaptedSourceViewer(parent, verticalRuler, getOverviewRuler(), 
			isOverviewRulerVisible(), styles, store, this);
		assertNotNull(viewer);
		
		installProjectionSupport(store, viewer);

		// ensure source viewer decoration support has been created and configured
		getSourceViewerDecorationSupport(viewer);
		
		return viewer;
	}
	
	public AdaptedSourceViewer getSourceViewer_() {
		return (AdaptedSourceViewer) getSourceViewer();
	}
	
	@Override
	protected void handleElementContentReplaced() {
		super.handleElementContentReplaced();
		getSourceViewer_().hadnleElementContentReplaced();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IInformationControlCreator informationControlCreator = new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell shell) {
				boolean cutDown = false;
				//int style = cutDown ? SWT.NONE : (SWT.V_SCROLL | SWT.H_SCROLL);
				// return new DefaultInformationControl(shell, SWT.RESIZE
				// | SWT.TOOL, style, new HTMLTextPresenter(cutDown));
				if (BrowserInformationControl.isAvailable(shell))
					return new BrowserInformationControl(shell, JFaceResources.DIALOG_FONT, true);
				else
					return new DefaultInformationControl(shell, new HTMLTextPresenter(cutDown));
			}
		};

		fInformationPresenter = new InformationPresenter(informationControlCreator);
		fInformationPresenter.setSizeConstraints(60, 10, true, true);
		fInformationPresenter.install(getSourceViewer());
		fInformationPresenter.setDocumentPartitioning(IDocument.DEFAULT_CONTENT_TYPE);

		fEditorSelectionChangedListener = new EditorSelectionChangedListener();
		fEditorSelectionChangedListener.install(getSelectionProvider());
		
		installSemanticHighlighting();
		
		if (occurrencesFinder != null) {
			occurrencesFinder.install();
		}
	}
	
	/* ----------------- set input ----------------- */
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		AdaptedSourceViewer sourceViewer = getSourceViewer_(); // can be null
		
		if(sourceViewer != null) {
			// uninstall & unregister preference store listener
			getSourceViewerDecorationSupport(sourceViewer).uninstall();
			sourceViewer.unconfigure();
			setPreferenceStore(createCombinedPreferenceStore(input));
			sourceViewer.configure(getSourceViewerConfiguration());
			getSourceViewerDecorationSupport(sourceViewer).install(getPreferenceStore());
		} else {
			setPreferenceStore(createCombinedPreferenceStore(input));
		}
		
		try {
			internalDoSetInput(input);
		} catch (ModelException e) {
			DLTKUIPlugin.log(e);
			this.close(false);
		}
	}
	
	protected void internalDoSetInput(IEditorInput input) throws CoreException {
		ScriptSourceViewer sourceViewer = getSourceViewer_(); // Can be null
		IPreferenceStore store = getPreferenceStore();
		
		if (sourceViewer != null && isFoldingEnabled()
				&& (store == null || !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS))) {
			sourceViewer.prepareDelayedProjection();
		}

		// correct connection code here.

		super.doSetInput(input);

		final IDocumentProvider docProvider = getDocumentProvider();
		final IAnnotationModel model = docProvider.getAnnotationModel(input);
		if (model instanceof SourceModuleAnnotationModel) {
			((SourceModuleAnnotationModel) model).problemFactory = 
					DLTKLanguageManager.getProblemFactory(getNatureId());
		}
		final IDocument doc = docProvider.getDocument(input);
		connectPartitioningToElement(input, doc);

		if (sourceViewer != null && sourceViewer.getReconciler() == null) {
			IReconciler reconciler = getSourceViewerConfiguration().getReconciler(sourceViewer);
			if (reconciler != null) {
				reconciler.install(sourceViewer);
				sourceViewer.setReconciler(reconciler);
			}
		}
		if (DLTKCore.DEBUG) {
			System.err
					.println("TODO: Add encoding support and overriding indicator support"); //$NON-NLS-1$
		}
		// if (fEncodingSupport != null)
		// fEncodingSupport.reset();
		// if (isShowingOverrideIndicators())
		// installOverrideIndicator(false);
		setOutlinePageInput(fOutlinePage, input);
	}
	
	@Override
	public void setStatusLineErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	private boolean isFoldingEnabled() {
		return getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_FOLDING_ENABLED);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	protected IVerticalRulerColumn createAnnotationRulerColumn(
			CompositeRuler ruler) {
		if (!getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_ANNOTATION_ROLL_OVER))
			return super.createAnnotationRulerColumn(ruler);

		AnnotationRulerColumn column = new AnnotationRulerColumn(VERTICAL_RULER_WIDTH, getAnnotationAccess());
		column.setHover(new ScriptExpandHover(ruler, getAnnotationAccess(),
				new IDoubleClickListener() {

					@Override
					public void doubleClick(DoubleClickEvent event) {
						// for now: just invoke ruler double click action
						triggerAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK);
					}

					private void triggerAction(String actionID) {
						IAction action = getAction(actionID);
						if (action != null) {
							if (action instanceof IUpdate)
								((IUpdate) action).update();
							// hack to propagate line change
							if (action instanceof ISelectionListener) {
								((ISelectionListener) action).selectionChanged(
										null, null);
							}
							if (action.isEnabled())
								action.run();
						}
					}

				}));

		return column;
	}

	@Override
	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		final SourceViewerConfiguration svConfiguration = getSourceViewerConfiguration();
		if (svConfiguration == null
				|| svConfiguration instanceof ScriptSourceViewerConfiguration) {
			final ScriptTextTools textTools = getTextTools();
			if (textTools != null) {
				setSourceViewerConfiguration(textTools
						.createSourceViewerConfiguraton(store, this));
			}
		}
		if (getSourceViewer() instanceof ScriptSourceViewer) {
			((ScriptSourceViewer) getSourceViewer()).setPreferenceStore(store);
		}
		if (occurrencesFinder != null) {
			occurrencesFinder.setPreferenceStore(store);
		}
	}

	private ScriptOutlinePage createOutlinePage() {
		final ScriptOutlinePage page = doCreateOutlinePage();
		fOutlineSelectionChangedListener.install(page);
		setOutlinePageInput(page, getEditorInput());
		return page;
	}

	/**
	 * Creates the outline page used with this editor.
	 * 
	 * @return the created script outline page
	 */
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new ScriptOutlinePage(this, getPreferenceStore());
	}

	/**
	 * Informs the editor that its outliner has been closed.
	 */
	@Override
	public void outlinePageClosed() {
		if (fOutlinePage != null) {
			fOutlineSelectionChangedListener.uninstall(fOutlinePage);
			fOutlinePage = null;
			resetHighlightRange();
		}
	}

	private void setOutlinePageInput(ScriptOutlinePage page, IEditorInput input) {
		if (page == null) {
			return;
		}
		IModelElement me = getInputModelElement();
		if (me != null && me.exists()) {
			page.setInput(me);
		} else {
			page.setInput(null);
		}
	}

	/**
	 * The templates page.
	 * 
	 * @since 3.0
	 */
	private ScriptTemplatesPage2 fTemplatesPage;

	/**
	 * Creates the templates page used with this editor.
	 * 
	 * @return the created script templates page
	 * @since 3.0
	 */
	protected ScriptTemplatesPage2 createTemplatesPage() {
		final IDLTKUILanguageToolkit uiToolkit = getUILanguageToolkit();
		if (uiToolkit == null) {
			return null;
		}
		final ITemplateAccess templateAccess = uiToolkit.getEditorTemplates();
		if (templateAccess == null) {
			return null;
		}
		try {
			return new ScriptTemplatesPage2(this, templateAccess);
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
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null)
				fOutlinePage = createOutlinePage();
			return fOutlinePage;
		}
		if (required == IShowInTargetList.class) {
			return new IShowInTargetList() {
				@Override
				public String[] getShowInTargetIds() {
					return new String[] { DLTKUIPlugin.ID_SCRIPT_EXPLORER,
							IPageLayout.ID_OUTLINE };
				}
			};
		}
		if (required == OccurrencesFinder2.class) {
			return occurrencesFinder;
		}
		if (required == IFoldingStructureProvider.class)
			return fProjectionModelUpdater;
		if (required == IFoldingStructureProviderExtension.class)
			return fProjectionModelUpdater;

		if (fProjectionSupport != null) {
			Object adapter = fProjectionSupport.getAdapter(getSourceViewer(),
					required);
			if (adapter != null)
				return adapter;
		}

		return super.getAdapter(required);
	}

	protected void doSelectionChanged(SelectionChangedEvent event) {
		ISourceReference reference = null;
		ISelection selection = event.getSelection();
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof ISourceReference) {
				reference = (ISourceReference) o;
				break;
			}
		}
		if (!isActivePart() && DLTKUIPlugin.getActivePage() != null)
			DLTKUIPlugin.getActivePage().bringToTop(this);
		setSelection(reference, !isActivePart());
		if (occurrencesFinder != null) {
			occurrencesFinder.updateOccurrenceAnnotations();
		}
	}

	protected boolean isActivePart() {
		IWorkbenchPart part = getActivePart();
		return part != null && part.equals(this);
	}

	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		IPartService service = window.getPartService();
		IWorkbenchPart part = service.getActivePart();
		return part;
	}

	protected void setSelection(ISourceReference reference, boolean moveCursor) {
		if (getSelectionProvider() == null)
			return;
		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) selection;
			// PR 39995: [navigation] Forward history cleared after going back
			// in navigation history:
			// mark only in navigation history if the cursor is being moved
			// (which it isn't if
			// this is called from a PostSelectionEvent that should only update
			// the magnet)
			if (moveCursor
					&& (textSelection.getOffset() != 0 || textSelection
							.getLength() != 0))
				markInNavigationHistory();
		}
		if (reference != null) {
			StyledText textWidget = null;
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null)
				textWidget = sourceViewer.getTextWidget();
			if (textWidget == null)
				return;
			try {
				ISourceRange range = null;
				range = reference.getSourceRange();
				if (range == null)
					return;
				int offset = range.getOffset();
				int length = range.getLength();
				if (offset < 0 || length < 0)
					return;
				setHighlightRange(offset, length, moveCursor);
				if (!moveCursor)
					return;
				offset = -1;
				length = -1;
				if (reference instanceof IMember) {
					range = ((IMember) reference).getNameRange();
					if (range != null) {
						offset = range.getOffset();
						length = range.getLength();
					}
				} else if (reference instanceof ILocalVariable) {
					range = ((ILocalVariable) reference).getNameRange();
					if (range != null) {
						offset = range.getOffset();
						length = range.getLength();
					}
				} else if (reference instanceof IImportDeclaration
						|| reference instanceof IPackageDeclaration) {
					// range is still getSourceRange()
					offset = range.getOffset();
					length = range.getLength();
				}
				if (offset > -1 && length > 0) {
					try {
						textWidget.setRedraw(false);
						sourceViewer.revealRange(offset, length);
						sourceViewer.setSelectedRange(offset, length);
					} finally {
						textWidget.setRedraw(true);
					}
					markInNavigationHistory();
				}
			} catch (ModelException x) {
			} catch (IllegalArgumentException x) {
			}
		} else if (moveCursor) {
			resetHighlightRange();
			markInNavigationHistory();
		}
	}

	@Override
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		synchronizeOutlinePageSelection();
	}

	@Override
	public void setSelection(IModelElement element) {

		if (element == null || element instanceof ISourceModule) {
			/*
			 * If the element is an ISourceModule this unit is either the input
			 * of this editor or not being displayed. In both cases, nothing
			 * should happened.
			 * (http://dev.eclipse.org/bugs/show_bug.cgi?id=5128)
			 */
			return;
		}

		IModelElement corresponding = getCorrespondingElement(element);
		if (corresponding instanceof ISourceReference) {
			ISourceReference reference = (ISourceReference) corresponding;
			// set highlight range
			setSelection(reference, true);
			// set outliner selection
			if (fOutlinePage != null) {
				fOutlineSelectionChangedListener.uninstall(fOutlinePage);
				fOutlinePage.select(reference);
				fOutlineSelectionChangedListener.install(fOutlinePage);
			}
		}
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            thescriptelement to select
	 */
	protected void synchronizeOutlinePage(ISourceReference element) {
		synchronizeOutlinePage(element, true);
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            thescriptelement to select
	 * @param checkIfOutlinePageActive
	 *            <code>true</code> if check for active outline page needs to be
	 *            done
	 * @since 2.0
	 */
	@Override
	public void synchronizeOutlinePage(ISourceReference element,
			boolean checkIfOutlinePageActive) {
		if (fOutlinePage != null && element != null
				&& !(checkIfOutlinePageActive && isOutlinePageActive())) {
			fOutlineSelectionChangedListener.uninstall(fOutlinePage);
			fOutlinePage.select(element);
			fOutlineSelectionChangedListener.install(fOutlinePage);
		}
	}

	/**
	 * Synchronizes the outliner selection with the actual cursor position in
	 * the editor.
	 */
	public void synchronizeOutlinePageSelection() {
		synchronizeOutlinePage(computeHighlightRangeSourceReference());
	}

	private boolean isOutlinePageActive() {
		IWorkbenchPart part = getActivePart();
		return part instanceof ContentOutline
				&& ((ContentOutline) part).getCurrentPage() == fOutlinePage;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overrides the default implementation to handle {@link IJavaAnnotation}.
	 * </p>
	 * 
	 * @param offset
	 *            the region offset
	 * @param length
	 *            the region length
	 * @param forward
	 *            <code>true</code> for forwards, <code>false</code> for
	 *            backward
	 * @param annotationPosition
	 *            the position of the found annotation
	 * @return the found annotation
	 */
	@Override
	protected Annotation findAnnotation(final int offset, final int length,
			boolean forward, Position annotationPosition) {

		Annotation nextAnnotation = null;
		Position nextAnnotationPosition = null;
		Annotation containingAnnotation = null;
		Position containingAnnotationPosition = null;
		boolean currentAnnotation = false;

		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		int endOfDocument = document.getLength();
		int distance = Integer.MAX_VALUE;

		IAnnotationModel model = getDocumentProvider().getAnnotationModel(
				getEditorInput());
		Iterator<Annotation> e = new ScriptAnnotationIterator(model, true);
		while (e.hasNext()) {
			Annotation a = e.next();
			if ((a instanceof IScriptAnnotation)
					&& ((IScriptAnnotation) a).hasOverlay()
					|| !isNavigationTarget(a))
				continue;

			Position p = model.getPosition(a);
			if (p == null)
				continue;

			if (forward && p.offset == offset || !forward
					&& p.offset + p.getLength() == offset + length) {// ||
				// p.includes(offset))
				// {
				if (containingAnnotation == null
						|| (forward
								&& p.length >= containingAnnotationPosition.length || !forward
								&& p.length >= containingAnnotationPosition.length)) {
					containingAnnotation = a;
					containingAnnotationPosition = p;
					currentAnnotation = p.length == length;
				}
			} else {
				int currentDistance = 0;

				if (forward) {
					currentDistance = p.getOffset() - offset;
					if (currentDistance < 0)
						currentDistance = endOfDocument + currentDistance;

					if (currentDistance < distance
							|| currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				} else {
					currentDistance = offset + length
							- (p.getOffset() + p.length);
					if (currentDistance < 0)
						currentDistance = endOfDocument + currentDistance;

					if (currentDistance < distance
							|| currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				}
			}
		}
		if (containingAnnotationPosition != null
				&& (!currentAnnotation || nextAnnotation == null)) {
			annotationPosition.setOffset(containingAnnotationPosition
					.getOffset());
			annotationPosition.setLength(containingAnnotationPosition
					.getLength());
			return containingAnnotation;
		}
		if (nextAnnotationPosition != null) {
			annotationPosition.setOffset(nextAnnotationPosition.getOffset());
			annotationPosition.setLength(nextAnnotationPosition.getLength());
		}

		return nextAnnotation;
	}

	/**
	 * Returns the annotation overlapping with the given range or
	 * <code>null</code>.
	 * 
	 * @param offset
	 *            the region offset
	 * @param length
	 *            the region length
	 * @return the found annotation or <code>null</code>
	 * @since 3.0
	 */
	private Annotation getAnnotation(int offset, int length) {
		IAnnotationModel model = getDocumentProvider().getAnnotationModel(
				getEditorInput());
		Iterator<Annotation> e = new ScriptAnnotationIterator(model, false);
		while (e.hasNext()) {
			Annotation a = e.next();
			Position p = model.getPosition(a);
			if (p != null && p.overlapsWith(offset, length))
				return a;
		}
		return null;
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#gotoAnnotation(
	 * boolean)
	 * 
	 * @since 3.2
	 */
	@Override
	public Annotation gotoAnnotation(boolean forward) {
		fSelectionChangedViaGotoAnnotation = true;
		return super.gotoAnnotation(forward);
	}

	/**
	 * Computes and returns the source reference that includes the caret and
	 * serves as provider for the outline page selection and the editor range
	 * indication.
	 * 
	 * @return the computed source reference
	 * @since 2.0
	 */
	@Override
	public ISourceReference computeHighlightRangeSourceReference() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return null;
		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
			return null;
		int caret = 0;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			caret = extension.widgetOffset2ModelOffset(styledText
					.getCaretOffset());
		} else {
			int offset = sourceViewer.getVisibleRegion().getOffset();
			caret = offset + styledText.getCaretOffset();
		}
		IModelElement element = getElementAt(caret, false);
		if (!(element instanceof ISourceReference))
			return null;
		// if (element.getElementType() == IModelElement.IMPORT_DECLARATION) {
		//
		// IImportDeclaration declaration= (IImportDeclaration) element;
		// IImportContainer container= (IImportContainer)
		// declaration.getParent();
		// ISourceRange srcRange= null;
		//
		// try {
		// srcRange= container.getSourceRange();
		// } catch (ModelException e) {
		// }
		//
		// if (srcRange != null && srcRange.getOffset() == caret)
		// return container;
		// }
		return (ISourceReference) element;
	}

	/**
	 * React to changed selection.
	 * 
	 * 
	 */
	protected void selectionChanged() {
		if (getSelectionProvider() == null)
			return;
		ISourceReference element = computeHighlightRangeSourceReference();
		if (getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
			synchronizeOutlinePage(element);
		setSelection(element, false);
		if (!fSelectionChangedViaGotoAnnotation)
			updateStatusLine();
		fSelectionChangedViaGotoAnnotation = false;
	}

	protected void updateStatusLine() {
		ITextSelection selection = (ITextSelection) getSelectionProvider()
				.getSelection();
		Annotation annotation = getAnnotation(selection.getOffset(),
				selection.getLength());
		setStatusLineErrorMessage(null);
		setStatusLineMessage(null);
		if (annotation != null) {
			updateMarkerViews(annotation);
			if (annotation instanceof IScriptAnnotation
					&& ((IScriptAnnotation) annotation).isProblem())
				setStatusLineMessage(annotation.getText());
		}
	}

	/**
	 * Returns the model element wrapped by this editors input.
	 * 
	 * @return the model element wrapped by this editors input.
	 * 
	 */
	public IModelElement getInputModelElement() {
		return EditorUtility.getEditorInputModelElement(this, false);
	}

	/**
	 * Returns thescriptelement of this editor's input corresponding to the
	 * given IModelElement.
	 * 
	 * @param element
	 *            thescriptelement
	 * @return the corresponding model element
	 */
	protected IModelElement getCorrespondingElement(IModelElement element) {
		return element;
	}

	/**
	 * Returns the most narrow model element including the given offset.
	 * 
	 * @param offset
	 *            the offset inside of the requested element
	 * @return the most narrow model element
	 */
	@Override
	public IModelElement getElementAt(int offset) {
		return getElementAt(offset, true);
	}

	/**
	 * Returns the most narrow element including the given offset. If
	 * <code>reconcile</code> is <code>true</code> the editor's input element is
	 * reconciled in advance. If it is <code>false</code> this method only
	 * returns a result if the editor's input element does not need to be
	 * reconciled.
	 * 
	 * @param offset
	 *            the offset included by the retrieved element
	 * @param reconcile
	 *            <code>true</code> if working copy should be reconciled
	 * @return the most narrow element which includes the given offset
	 */
	public IModelElement getElementAt(int offset, boolean reconcile) {
		ISourceModule unit = (ISourceModule) getInputModelElement();
		if (unit != null) {
			try {
				if (reconcile) {
					ScriptModelUtil.reconcile(unit);
					return unit.getElementAt(offset);
				} else if (unit.isConsistent())
					return unit.getElementAt(offset);
			} catch (ModelException x) {
				if (!x.isDoesNotExist())
					// DLTKUIPlugin.log(x.getStatus());
					System.err.println(x.getStatus());
				// nothing found, be tolerant and go on
			}
		}
		return null;
	}

	/**
	 * The folding runner.
	 * 
	 * 
	 */
	private ToggleFoldingRunner fFoldingRunner;

	/**
	 * Tells whether the selection changed event is caused by a call to
	 * {@link #gotoAnnotation(boolean)}.
	 * 
	 */
	private boolean fSelectionChangedViaGotoAnnotation;

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
				if (!page.isPartVisible(ScriptEditor2.this)) {
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
			if (ScriptEditor2.this.equals(partRef.getPart(false))) {
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
			if (ScriptEditor2.this.equals(partRef.getPart(false))) {
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

	/**
	 * Creates folding structure provider to use in this editor. Default
	 * implementation queries the
	 * <code>org.eclipse.dltk.ui.folding/structureProvider</code> extension
	 * point.
	 * 
	 * @return folding structure provider or <code>null</code>.
	 */
	protected IFoldingStructureProvider createFoldingStructureProvider() {
		return FoldingProviderManager.getStructureProvider(getNatureId());
	}
	
	private boolean isEditorHoverProperty(String property) {
		return PreferenceConstants.EDITOR_TEXT_HOVER_MODIFIERS.equals(property);
	}

	/*
	 * Update the hovering behavior depending on the preferences.
	 */
	private void updateHoverBehavior() {
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		String[] types = configuration
				.getConfiguredContentTypes(getSourceViewer());

		for (int i = 0; i < types.length; i++) {

			String t = types[i];

			AdaptedSourceViewer sourceViewer = getSourceViewer_();
			if (sourceViewer instanceof ITextViewerExtension2) {
				// Remove existing hovers
				sourceViewer.removeTextHovers(t);

				int[] stateMasks = configuration
						.getConfiguredTextHoverStateMasks(getSourceViewer(), t);

				if (stateMasks != null) {
					for (int j = 0; j < stateMasks.length; j++) {
						int stateMask = stateMasks[j];
						ITextHover textHover = configuration.getTextHover(
								sourceViewer, t, stateMask);
						sourceViewer.setTextHover(textHover, t, stateMask);
					}
				} else {
					ITextHover textHover = configuration.getTextHover(
							sourceViewer, t);
					sourceViewer.setTextHover(
							textHover, t,
							ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
				}
			} else
				sourceViewer.setTextHover(configuration.getTextHover(sourceViewer, t), t);
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
			boolean newBooleanValue = false;
			Object newValue = event.getNewValue();

			if (isEditorHoverProperty(property))
				updateHoverBehavior();

			if (newValue != null)
				newBooleanValue = Boolean.valueOf(newValue.toString())
						.booleanValue();
			if (PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE
					.equals(property)) {
				if (newBooleanValue)
					selectionChanged();
				return;
			}

			if (occurrencesFinder != null
					&& occurrencesFinder.handlePreferenceStoreChanged(property,
							newBooleanValue)) {
				return;
			}

			if (CodeFormatterConstants.FORMATTER_TAB_SIZE.equals(property)
					|| CodeFormatterConstants.FORMATTER_INDENTATION_SIZE
							.equals(property)
					|| CodeFormatterConstants.FORMATTER_TAB_CHAR
							.equals(property)) {
				if (CodeFormatterConstants.FORMATTER_TAB_CHAR.equals(property)) {
					if (isTabsToSpacesConversionEnabled())
						installTabsToSpacesConverter();
					else
						uninstallTabsToSpacesConverter();
				}
				updateIndentPrefixes();
				StyledText textWidget = sourceViewer.getTextWidget();
				int tabWidth = getSourceViewerConfiguration().getTabWidth(
						sourceViewer);
				if (textWidget.getTabs() != tabWidth)
					textWidget.setTabs(tabWidth);
				return;
			}
			if (PreferenceConstants.EDITOR_SMART_TAB.equals(property)) {
				if (getPreferenceStore().getBoolean(
						PreferenceConstants.EDITOR_SMART_TAB)) {
					setActionActivationCode(DLTKActionConstants.INDENT_ON_TAB,
							SWT.TAB, -1, SWT.NONE);
				} else {
					removeActionActivationCode(DLTKActionConstants.INDENT_ON_TAB);
				}
			}

			if (isFoldingPropertyEvent(property)
					&& sourceViewer instanceof ProjectionViewer) {
				handleFoldingPropertyEvent(property);
			}

			final ScriptSourceViewerConfiguration ssvc = (ScriptSourceViewerConfiguration) getSourceViewerConfiguration();
			final IContentAssistant c = ((AdaptedSourceViewer) sourceViewer)
					.getContentAssistant();
			if (c instanceof ContentAssistant) {
				ssvc.changeContentAssistantConfiguration((ContentAssistant) c,
						event);
			}
			ssvc.handlePropertyChangeEvent(event);
		} finally {
			super.handlePreferenceStoreChanged(event);
		}
		if (AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR
				.equals(property)) {
			// superclass already installed the range indicator
			Object newValue = event.getNewValue();
			ISourceViewer viewer = getSourceViewer();
			if (newValue != null && viewer != null) {
				if (Boolean.valueOf(newValue.toString()).booleanValue()) {
					// adjust the highlightrange in order to get the magnet
					// right after changing the selection
					Point selection = viewer.getSelectedRange();
					adjustHighlightRange(selection.x, selection.y);
				}
			}
		}
	}

	@Override
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return ((ScriptSourceViewerConfiguration) getSourceViewerConfiguration())
				.affectsTextPresentation(event)
				|| super.affectsTextPresentation(event);
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
		
		final IDLTKLanguageToolkit toolkit = this.getLanguageToolkit();
		fProjectionSupport.setHoverControlCreator(new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell shell) {
				int shellStyle = SWT.TOOL | SWT.NO_TRIM | getOrientation();
				String statusFieldText = EditorsUI.getTooltipAffordanceString();
				return new SourceViewerInformationControl(shell,
						shellStyle, SWT.NONE, statusFieldText, toolkit);
			}
		});
		fProjectionSupport.setInformationPresenterControlCreator(new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(
					Shell shell) {
				int shellStyle = SWT.RESIZE | SWT.TOOL | getOrientation();
				int style = SWT.V_SCROLL | SWT.H_SCROLL;
				return new SourceViewerInformationControl(shell, shellStyle, style, toolkit);
			}
		});
		
		fProjectionSupport.install();
		
		fProjectionModelUpdater = createFoldingStructureProvider();
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.install(this, projectionViewer, getPreferenceStore());
		}
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
		ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
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
					fProjectionModelUpdater.install(this, projectionViewer,
							getPreferenceStore());
				projectionViewer.enableProjection();
			}

		} finally {
			projectionViewer.setRedraw(true);
		}
	}
	
	public String getNatureId() {
		return LangNature.NATURE_ID;
	}
	
	@Override
	public DeeLanguageToolkit getLanguageToolkit() {
		return DeeLanguageToolkit.getDefault();
	}
	
	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return DLTKUILanguageManager.getLanguageToolkit(getNatureId());
	}

	/*
	 * @see AbstractTextEditor#performSave(boolean, IProgressMonitor)
	 */
	@Override
	protected void performSave(boolean overwrite,
			IProgressMonitor progressMonitor) {
		IDocumentProvider p = getDocumentProvider();
		if (p instanceof ISourceModuleDocumentProvider) {
			ISourceModuleDocumentProvider cp = (ISourceModuleDocumentProvider) p;
			cp.setSavePolicy(fSavePolicy);
		}
		try {
			super.performSave(overwrite, progressMonitor);
		} finally {
			if (p instanceof ISourceModuleDocumentProvider) {
				ISourceModuleDocumentProvider cp = (ISourceModuleDocumentProvider) p;
				cp.setSavePolicy(null);
			}
		}
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

	private ICharacterPairMatcher fBracketMatcher;

	/**
	 * Returns the bracket matcher for this editor, delegates to
	 * {@link #createBracketMatcher()} to actually create it.
	 * 
	 * @return the bracket matcher or <code>null</code>
	 */
	public final ICharacterPairMatcher getBracketMatcher() {
		if (fBracketMatcher == null) {
			fBracketMatcher = createBracketMatcher();
		}
		return fBracketMatcher;
	}

	/**
	 * Override in your editor class to create bracket matcher for your
	 * language.
	 * 
	 * @return
	 */
	protected ICharacterPairMatcher createBracketMatcher() {
		return null;
	}

	@Override
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		configureBracketMatcher(support);
		super.configureSourceViewerDecorationSupport(support);
	}

	protected void configureBracketMatcher(SourceViewerDecorationSupport support) {
		final ICharacterPairMatcher bracketMatcher = getBracketMatcher();
		if (bracketMatcher != null) {
			support.setCharacterPairMatcher(bracketMatcher);
			support.setMatchingCharacterPainterPreferenceKeys(
					MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);
		}
	}

	public void updatedTitleImage(Image image) {
		setTitleImage(image);
	}


	@Override
	public void aboutToBeReconciled() {
		
		if(fSemanticManager != null && fSemanticManager.getReconciler() != null) {
			fSemanticManager.getReconciler().aboutToBeReconciled();
		}

	}

	@Override
	public void reconciled(ISourceModule ast, boolean forced, IProgressMonitor progressMonitor) {

		// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=58245
		// JavaPlugin javaPlugin= JavaPlugin.getDefault();
		// if (javaPlugin == null)
		// return;
		//
		// // Always notify AST provider
		// javaPlugin.getASTProvider().reconciled(ast, getInputJavaElement(),
		// progressMonitor);
		
		if(fSemanticManager != null && fSemanticManager.getReconciler() != null) {
			fSemanticManager.getReconciler().reconciled(ast, forced, progressMonitor);
		}
		
		// Update Outline page selection
		if (!forced && !progressMonitor.isCanceled()) {
			Shell shell = getSite().getShell();
			if (shell != null && !shell.isDisposed()) {
				shell.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						selectionChanged();
					}
				});
			}
		}
	}

	private SemanticHighlightingManager fSemanticManager;

	protected void installSemanticHighlighting() {
		fSemanticManager = SemanticHighlightingManager.install(getTextTools(), this, this.getPreferenceStore());
	}

	@Override
	public int getOrientation() {
		return SWT.LEFT_TO_RIGHT;
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		final List<String> result = new ArrayList<String>();
		final IDLTKUILanguageToolkit uiToolkit = getUILanguageToolkit();
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

	/*
	 * @see AbstractDecoratedTextEditor#isTabsToSpacesConversionEnabled()
	 */
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return getPreferenceStore() != null
				&& CodeFormatterConstants.SPACE.equals(getPreferenceStore()
						.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR));
	}

	private boolean isHandledPropertyEvent(String property, String[] handled) {
		for (int i = 0; i < handled.length; i++) {
			if (handled[i].equals(property)) {
				return true;
			}
		}

		return false;
	}

	protected String getSymbolicFontName() {
		return getFontPropertyPreferenceKey();
	}

	/*
	 * Increase visibility for this package - called from {@link
	 * OccurrencesFinder}
	 */
	@Override
	protected IProgressMonitor getProgressMonitor() {
		return super.getProgressMonitor();
	}
	
	public static int widgetOffset2ModelOffset_(ISourceViewer viewer, int widgetOffset) {
		return widgetOffset2ModelOffset(viewer, widgetOffset);
	}
	
}