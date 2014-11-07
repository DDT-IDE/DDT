/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.text.ScriptWordFinder;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.search.IOccurrencesFinder;
import org.eclipse.dltk.ui.search.IOccurrencesFinder.OccurrenceLocation;
import org.eclipse.dltk.ui.viewsupport.ISelectionListenerWithAST;
import org.eclipse.dltk.ui.viewsupport.SelectionListenerWithASTManager;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISelectionValidator;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * @since 3.0
 */
public class OccurrencesFinder2 {
	/**
	 * Tells whether all occurrences of the element at the current caret
	 * location are automatically marked in this editor.
	 */
	private boolean fMarkOccurrenceAnnotations;

	/**
	 * Tells whether the occurrence annotations are sticky i.e. whether they
	 * stay even if there's no valid Java element at the current caret position.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 */
	private boolean fStickyOccurrenceAnnotations;

	private OccurrencesFinderJob fOccurrencesFinderJob;
	/** The occurrences finder job canceler */
	private OccurrencesFinderJobCanceler fOccurrencesFinderJobCanceler;

	/**
	 * The document modification stamp at the time when the last occurrence
	 * marking took place.
	 */
	private long fMarkOccurrenceModificationStamp = IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;

	/**
	 * The region of the word under the caret used to when computing the current
	 * occurrence markings.
	 */
	private IRegion fMarkOccurrenceTargetRegion;

	/**
	 * The selection used when forcing occurrence marking through code.
	 */
	private ISelection fForcedMarkOccurrencesSelection;

	/**
	 * Holds the current occurrence annotations.
	 */
	private Annotation[] fOccurrenceAnnotations = null;

	private final ScriptEditor2 editor;
	private final IOccurrencesFinder[] finders;

	public OccurrencesFinder2(ScriptEditor2 editor) {
		this.editor = editor;
		final NatureExtensionManager<IOccurrencesFinder> occurrencesFinderManager = new NatureExtensionManager<IOccurrencesFinder>(
				DLTKUIPlugin.PLUGIN_ID + ".search", IOccurrencesFinder.class);
		finders = (IOccurrencesFinder[]) occurrencesFinderManager
				.getInstances(editor.getNatureId());
	}

	/**
	 * Finds and marks occurrence annotations.
	 * 
	 * @since 3.0
	 */
	class OccurrencesFinderJob extends Job {

		private final IDocument fDocument;
		private final ISelection fSelection;
		private final ISelectionValidator fPostSelectionValidator;
		private boolean fCanceled = false;
		private final OccurrenceLocation[] fLocations;

		public OccurrencesFinderJob(IDocument document,
				OccurrenceLocation[] locations, ISelection selection) {
			super(DLTKEditorMessages.ScriptEditor_markOccurrences_job_name);
			fDocument = document;
			fSelection = selection;
			fLocations = locations;

			if (getSelectionProvider() instanceof ISelectionValidator)
				fPostSelectionValidator = (ISelectionValidator) getSelectionProvider();
			else
				fPostSelectionValidator = null;
		}

		// cannot use cancel() because it is declared final
		void doCancel() {
			fCanceled = true;
			cancel();
		}

		private boolean isCanceled(IProgressMonitor progressMonitor) {
			return fCanceled
					|| progressMonitor.isCanceled()
					|| fPostSelectionValidator != null
					&& !(fPostSelectionValidator.isValid(fSelection) || fForcedMarkOccurrencesSelection == fSelection)
					|| LinkedModeModel.hasInstalledModel(fDocument);
		}

		/*
		 * @see Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public IStatus run(IProgressMonitor progressMonitor) {
			if (isCanceled(progressMonitor))
				return Status.CANCEL_STATUS;

			ITextViewer textViewer = getViewer();
			if (textViewer == null)
				return Status.CANCEL_STATUS;

			IDocument document = textViewer.getDocument();
			if (document == null)
				return Status.CANCEL_STATUS;

			IDocumentProvider documentProvider = getDocumentProvider();
			if (documentProvider == null)
				return Status.CANCEL_STATUS;

			IAnnotationModel annotationModel = documentProvider
					.getAnnotationModel(getEditorInput());
			if (annotationModel == null)
				return Status.CANCEL_STATUS;

			// Add occurrence annotations
			int length = fLocations.length;
			Map<Annotation, Position> annotationMap = new HashMap<Annotation, Position>(
					length);
			for (int i = 0; i < length; i++) {

				if (isCanceled(progressMonitor))
					return Status.CANCEL_STATUS;

				OccurrenceLocation location = fLocations[i];
				Position position = new Position(location.getOffset(),
						location.getLength());

				String description = location.getDescription();
				String annotationType = "org.eclipse.dltk.ui.occurrences"; //$NON-NLS-1$ 

				annotationMap.put(new Annotation(annotationType, false,
						description), position);
			}

			if (isCanceled(progressMonitor))
				return Status.CANCEL_STATUS;

			synchronized (getLockObject(annotationModel)) {
				if (annotationModel instanceof IAnnotationModelExtension) {
					((IAnnotationModelExtension) annotationModel)
							.replaceAnnotations(fOccurrenceAnnotations,
									annotationMap);
				} else {
					removeOccurrenceAnnotations();
					for (Map.Entry<Annotation, Position> mapEntry : annotationMap
							.entrySet()) {
						annotationModel.addAnnotation(mapEntry.getKey(),
								mapEntry.getValue());
					}
				}
				fOccurrenceAnnotations = annotationMap.keySet().toArray(
						new Annotation[annotationMap.keySet().size()]);
			}

			return Status.OK_STATUS;
		}
	}

	/**
	 * Cancels the occurrences finder job upon document changes.
	 * 
	 * @since 3.0
	 */
	class OccurrencesFinderJobCanceler implements IDocumentListener,
			ITextInputListener {

		public void install() {
			ISourceViewer sourceViewer = getViewer();
			if (sourceViewer == null)
				return;

			StyledText text = sourceViewer.getTextWidget();
			if (text == null || text.isDisposed())
				return;

			sourceViewer.addTextInputListener(this);

			IDocument document = sourceViewer.getDocument();
			if (document != null)
				document.addDocumentListener(this);
		}

		public void uninstall() {
			ISourceViewer sourceViewer = getViewer();
			if (sourceViewer != null)
				sourceViewer.removeTextInputListener(this);

			IDocumentProvider documentProvider = getDocumentProvider();
			if (documentProvider != null) {
				IDocument document = documentProvider
						.getDocument(getEditorInput());
				if (document != null)
					document.removeDocumentListener(this);
			}
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org
		 * .eclipse.jface.text.DocumentEvent)
		 */
		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
			if (fOccurrencesFinderJob != null)
				fOccurrencesFinderJob.doCancel();
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.
		 * jface.text.DocumentEvent)
		 */
		@Override
		public void documentChanged(DocumentEvent event) {
		}

		/*
		 * @see
		 * org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged
		 * (org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		@Override
		public void inputDocumentAboutToBeChanged(IDocument oldInput,
				IDocument newInput) {
			if (oldInput == null)
				return;

			oldInput.removeDocumentListener(this);
		}

		/*
		 * @see
		 * org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org
		 * .eclipse .jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		@Override
		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (newInput == null)
				return;
			newInput.addDocumentListener(this);
		}
	}

	private ActivationListener fActivationListener = new ActivationListener();
	private ISelectionListenerWithAST fPostSelectionListenerWithAST;

	/**
	 * Internal activation listener.
	 * 
	 * @since 3.0
	 */
	private class ActivationListener implements IWindowListener {

		/*
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.
		 * IWorkbenchWindow)
		 * 
		 * @since 3.1
		 */
		@Override
		public void windowActivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow()
					&& fMarkOccurrenceAnnotations && isActivePart()) {
				fForcedMarkOccurrencesSelection = getSelectionProvider()
						.getSelection();
				ISourceModule inputElement = getInputElement();
				if (inputElement != null)
					updateOccurrenceAnnotations(
							(ITextSelection) fForcedMarkOccurrencesSelection,
							inputElement,
							getAST(inputElement, getProgressMonitor()));
			}
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.
		 * IWorkbenchWindow)
		 * 
		 * @since 3.1
		 */
		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow()
					&& fMarkOccurrenceAnnotations && isActivePart())
				removeOccurrenceAnnotations();
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.
		 * IWorkbenchWindow)
		 * 
		 * @since 3.1
		 */
		@Override
		public void windowClosed(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.
		 * IWorkbenchWindow)
		 * 
		 * @since 3.1
		 */
		@Override
		public void windowOpened(IWorkbenchWindow window) {
		}
	}

	public void install() {
		PlatformUI.getWorkbench().addWindowListener(fActivationListener);
		if (isMarkingOccurrences()) {
			installOccurrencesFinder(false);
		}
	}

	public void dispose() {
		// cancel possible running computation
		fMarkOccurrenceAnnotations = false;
		uninstallOccurrencesFinder();

		if (fActivationListener != null) {
			PlatformUI.getWorkbench().removeWindowListener(fActivationListener);
			fActivationListener = null;
		}
	}

	private IPreferenceStore preferenceStore;

	public void setPreferenceStore(IPreferenceStore store) {
		preferenceStore = store;
		fMarkOccurrenceAnnotations = store
				.getBoolean(PreferenceConstants.EDITOR_MARK_OCCURRENCES);
		fStickyOccurrenceAnnotations = store
				.getBoolean(PreferenceConstants.EDITOR_STICKY_OCCURRENCES);
	}

	/**
	 * Checks if "mark occurrences" is enabled in preferences
	 * 
	 * @return
	 */
	public boolean isMarkingOccurrences() {
		return preferenceStore != null
				&& preferenceStore
						.getBoolean(PreferenceConstants.EDITOR_MARK_OCCURRENCES);
	}

	protected boolean handlePreferenceStoreChanged(String property,
			boolean newValue) {
		if (PreferenceConstants.EDITOR_MARK_OCCURRENCES.equals(property)) {
			if (newValue != fMarkOccurrenceAnnotations) {
				fMarkOccurrenceAnnotations = newValue;
				if (!newValue)
					uninstallOccurrencesFinder();
				else
					installOccurrencesFinder(true);
			}
			return true;
		} else if (PreferenceConstants.EDITOR_STICKY_OCCURRENCES
				.equals(property)) {
			fStickyOccurrenceAnnotations = newValue;
			return true;
		} else {
			return false;
		}
	}

	protected void installOccurrencesFinder(boolean forceUpdate) {
		fMarkOccurrenceAnnotations = true;

		fPostSelectionListenerWithAST = new ISelectionListenerWithAST() {
			@Override
			public void selectionChanged(IEditorPart part,
					ITextSelection selection, ISourceModule module,
					IModuleDeclaration astRoot) {
				updateOccurrenceAnnotations(selection, module, astRoot);
			}
		};
		SelectionListenerWithASTManager.getDefault().addListener(editor,
				fPostSelectionListenerWithAST);
		if (forceUpdate && getSelectionProvider() != null) {
			fForcedMarkOccurrencesSelection = getSelectionProvider()
					.getSelection();
			ISourceModule inputElement = getInputElement();
			if (inputElement != null)
				updateOccurrenceAnnotations(
						(ITextSelection) fForcedMarkOccurrencesSelection,
						inputElement,
						getAST(inputElement, getProgressMonitor()));
		}

		if (fOccurrencesFinderJobCanceler == null) {
			fOccurrencesFinderJobCanceler = new OccurrencesFinderJobCanceler();
			fOccurrencesFinderJobCanceler.install();
		}
	}

	protected void uninstallOccurrencesFinder() {
		fMarkOccurrenceAnnotations = false;

		if (fOccurrencesFinderJob != null) {
			fOccurrencesFinderJob.cancel();
			fOccurrencesFinderJob = null;
		}

		if (fOccurrencesFinderJobCanceler != null) {
			fOccurrencesFinderJobCanceler.uninstall();
			fOccurrencesFinderJobCanceler = null;
		}

		if (fPostSelectionListenerWithAST != null) {
			SelectionListenerWithASTManager.getDefault().removeListener(editor,
					fPostSelectionListenerWithAST);
			fPostSelectionListenerWithAST = null;
		}

		removeOccurrenceAnnotations();
	}

	public void updateOccurrenceAnnotations() {
		ISelectionProvider selectionProvider = getSelectionProvider();
		if (selectionProvider == null)
			return;

		ISelection textSelection = selectionProvider.getSelection();
		if (!(textSelection instanceof ITextSelection))
			return;

		ISourceModule inputElement = getInputElement();
		if (inputElement == null)
			return;

		IModuleDeclaration ast = getAST(inputElement, getProgressMonitor());
		if (ast != null) {
			fForcedMarkOccurrencesSelection = textSelection;
			updateOccurrenceAnnotations((ITextSelection) textSelection,
					inputElement, ast);
		}
	}

	/**
	 * Updates the occurrences annotations based on the current selection.
	 * 
	 * @param selection
	 *            the text selection
	 * @param astRoot
	 *            the compilation unit AST
	 * @since 3.0
	 */
	protected void updateOccurrenceAnnotations(ITextSelection selection,
			ISourceModule module, IModuleDeclaration astRoot) {

		if (fOccurrencesFinderJob != null)
			fOccurrencesFinderJob.cancel();

		if (!fMarkOccurrenceAnnotations)
			return;

		if (astRoot == null || selection == null)
			return;

		IDocument document = getViewer().getDocument();
		if (document == null)
			return;

		boolean hasChanged = false;
		if (document instanceof IDocumentExtension4) {
			int offset = selection.getOffset();
			long currentModificationStamp = ((IDocumentExtension4) document)
					.getModificationStamp();
			IRegion markOccurrenceTargetRegion = fMarkOccurrenceTargetRegion;
			hasChanged = currentModificationStamp != fMarkOccurrenceModificationStamp;
			if (markOccurrenceTargetRegion != null && !hasChanged) {
				if (markOccurrenceTargetRegion.getOffset() <= offset
						&& offset <= markOccurrenceTargetRegion.getOffset()
								+ markOccurrenceTargetRegion.getLength())
					return;
			}
			fMarkOccurrenceTargetRegion = ScriptWordFinder.findWord(document,
					offset);
			fMarkOccurrenceModificationStamp = currentModificationStamp;
		}

		OccurrenceLocation[] locations = null;

		if (finders != null) {
			for (IOccurrencesFinder finder : finders) {
				if (finder.initialize(module, astRoot, selection.getOffset(),
						selection.getLength()) == null) {
					locations = finder.getOccurrences();
					if (locations != null) {
						break;
					}
				}
			}
		}

		if (locations == null) {
			if (!fStickyOccurrenceAnnotations)
				removeOccurrenceAnnotations();
			else if (hasChanged) // check consistency of current annotations
				removeOccurrenceAnnotations();
			return;
		}

		fOccurrencesFinderJob = new OccurrencesFinderJob(document, locations,
				selection);
		// fOccurrencesFinderJob.setPriority(Job.DECORATE);
		// fOccurrencesFinderJob.setSystem(true);
		// fOccurrencesFinderJob.schedule();
		fOccurrencesFinderJob.run(new NullProgressMonitor());
	}

	void removeOccurrenceAnnotations() {
		fMarkOccurrenceModificationStamp = IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;
		fMarkOccurrenceTargetRegion = null;

		IDocumentProvider documentProvider = getDocumentProvider();
		if (documentProvider == null)
			return;

		IAnnotationModel annotationModel = documentProvider
				.getAnnotationModel(getEditorInput());
		if (annotationModel == null || fOccurrenceAnnotations == null)
			return;

		synchronized (getLockObject(annotationModel)) {
			if (annotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension) annotationModel)
						.replaceAnnotations(fOccurrenceAnnotations, null);
			} else {
				for (int i = 0, length = fOccurrenceAnnotations.length; i < length; i++)
					annotationModel.removeAnnotation(fOccurrenceAnnotations[i]);
			}
			fOccurrenceAnnotations = null;
		}
	}

	/**
	 * Returns the lock object for the given annotation model.
	 * 
	 * @param annotationModel
	 *            the annotation model
	 * @return the annotation model's lock object
	 * @since 3.0
	 */
	private static Object getLockObject(IAnnotationModel annotationModel) {
		if (annotationModel instanceof ISynchronizable) {
			Object lock = ((ISynchronizable) annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}

	// ///////////////

	protected boolean isActivePart() {
		return editor.isActivePart();
	}

	protected IEditorSite getEditorSite() {
		return editor.getEditorSite();
	}

	protected IModuleDeclaration getAST(IModelElement inputElement,
			IProgressMonitor progressMonitor) {
		return SourceParserUtil.parse((ISourceModule) inputElement, null);
	}

	protected ISourceModule getInputElement() {
		return (ISourceModule) editor.getInputModelElement();
	}

	protected IProgressMonitor getProgressMonitor() {
		return editor.getProgressMonitor();
	}

	protected ISelectionProvider getSelectionProvider() {
		return editor.getSelectionProvider();
	}

	protected IDocumentProvider getDocumentProvider() {
		return editor.getDocumentProvider();
	}

	protected IEditorInput getEditorInput() {
		return editor.getEditorInput();
	}

	protected ISourceViewer getViewer() {
		return editor.getSourceViewer_();
	}

	/**
	 * Checks if this object is correctly configured, i.e. has necessary finders
	 * installed, etc.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return finders != null;
	}

}
