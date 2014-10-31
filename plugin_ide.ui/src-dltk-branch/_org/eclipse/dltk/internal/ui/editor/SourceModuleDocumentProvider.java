/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.dltk.compiler.problem.CategorizedProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemFactory;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProblemRequestor;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.BufferManager;
import org.eclipse.dltk.internal.ui.IDLTKStatusConstants;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.DocumentAdapter;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.dltk.internal.ui.editor.ISavePolicy;
import org.eclipse.dltk.internal.ui.editor.ISourceModuleDocumentProvider;
import org.eclipse.dltk.internal.ui.editor.Messages;
import org.eclipse.dltk.internal.ui.editor.SourceForwardingDocumentProvider;
import org.eclipse.dltk.internal.ui.text.IProblemRequestorExtension;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.dltk.ui.editor.ScriptMarkerAnnotation;
import org.eclipse.dltk.ui.editor.SourceModuleAnnotationModelEvent;
import org.eclipse.dltk.ui.editor.saveparticipant.IPostSaveListener;
import org.eclipse.dltk.ui.editor.saveparticipant.SaveParticipantRegistry;
import org.eclipse.dltk.ui.text.ScriptAnnotationUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;
import org.eclipse.ui.texteditor.spelling.SpellingAnnotation;

public class SourceModuleDocumentProvider extends TextFileDocumentProvider
		implements ISourceModuleDocumentProvider {

	/** Indicates whether the save has been initialized by this provider */
	private boolean fIsAboutToSave = false;

	/** Preference key for temporary problems */
	private final static String HANDLE_TEMPORARY_PROBLEMS = PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS;

	/** The save policy used by this provider */
	private ISavePolicy fSavePolicy;

	/** Internal property changed listener */
	private IPropertyChangeListener fPropertyListener;
	/** Annotation model listener added to all created CU annotation models */
	private GlobalAnnotationModelListener fGlobalAnnotationModelListener;

	@Override
	public boolean isModifiable(Object element) {
		if (element instanceof FileStoreEditorInput) {
			ISourceModule module = DLTKUIPlugin
					.resolveSourceModule((FileStoreEditorInput) element);
			if (module != null) {
				return !module.isReadOnly();
			}
		}

		return super.isModifiable(element);
	}

	/**
	 * Annotation representing an <code>IProblem</code>.
	 */
	static public class ProblemAnnotation extends Annotation implements
			IScriptAnnotation, IAnnotationPresentation, IQuickFixableAnnotation {

		public static final String SPELLING_ANNOTATION_TYPE = SpellingAnnotation.TYPE;

		// XXX: To be fully correct these constants should be non-static
		/**
		 * The layer in which task problem annotations are located.
		 */
		private static final int TASK_LAYER;
		/**
		 * The layer in which info problem annotations are located.
		 */
		private static final int INFO_LAYER;
		/**
		 * The layer in which warning problem annotations representing are
		 * located.
		 */
		private static final int WARNING_LAYER;
		/**
		 * The layer in which error problem annotations representing are
		 * located.
		 */
		private static final int ERROR_LAYER;

		static {
			final AnnotationPreferenceLookup lookup = EditorsUI
					.getAnnotationPreferenceLookup();
			TASK_LAYER = computeLayer(
					ScriptMarkerAnnotation.TASK_ANNOTATION_TYPE, lookup);
			INFO_LAYER = computeLayer(
					ScriptMarkerAnnotation.INFO_ANNOTATION_TYPE, lookup);
			WARNING_LAYER = computeLayer(
					ScriptMarkerAnnotation.WARNING_ANNOTATION_TYPE, lookup);
			ERROR_LAYER = computeLayer(
					ScriptMarkerAnnotation.ERROR_ANNOTATION_TYPE, lookup);
		}

		private static int computeLayer(String annotationType,
				AnnotationPreferenceLookup lookup) {
			Annotation annotation = new Annotation(annotationType, false, null);
			AnnotationPreference preference = lookup
					.getAnnotationPreference(annotation);
			if (preference != null)
				return preference.getPresentationLayer() + 1;
			else
				return IAnnotationAccessExtension.DEFAULT_LAYER + 1;
		}

		private static Image fgQuickFixImage;
		private static Image fgQuickFixErrorImage;

		private static Image fgTaskImage;
		private static Image fgInfoImage;
		private static Image fgWarningImage;
		private static Image fgErrorImage;
		private static boolean fgImagesInitialized = false;

		private final ISourceModule fSourceModule;
		private List<IScriptAnnotation> fOverlaids;
		private final IProblem fProblem;
		private Image fImage;
		private boolean fImageInitialized = false;
		private int fLayer = IAnnotationAccessExtension.DEFAULT_LAYER;
		private boolean fIsQuickFixable;
		private boolean fIsQuickFixableStateSet = false;

		public ProblemAnnotation(IProblem problem, ISourceModule cu) {

			fProblem = problem;
			fSourceModule = cu;

//			if (SpellingProblems.SPELLING_PROBLEM == fProblem.getID()) {
//				setType(SPELLING_ANNOTATION_TYPE);
//				fLayer = WARNING_LAYER;
//			} else 
			if (fProblem.isTask()) {
				setType(ScriptMarkerAnnotation.TASK_ANNOTATION_TYPE);
				fLayer = TASK_LAYER;
			} else if (fProblem.isWarning()) {
				setType(ScriptMarkerAnnotation.WARNING_ANNOTATION_TYPE);
				fLayer = WARNING_LAYER;
			} else if (fProblem.isError()) {
				setType(ScriptMarkerAnnotation.ERROR_ANNOTATION_TYPE);
				fLayer = ERROR_LAYER;
			} else {
				setType(ScriptMarkerAnnotation.INFO_ANNOTATION_TYPE);
				fLayer = INFO_LAYER;
			}
		}

		/*
		 * @see org.eclipse.jface.text.source.IAnnotationPresentation#getLayer()
		 */
		@Override
		public int getLayer() {
			return fLayer;
		}

		/**
		 * delayed image loading - to be sure it is called on the UI thread
		 */
		private void initializeImage() {
			if (!fImageInitialized) {
				initializeImages();
				if (!isQuickFixableStateSet()) {
					setQuickFixable(isProblem()
							&& ScriptAnnotationUtils.hasCorrections(this));
				}
				if (isQuickFixable()) {
					if (ScriptMarkerAnnotation.ERROR_ANNOTATION_TYPE
							.equals(getType()))
						fImage = fgQuickFixErrorImage;
					else
						fImage = fgQuickFixImage;
				} else {
					final String type = getType();
					if (ScriptMarkerAnnotation.TASK_ANNOTATION_TYPE
							.equals(type))
						fImage = fgTaskImage;
					else if (ScriptMarkerAnnotation.INFO_ANNOTATION_TYPE
							.equals(type))
						fImage = fgInfoImage;
					else if (ScriptMarkerAnnotation.WARNING_ANNOTATION_TYPE
							.equals(type))
						fImage = fgWarningImage;
					else if (ScriptMarkerAnnotation.ERROR_ANNOTATION_TYPE
							.equals(type))
						fImage = fgErrorImage;
				}
				fImageInitialized = true;
			}
		}

		private static void initializeImages() {
			if (fgImagesInitialized)
				return;

			fgQuickFixImage = DLTKPluginImages
					.get(DLTKPluginImages.IMG_OBJS_FIXABLE_PROBLEM);
			fgQuickFixErrorImage = DLTKPluginImages
					.get(DLTKPluginImages.IMG_OBJS_FIXABLE_ERROR);

			final ISharedImages sharedImages = PlatformUI.getWorkbench()
					.getSharedImages();
			fgTaskImage = sharedImages.getImage(SharedImages.IMG_OBJS_TASK_TSK);
			fgInfoImage = sharedImages
					.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
			fgWarningImage = sharedImages
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
			fgErrorImage = sharedImages
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

			fgImagesInitialized = true;
		}

		/*
		 * @see Annotation#paint
		 */
		@Override
		public void paint(GC gc, Canvas canvas, Rectangle r) {
			initializeImage();
			if (fImage != null)
				ImageUtilities.drawImage(fImage, gc, canvas, r, SWT.CENTER,
						SWT.TOP);
		}

		/*
		 * @see IJavaAnnotation#getImage(Display)
		 */
		public Image getImage(Display display) {
			initializeImage();
			return fImage;
		}

		/*
		 * @see IJavaAnnotation#getMessage()
		 */
		@Override
		public String getText() {
			String[] arguments = getArguments();
			if (arguments != null) {
				for (int i = 0; i < arguments.length; i++) {
					String ar = arguments[i];
					if (ar.startsWith(IProblem.DESCRIPTION_ARGUMENT_PREFIX)) {
						return fProblem.getMessage()
								+ '\n'
								+ ar.substring(IProblem.DESCRIPTION_ARGUMENT_PREFIX
										.length());
					}
				}
			}
			return fProblem.getMessage();
		}

		/*
		 * @see IJavaAnnotation#getArguments()
		 */
		@Override
		public String[] getArguments() {
			return isProblem() ? fProblem.getArguments() : null;
		}

		/*
		 * @see IJavaAnnotation#getId()
		 */
		@Override
		public IProblemIdentifier getId() {
			return fProblem.getID();
		}

		/*
		 * @see IJavaAnnotation#isProblem()
		 */
		@Override
		public boolean isProblem() {
			String type = getType();
			return ScriptMarkerAnnotation.WARNING_ANNOTATION_TYPE.equals(type)
					|| ScriptMarkerAnnotation.ERROR_ANNOTATION_TYPE
							.equals(type)
					|| SPELLING_ANNOTATION_TYPE.equals(type);
		}

		/*
		 * @see IJavaAnnotation#hasOverlay()
		 */
		@Override
		public boolean hasOverlay() {
			return false;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getOverlay()
		 */
		@Override
		public IScriptAnnotation getOverlay() {
			return null;
		}

		/*
		 * @see IJavaAnnotation#addOverlaid(IJavaAnnotation)
		 */
		@Override
		public void addOverlaid(IScriptAnnotation annotation) {
			if (fOverlaids == null)
				fOverlaids = new ArrayList<IScriptAnnotation>(1);
			fOverlaids.add(annotation);
		}

		/*
		 * @see IJavaAnnotation#removeOverlaid(IJavaAnnotation)
		 */
		@Override
		public void removeOverlaid(IScriptAnnotation annotation) {
			if (fOverlaids != null) {
				fOverlaids.remove(annotation);
				if (fOverlaids.size() == 0)
					fOverlaids = null;
			}
		}

		/*
		 * @see IJavaAnnotation#getOverlaidIterator()
		 */
		@Override
		public Iterator getOverlaidIterator() {
			if (fOverlaids != null)
				return fOverlaids.iterator();
			return null;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getCompilationUnit
		 * ()
		 */
		@Override
		public ISourceModule getSourceModule() {
			return fSourceModule;
		}

		public IProblem getProblem() {
			return fProblem;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getMarkerType
		 * ()
		 */
		@Override
		public String getMarkerType() {
			if (fProblem instanceof CategorizedProblem)
				return ((CategorizedProblem) fProblem).getMarkerType();
			return null;
		}

		/*
		 * @seeorg.eclipse.jface.text.quickassist.IQuickFixableAnnotation#
		 * setQuickFixable(boolean)
		 * 
		 * @since 3.2
		 */
		@Override
		public void setQuickFixable(boolean state) {
			fIsQuickFixable = state;
			fIsQuickFixableStateSet = true;
		}

		/*
		 * @seeorg.eclipse.jface.text.quickassist.IQuickFixableAnnotation#
		 * isQuickFixableStateSet()
		 * 
		 * @since 3.2
		 */
		@Override
		public boolean isQuickFixableStateSet() {
			return fIsQuickFixableStateSet;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.quickassist.IQuickFixableAnnotation#isQuickFixable
		 * ()
		 * 
		 * @since 3.2
		 */
		@Override
		public boolean isQuickFixable() {
			Assert.isTrue(isQuickFixableStateSet());
			return fIsQuickFixable;
		}

		@Override
		public int getSourceStart() {
			return fProblem.getSourceStart();
		}

		@Override
		public int getSourceEnd() {
			return fProblem.getSourceEnd();
		}
	}

	/**
	 * Internal structure for mapping positions to some value. The reason for
	 * this specific structure is that positions can change over time. Thus a
	 * lookup is based on value and not on hash value.
	 */
	protected static class ReverseMap {

		static class Entry {
			Position fPosition;
			Object fValue;
		}

		private List<Entry> fList = new ArrayList<Entry>(2);
		private int fAnchor = 0;

		public ReverseMap() {
		}

		public Object get(Position position) {

			Entry entry;

			// behind anchor
			int length = fList.size();
			for (int i = fAnchor; i < length; i++) {
				entry = fList.get(i);
				if (entry.fPosition.equals(position)) {
					fAnchor = i;
					return entry.fValue;
				}
			}

			// before anchor
			for (int i = 0; i < fAnchor; i++) {
				entry = fList.get(i);
				if (entry.fPosition.equals(position)) {
					fAnchor = i;
					return entry.fValue;
				}
			}

			return null;
		}

		private int getIndex(Position position) {
			Entry entry;
			int length = fList.size();
			for (int i = 0; i < length; i++) {
				entry = fList.get(i);
				if (entry.fPosition.equals(position))
					return i;
			}
			return -1;
		}

		public void put(Position position, Object value) {
			int index = getIndex(position);
			if (index == -1) {
				Entry entry = new Entry();
				entry.fPosition = position;
				entry.fValue = value;
				fList.add(entry);
			} else {
				Entry entry = fList.get(index);
				entry.fValue = value;
			}
		}

		public void remove(Position position) {
			int index = getIndex(position);
			if (index > -1)
				fList.remove(index);
		}

		public void clear() {
			fList.clear();
		}
	}

	/**
	 * Annotation model dealing with java marker annotations and temporary
	 * problems. Also acts as problem requester for its compilation unit.
	 * Initially inactive. Must explicitly be activated.
	 */
	public static class SourceModuleAnnotationModel extends
			ResourceMarkerAnnotationModel implements IProblemRequestor,
			IProblemRequestorExtension {

		private static class ProblemRequestorState {
			boolean fInsideReportingSequence = false;
			List<IProblem> fReportedProblems;
		}

		private ThreadLocal<ProblemRequestorState> fProblemRequestorState = new ThreadLocal<ProblemRequestorState>();
		private int fStateCount = 0;

		private ISourceModule fSourceModule;
		private List<Annotation> fGeneratedAnnotations = new ArrayList<Annotation>();
		private IProgressMonitor fProgressMonitor;
		private boolean fIsActive = false;
		private boolean fIsHandlingTemporaryProblems;

		private ReverseMap fReverseMap = new ReverseMap();
		private List<ScriptMarkerAnnotation> fPreviouslyOverlaid = null;
		private List<ScriptMarkerAnnotation> fCurrentlyOverlaid = new ArrayList<ScriptMarkerAnnotation>();
		protected IProblemFactory problemFactory;

		public SourceModuleAnnotationModel(IResource resource) {
			super(resource);
		}

		public void setSourceModule(ISourceModule unit) {
			fSourceModule = unit;
		}

		@Override
		protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
			if (isScriptMarker(marker))
				return new ScriptMarkerAnnotation(marker);
			return super.createMarkerAnnotation(marker);
		}

		private boolean isScriptMarker(IMarker marker) {
			if (problemFactory != null) {
				return problemFactory.isValidMarker(marker);
			} else {
				String markerType = MarkerUtilities.getMarkerType(marker);
				return markerType != null
						&& markerType
								.startsWith(DefaultProblem.MARKER_TYPE_PREFIX);
			}
		}

		/*
		 * @see
		 * org.eclipse.jface.text.source.AnnotationModel#createAnnotationModelEvent
		 * ()
		 */
		@Override
		protected AnnotationModelEvent createAnnotationModelEvent() {
			return new SourceModuleAnnotationModelEvent(this, getResource());
		}

		protected Position createPositionFromProblem(IProblem problem) {
			int start = problem.getSourceStart();
			int end = problem.getSourceEnd();
			if (start <= 0 && end <= 0)
				return new Position(0);
			if (start < 0)
				return new Position(end);
			if (end < 0)
				return new Position(start);
			int length = end - start;
			if (length < 0)
				return null;
			if (fDocument != null) {
				final int documentLength = fDocument.getLength();
				if (start > documentLength)
					start = documentLength;
				if (start + length > documentLength) {
					length = documentLength - start;
				}
			}
			return new Position(start, length);
		}

		/*
		 * @see IProblemRequestor#beginReporting()
		 */
		@Override
		public void beginReporting() {
			ProblemRequestorState state = fProblemRequestorState.get();
			if (state == null)
				internalBeginReporting(false);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.java.IProblemRequestorExtension#
		 * beginReportingSequence()
		 */
		@Override
		public void beginReportingSequence() {
			ProblemRequestorState state = fProblemRequestorState.get();
			if (state == null)
				internalBeginReporting(true);
		}

		/**
		 * Sets up the infrastructure necessary for problem reporting.
		 * 
		 * @param insideReportingSequence
		 *            <code>true</code> if this method call is issued from
		 *            inside a reporting sequence
		 */
		private void internalBeginReporting(boolean insideReportingSequence) {

			// the same behavior as in
			// AbstractSourceModule.getAccumulatingProblemReporter
			if (fSourceModule != null && !fSourceModule.isReadOnly()) {
				ProblemRequestorState state = new ProblemRequestorState();
				state.fInsideReportingSequence = insideReportingSequence;
				state.fReportedProblems = new ArrayList<IProblem>();
				synchronized (getLockObject()) {
					fProblemRequestorState.set(state);
					++fStateCount;
				}
			}
		}

		/*
		 * @see IProblemRequestor#acceptProblem(IProblem)
		 */
		@Override
		public void acceptProblem(IProblem problem) {
			if (fIsHandlingTemporaryProblems
//					|| problem.getID() == SpellingProblems.SPELLING_PROBLEM
					) {
				ProblemRequestorState state = fProblemRequestorState.get();
				if (state != null)
					state.fReportedProblems.add(problem);
			}
		}

		/*
		 * @see IProblemRequestor#endReporting()
		 */
		@Override
		public void endReporting() {
			ProblemRequestorState state = fProblemRequestorState.get();
			if (state != null && !state.fInsideReportingSequence)
				internalEndReporting(state);
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.java.IProblemRequestorExtension#
		 * endReportingSequence()
		 */
		@Override
		public void endReportingSequence() {
			ProblemRequestorState state = fProblemRequestorState.get();
			if (state != null && state.fInsideReportingSequence)
				internalEndReporting(state);
		}

		private void internalEndReporting(ProblemRequestorState state) {
			int stateCount = 0;
			synchronized (getLockObject()) {
				--fStateCount;
				stateCount = fStateCount;
				fProblemRequestorState.set(null);
			}

			if (stateCount == 0)
				reportProblems(state.fReportedProblems);
		}

		/**
		 * Signals the end of problem reporting.
		 */
		private void reportProblems(List<IProblem> reportedProblems) {
			if (fProgressMonitor != null && fProgressMonitor.isCanceled())
				return;

			boolean temporaryProblemsChanged = false;

			synchronized (getLockObject()) {

				boolean isCanceled = false;

				fPreviouslyOverlaid = fCurrentlyOverlaid;
				fCurrentlyOverlaid = new ArrayList<ScriptMarkerAnnotation>();

				if (fGeneratedAnnotations.size() > 0) {
					temporaryProblemsChanged = true;
					removeAnnotations(fGeneratedAnnotations, false, true);
					fGeneratedAnnotations.clear();
				}

				if (reportedProblems != null && reportedProblems.size() > 0) {

					Iterator<IProblem> e = reportedProblems.iterator();
					while (e.hasNext()) {

						if (fProgressMonitor != null
								&& fProgressMonitor.isCanceled()) {
							isCanceled = true;
							break;
						}

						IProblem problem = e.next();
						Position position = createPositionFromProblem(problem);
						if (position != null) {

							try {
//								if (problem instanceof ScriptSpellingProblem) {
//									SpellingAnnotation annotation = new SpellingAnnotation(
//											((ScriptSpellingProblem) problem)
//													.getSpellingProblem());
//									addAnnotation(annotation, position, false);
//									fGeneratedAnnotations.add(annotation);
//								}
								ProblemAnnotation annotation = new ProblemAnnotation(
										problem, fSourceModule);
								overlayMarkers(position, annotation);

								addAnnotation(annotation, position, false);
								fGeneratedAnnotations.add(annotation);

								temporaryProblemsChanged = true;
							} catch (BadLocationException x) {
								// ignore invalid position
							}
						}
					}
				}

				removeMarkerOverlays(isCanceled);
				fPreviouslyOverlaid = null;
			}

			if (temporaryProblemsChanged)
				fireModelChanged();
		}

		private void removeMarkerOverlays(boolean isCanceled) {
			if (isCanceled) {
				fCurrentlyOverlaid.addAll(fPreviouslyOverlaid);
			} else if (fPreviouslyOverlaid != null) {
				for (ScriptMarkerAnnotation annotation : fPreviouslyOverlaid) {
					annotation.setOverlay(null);
				}
			}
		}

		/**
		 * Overlays value with problem annotation.
		 * 
		 * @param problemAnnotation
		 */
		private void setOverlay(Object value,
				ProblemAnnotation problemAnnotation) {
			if (value instanceof ScriptMarkerAnnotation) {
				ScriptMarkerAnnotation annotation = (ScriptMarkerAnnotation) value;
				if (annotation.isProblem()) {
					annotation.setOverlay(problemAnnotation);
					fPreviouslyOverlaid.remove(annotation);
					fCurrentlyOverlaid.add(annotation);
				}
			} else {
			}
		}

		private void overlayMarkers(Position position,
				ProblemAnnotation problemAnnotation) {
			Object value = getAnnotations(position);
			if (value instanceof List<?>) {
				List<?> list = (List<?>) value;
				for (Iterator<?> e = list.iterator(); e.hasNext();)
					setOverlay(e.next(), problemAnnotation);
			} else {
				setOverlay(value, problemAnnotation);
			}
		}

		/**
		 * Tells this annotation model to collect temporary problems from now
		 * on.
		 */
		private void startCollectingProblems() {
			fGeneratedAnnotations.clear();
		}

		/**
		 * Tells this annotation model to no longer collect temporary problems.
		 */
		private void stopCollectingProblems() {
			if (fGeneratedAnnotations != null)
				removeAnnotations(fGeneratedAnnotations, true, true);
			fGeneratedAnnotations.clear();
		}

		/*
		 * @see IProblemRequestor#isActive()
		 */
		@Override
		public boolean isActive() {
			return fIsActive;
		}

		/*
		 * @see IProblemRequestorExtension#setProgressMonitor(IProgressMonitor)
		 */
		@Override
		public void setProgressMonitor(IProgressMonitor monitor) {
			fProgressMonitor = monitor;
		}

		/*
		 * @see IProblemRequestorExtension#setIsActive(boolean)
		 */
		@Override
		public void setIsActive(boolean isActive) {
			fIsActive = isActive;
		}

		/*
		 * @see
		 * IProblemRequestorExtension#setIsHandlingTemporaryProblems(boolean)
		 * 
		 * @since 3.1
		 */
		@Override
		public void setIsHandlingTemporaryProblems(boolean enable) {
			if (fIsHandlingTemporaryProblems != enable) {
				fIsHandlingTemporaryProblems = enable;
				if (fIsHandlingTemporaryProblems)
					startCollectingProblems();
				else
					stopCollectingProblems();
			}

		}

		private Object getAnnotations(Position position) {
			synchronized (getLockObject()) {
				return fReverseMap.get(position);
			}
		}

		/*
		 * @see AnnotationModel#addAnnotation(Annotation, Position, boolean)
		 */
		@Override
		protected void addAnnotation(Annotation annotation, Position position,
				boolean fireModelChanged) throws BadLocationException {
			super.addAnnotation(annotation, position, fireModelChanged);

			synchronized (getLockObject()) {
				Object cached = fReverseMap.get(position);
				if (cached == null)
					fReverseMap.put(position, annotation);
				else if (cached instanceof List<?>) {
					@SuppressWarnings("unchecked")
					List<Annotation> list = (List<Annotation>) cached;
					list.add(annotation);
				} else if (cached instanceof Annotation) {
					List<Annotation> list = new ArrayList<Annotation>(2);
					list.add((Annotation) cached);
					list.add(annotation);
					fReverseMap.put(position, list);
				}
			}
		}

		/*
		 * @see AnnotationModel#removeAllAnnotations(boolean)
		 */
		@Override
		protected void removeAllAnnotations(boolean fireModelChanged) {
			super.removeAllAnnotations(fireModelChanged);
			synchronized (getLockObject()) {
				fReverseMap.clear();
			}
		}

		/*
		 * @see AnnotationModel#removeAnnotation(Annotation, boolean)
		 */
		@Override
		protected void removeAnnotation(Annotation annotation,
				boolean fireModelChanged) {
			Position position = getPosition(annotation);
			synchronized (getLockObject()) {
				Object cached = fReverseMap.get(position);
				if (cached instanceof List<?>) {
					List<?> list = (List<?>) cached;
					list.remove(annotation);
					if (list.size() == 1) {
						fReverseMap.put(position, list.get(0));
						list.clear();
					}
				} else if (cached instanceof Annotation) {
					fReverseMap.remove(position);
				}
			}
			super.removeAnnotation(annotation, fireModelChanged);
		}
	}

	/**
	 * Annotation model dealing with java marker annotations and temporary
	 * problems. Also acts as problem requester for its compilation unit.
	 * Initially inactive. Must explicitly be activated.
	 */
	protected static class ExternalSourceModuleAnnotationModel extends
			SourceModuleAnnotationModel {
		private final IPath location;

		public ExternalSourceModuleAnnotationModel(IPath location) {
			super(ResourcesPlugin.getWorkspace().getRoot());
			this.location = location;
		}

		/*
		 * @see AbstractMarkerAnnotationModel#retrieveMarkers()
		 */
		@Override
		protected IMarker[] retrieveMarkers() throws CoreException {
			String moduleLocation = location.toPortableString();
			IMarker[] markers = super.retrieveMarkers();
			List<IMarker> locationMarkers = new LinkedList<IMarker>();
			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				String markerLocation = (String) marker
						.getAttribute(IMarker.LOCATION);
				if (moduleLocation.equals(markerLocation)) {
					locationMarkers.add(marker);
				}
			}
			return locationMarkers.toArray(new IMarker[locationMarkers.size()]);
		}

		/**
		 * Updates this model to the given marker deltas.
		 * 
		 * @param markerDeltas
		 *            the array of marker deltas
		 */
		@Override
		protected void update(IMarkerDelta[] markerDeltas) {

			if (markerDeltas.length == 0)
				return;

			String moduleLocation = location.toPortableString();

			for (int i = 0; i < markerDeltas.length; i++) {
				IMarkerDelta delta = markerDeltas[i];
				IMarker marker = delta.getMarker();

				if (moduleLocation.equals(marker.getAttribute(IMarker.LOCATION,
						moduleLocation))) {
					switch (delta.getKind()) {
					case IResourceDelta.ADDED:
						addMarkerAnnotation(marker);
						break;
					case IResourceDelta.REMOVED:
						removeMarkerAnnotation(marker);
						break;
					case IResourceDelta.CHANGED:
						modifyMarkerAnnotation(marker);
						break;
					}
				}
			}

			fireModelChanged();
		}
	}

	protected static class GlobalAnnotationModelListener implements
			IAnnotationModelListener, IAnnotationModelListenerExtension {

		private ListenerList fListenerList;

		public GlobalAnnotationModelListener() {
			fListenerList = new ListenerList(ListenerList.IDENTITY);
		}

		/**
		 * @see IAnnotationModelListener#modelChanged(IAnnotationModel)
		 */
		@Override
		public void modelChanged(IAnnotationModel model) {
			Object[] listeners = fListenerList.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				((IAnnotationModelListener) listeners[i]).modelChanged(model);
			}
		}

		/**
		 * @see IAnnotationModelListenerExtension#modelChanged(AnnotationModelEvent)
		 */
		@Override
		public void modelChanged(AnnotationModelEvent event) {
			Object[] listeners = fListenerList.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				Object curr = listeners[i];
				if (curr instanceof IAnnotationModelListenerExtension) {
					((IAnnotationModelListenerExtension) curr)
							.modelChanged(event);
				}
			}
		}

		public void addListener(IAnnotationModelListener listener) {
			fListenerList.add(listener);
		}

		public void removeListener(IAnnotationModelListener listener) {
			fListenerList.remove(listener);
		}
	}

	/**
	 * Element information of all connected elements with a fake CU but no file
	 * info.
	 * 
	 * 
	 */
	private final Map<Object, SourceModuleInfo> fFakeCUMapForMissingInfo = new HashMap<Object, SourceModuleInfo>();

	public SourceModuleDocumentProvider() {

		IDocumentProvider provider = new TextFileDocumentProvider();
		provider = new SourceForwardingDocumentProvider(provider);
		setParentDocumentProvider(provider);

		fGlobalAnnotationModelListener = new GlobalAnnotationModelListener();
		fPropertyListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (HANDLE_TEMPORARY_PROBLEMS.equals(event.getProperty()))
					enableHandlingTemporaryProblems();
			}
		};
		DLTKUIPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(fPropertyListener);
	}

	/**
	 * Bundle of all required informations to allow working copy management.
	 */
	static protected class SourceModuleInfo extends FileInfo {

		public SourceModuleInfo() {
		}

		public ISourceModule fCopy;

		public IProblemRequestor getProblemRequestor() {
			return fModel instanceof IProblemRequestor ? (IProblemRequestor) fModel
					: null;
		}
	}

	@Override
	public void shutdown() {
		DLTKUIPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(fPropertyListener);

		Iterator e = getConnectedElementsIterator();
		while (e.hasNext()) {
			disconnect(e.next());
		}
	}

	@Override
	public ISourceModule getWorkingCopy(Object element) {
		FileInfo fileInfo = getFileInfo(element);
		if (fileInfo instanceof SourceModuleInfo) {
			SourceModuleInfo info = (SourceModuleInfo) fileInfo;
			return info.fCopy;
		}
		SourceModuleInfo cuInfo = fFakeCUMapForMissingInfo.get(element);
		if (cuInfo != null)
			return cuInfo.fCopy;

		return null;
	}

	@Override
	public void saveDocumentContent(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {

		if (!fIsAboutToSave) {
			return;
		}
		super.saveDocument(monitor, element, document, overwrite);
	}

	@Override
	public ILineTracker createLineTracker(Object element) {
		return new DefaultLineTracker();
	}

	/**
	 * Returns the preference whether handling temporary problems is enabled.
	 */
	protected boolean isHandlingTemporaryProblems() {
		IPreferenceStore store = DLTKUIPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(HANDLE_TEMPORARY_PROBLEMS);
	}

	/**
	 * Switches the state of problem acceptance according to the value in the
	 * preference store.
	 */
	protected void enableHandlingTemporaryProblems() {
		boolean enable = isHandlingTemporaryProblems();
		for (Iterator iter = getFileInfosIterator(); iter.hasNext();) {
			FileInfo info = (FileInfo) iter.next();
			if (info.fModel instanceof IProblemRequestorExtension) {
				IProblemRequestorExtension extension = (IProblemRequestorExtension) info.fModel;
				extension.setIsHandlingTemporaryProblems(enable);
			}
		}
	}

	@Override
	public void setSavePolicy(ISavePolicy savePolicy) {
		fSavePolicy = savePolicy;
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider
	 * #addGlobalAnnotationModelListener
	 * (org.eclipse.jface.text.source.IAnnotationModelListener)
	 */
	@Override
	public void addGlobalAnnotationModelListener(
			IAnnotationModelListener listener) {
		fGlobalAnnotationModelListener.addListener(listener);
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider
	 * #removeGlobalAnnotationModelListener
	 * (org.eclipse.jface.text.source.IAnnotationModelListener)
	 */
	@Override
	public void removeGlobalAnnotationModelListener(
			IAnnotationModelListener listener) {
		fGlobalAnnotationModelListener.removeListener(listener);
	}

	/**
	 * Creates a source module from the given file.
	 * 
	 * @param file
	 *            the file from which to create the source module
	 */
	protected ISourceModule createSourceModule(IFile file) {

		Object element = DLTKCore.create(file);
		if (element instanceof ISourceModule) {
			return (ISourceModule) element;
		}
		return null;
	}

	@Override
	protected FileInfo createEmptyFileInfo() {

		return new SourceModuleInfo();
	}

	private void setUpSynchronization(SourceModuleInfo cuInfo) {

		IDocument document = cuInfo.fTextFileBuffer.getDocument();
		IAnnotationModel model = cuInfo.fModel;

		if (document instanceof ISynchronizable
				&& model instanceof ISynchronizable) {
			Object lock = ((ISynchronizable) document).getLockObject();
			((ISynchronizable) model).setLockObject(lock);
		}
	}

	@Override
	protected IAnnotationModel createAnnotationModel(IFile file) {
		return new SourceModuleAnnotationModel(file);
	}

	static class DelegatingRequestor implements IProblemRequestor {

		IProblemRequestor fRequestor;

		@Override
		public void acceptProblem(IProblem problem) {
			if (fRequestor != null)
				fRequestor.acceptProblem(problem);
		}

		@Override
		public void beginReporting() {
			if (fRequestor != null)
				fRequestor.beginReporting();
		}

		@Override
		public void endReporting() {
			if (fRequestor != null)
				fRequestor.endReporting();
		}

		@Override
		public boolean isActive() {
			return fRequestor != null && fRequestor.isActive();
		}

	}

	@Override
	protected FileInfo createFileInfo(Object element) throws CoreException {

		ISourceModule original = null;

		if (element instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) element;
			original = createSourceModule(input.getFile());
		}
		if (original == null && element instanceof IAdaptable) {
			IModelElement modelE = (IModelElement) ((IAdaptable) element)
					.getAdapter(IModelElement.class);
			if (modelE != null && modelE instanceof ISourceModule) {
				original = (ISourceModule) modelE;
			}
		}

		FileInfo info = super.createFileInfo(element);

		if (!(info instanceof SourceModuleInfo))
			return null;

		DelegatingRequestor delegatingRequestor = null;
		if (original == null) {
			original = createFakeSourceModule(element, false,
					delegatingRequestor = new DelegatingRequestor());
		}
		if (original == null)
			return null;

		if (info.fModel == null) {
			// There is no resource for this ISourceModule, so markers are set
			// to workspace root

			IPath location = original.getPath();
			info.fModel = new ExternalSourceModuleAnnotationModel(location);
		}

		SourceModuleInfo cuInfo = (SourceModuleInfo) info;
		setUpSynchronization(cuInfo);

		final IProblemRequestor requestor = cuInfo.getProblemRequestor();
		if (delegatingRequestor != null) {
			delegatingRequestor.fRequestor = requestor;
		}
		if (requestor instanceof IProblemRequestorExtension) {
			IProblemRequestorExtension extension = (IProblemRequestorExtension) requestor;
			extension.setIsActive(false);
			extension
					.setIsHandlingTemporaryProblems(isHandlingTemporaryProblems());
		}

		if (ScriptModelUtil.isPrimary(original))
			original.becomeWorkingCopy(requestor, getProgressMonitor());
		cuInfo.fCopy = original;

		if (cuInfo.fModel instanceof SourceModuleAnnotationModel) {
			SourceModuleAnnotationModel model = (SourceModuleAnnotationModel) cuInfo.fModel;
			model.setSourceModule(cuInfo.fCopy);
		}

		if (cuInfo.fModel != null) {
			cuInfo.fModel
					.addAnnotationModelListener(fGlobalAnnotationModelListener);
		}

		return cuInfo;
	}

	@Override
	protected void disposeFileInfo(Object element, FileInfo info) {

		if (info instanceof SourceModuleInfo) {
			SourceModuleInfo cuInfo = (SourceModuleInfo) info;

			try {
				cuInfo.fCopy.discardWorkingCopy();
			} catch (ModelException x) {
				handleCoreException(x, x.getMessage());
			}

			// if( cuInfo.fModel != null )
			// cuInfo.fModel.removeAnnotationModelListener(
			// fGlobalAnnotationModelListener );
		}
		super.disposeFileInfo(element, info);
	}

	/**
	 * Creates and returns a new sub-progress monitor for the given parent
	 * monitor.
	 * 
	 * @param monitor
	 *            the parent progress monitor
	 * @param ticks
	 *            the number of work ticks allocated from the parent monitor
	 * @return the new sub-progress monitor
	 */
	private IProgressMonitor getSubProgressMonitor(IProgressMonitor monitor,
			int ticks) {

		if (monitor != null)
			return new SubProgressMonitor(monitor, ticks,
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);

		return new NullProgressMonitor();
	}

	@Override
	protected DocumentProviderOperation createSaveOperation(
			final Object element, final IDocument document,
			final boolean overwrite) throws CoreException {

		final FileInfo info = getFileInfo(element);
		if (info instanceof SourceModuleInfo) {

			// Delegate handling of non-primary SourceModules
			ISourceModule cu = ((SourceModuleInfo) info).fCopy;
			// condition should be the same as for becomeWorkingCopy() above
			if (cu != null && !ScriptModelUtil.isPrimary(cu))
				return super.createSaveOperation(element, document, overwrite);

			if (info.fTextFileBuffer.getDocument() != document) {
				// the info exists, but not for the given document
				// -> saveAs was executed with a target that is already open
				// in another editor
				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=85519
				System.out
						.println("SourceModuleDocumentProvider: need to replace with messages api"); //$NON-NLS-1$
				Status status = new Status(
						IStatus.WARNING,
						EditorsUI.PLUGIN_ID,
						IStatus.ERROR,
						Messages.SourceModuleDocumentProvider_saveAsTargetOpenInEditor,
						null);
				throw new CoreException(status);
			}

			return new DocumentProviderOperation() {
				/*
				 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider.
				 * DocumentProviderOperation
				 * #execute(org.eclipse.core.runtime.IProgressMonitor)
				 */
				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException {

					commitWorkingCopy(monitor, element,
							(SourceModuleInfo) info, overwrite);
				}

				/*
				 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider.
				 * DocumentProviderOperation#getSchedulingRule()
				 */
				@Override
				public ISchedulingRule getSchedulingRule() {

					if (info.fElement instanceof IFileEditorInput) {
						IFile file = ((IFileEditorInput) info.fElement)
								.getFile();
						return computeSchedulingRule(file);
					} else
						return null;
				}
			};
		}
		return null;
	}

	protected void commitWorkingCopy(IProgressMonitor monitor, Object element,
			final SourceModuleInfo info, boolean overwrite)
			throws CoreException {

		if (monitor == null)
			monitor = new NullProgressMonitor();

		monitor.beginTask("", 100); //$NON-NLS-1$

		try {

			IDocument document = info.fTextFileBuffer.getDocument();
			IResource resource = info.fCopy.getResource();

			Assert.isTrue(resource instanceof IFile);

			boolean isSynchronized = resource
					.isSynchronized(IResource.DEPTH_ZERO);
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=98327 Make sure
			 * file gets save in commit() if the underlying file has been
			 * deleted
			 */
			if (!isSynchronized && isDeleted(element))
				info.fTextFileBuffer.setDirty(true);

			if (!resource.exists()) {
				// underlying resource has been deleted, just recreate file,
				// ignore the rest
				createFileFromDocument(monitor, (IFile) resource, document);
				return;
			}

			if (fSavePolicy != null)
				fSavePolicy.preSave(info.fCopy);

			IProgressMonitor subMonitor = null;
			try {
				fIsAboutToSave = true;

				IPostSaveListener[] listeners = DLTKUIPlugin.getDefault()
						.getSaveParticipantRegistry()
						.getEnabledPostSaveListeners(info.fCopy);

				CoreException changedRegionException = null;
				boolean needsChangedRegions = false;
				try {
					if (listeners.length > 0)
						needsChangedRegions = SaveParticipantRegistry
								.isChangedRegionsRequired(info.fCopy, listeners);
				} catch (CoreException ex) {
					changedRegionException = ex;
				}

				IRegion[] changedRegions = null;
				if (needsChangedRegions) {
					try {
						changedRegions = EditorUtility
								.calculateChangedLineRegions(
										info.fTextFileBuffer,
										getSubProgressMonitor(monitor, 20));
					} catch (CoreException ex) {
						changedRegionException = ex;
					} finally {
						subMonitor = getSubProgressMonitor(monitor, 50);
					}
				} else
					subMonitor = getSubProgressMonitor(monitor,
							listeners.length > 0 ? 70 : 100);

				info.fCopy.commitWorkingCopy(isSynchronized || overwrite,
						subMonitor);
				if (listeners.length > 0)
					notifyPostSaveListeners(info, changedRegions, listeners,
							getSubProgressMonitor(monitor, 30));

				if (changedRegionException != null) {
					throw changedRegionException;
				}

				info.fCopy.commitWorkingCopy(isSynchronized || overwrite,
						subMonitor);
			} catch (CoreException x) {
				// inform about the failure
				fireElementStateChangeFailed(element);
				throw x;
			} catch (RuntimeException x) {
				// inform about the failure
				fireElementStateChangeFailed(element);
				throw x;
			} finally {
				fIsAboutToSave = false;
				if (subMonitor != null)
					subMonitor.done();
			}

			// If here, the dirty state of the editor will change to "not
			// dirty".
			// Thus, the state changing flag will be reset.
			if (info.fModel instanceof AbstractMarkerAnnotationModel) {
				AbstractMarkerAnnotationModel model = (AbstractMarkerAnnotationModel) info.fModel;
				model.updateMarkers(document);
			}

			if (fSavePolicy != null) {
				ISourceModule unit = fSavePolicy.postSave(info.fCopy);
				if (unit != null
						&& info.fModel instanceof AbstractMarkerAnnotationModel) {
					IResource r = unit.getResource();
					IMarker[] markers = r.findMarkers(IMarker.MARKER, true,
							IResource.DEPTH_ZERO);
					if (markers != null && markers.length > 0) {
						AbstractMarkerAnnotationModel model = (AbstractMarkerAnnotationModel) info.fModel;
						for (int i = 0; i < markers.length; i++)
							model.updateMarker(document, markers[i], null);
					}
				}
			}
		} finally {
			monitor.done();
		}
	}

	@Override
	public void connect(Object element) throws CoreException {
		super.connect(element);
		if (getFileInfo(element) != null)
			return;

		SourceModuleInfo info = fFakeCUMapForMissingInfo.get(element);
		if (info == null) {
			ISourceModule cu = null;
			if (element instanceof IAdaptable) {
				IModelElement e = (IModelElement) ((IAdaptable) element)
						.getAdapter(IModelElement.class);
				if (e != null && e instanceof ISourceModule) {
					cu = (ISourceModule) e;
				}
			}
			DelegatingRequestor delegatingRequestor = null;
			if (cu == null) {
				cu = createFakeSourceModule(element, true,
						delegatingRequestor = new DelegatingRequestor());
			}
			if (cu == null)
				return;
			info = new SourceModuleInfo();
			info.fCopy = cu;
			info.fElement = element;
			info.fModel = createAnnotationModel(element);
			if (delegatingRequestor != null) {
				delegatingRequestor.fRequestor = info.getProblemRequestor();
			}
			fFakeCUMapForMissingInfo.put(element, info);
		}
		info.fCount++;
	}

	private IAnnotationModel createAnnotationModel(Object element) {
		if (element instanceof ExternalStorageEditorInput) {
			final IModelElement modelElement = (IModelElement) ((ExternalStorageEditorInput) element)
					.getAdapter(IModelElement.class);
			if (modelElement != null) {
				final IPath path = modelElement.getPath();
				if (path != null) {
					return new ExternalSourceModuleAnnotationModel(path);
				}
			}
		}
		return new AnnotationModel();
	}

	/*
	 * @see
	 * org.eclipse.ui.editors.text.TextFileDocumentProvider#getAnnotationModel
	 * (java.lang.Object)
	 */
	@Override
	public IAnnotationModel getAnnotationModel(Object element) {
		IAnnotationModel model = super.getAnnotationModel(element);
		if (model != null)
			return model;

		FileInfo info = fFakeCUMapForMissingInfo.get(element);
		if (info != null) {
			if (info.fModel != null)
				return info.fModel;
			if (info.fTextFileBuffer != null)
				return info.fTextFileBuffer.getAnnotationModel();
		}

		return null;
	}

	/*
	 * @see
	 * org.eclipse.ui.editors.text.TextFileDocumentProvider#disconnect(java.
	 * lang.Object)
	 */
	@Override
	public void disconnect(Object element) {
		SourceModuleInfo info = fFakeCUMapForMissingInfo.get(element);
		if (info != null) {
			if (info.fCount == 1) {
				fFakeCUMapForMissingInfo.remove(element);
				info.fModel = null;
				// Destroy and unregister fake working copy
				try {
					info.fCopy.discardWorkingCopy();
				} catch (ModelException ex) {
					handleCoreException(ex, ex.getMessage());
				}
			} else
				info.fCount--;
		}
		super.disconnect(element);
	}

	/**
	 * Creates a fake compilation unit.
	 * 
	 * @param element
	 *            the element
	 * @param setContents
	 *            tells whether to read and set the contents to the new CU
	 * 
	 */
	private ISourceModule createFakeSourceModule(Object element,
			boolean setContents, IProblemRequestor requestor) {
		if (element instanceof IStorageEditorInput)
			return createFakeSourceModule((IStorageEditorInput) element,
					setContents, requestor);
		else if (element instanceof IURIEditorInput)
			return createFakeSourceModule((IURIEditorInput) element, requestor);
		else if (element instanceof NonExistingFileEditorInput)
			return createFakeSourceModule((NonExistingFileEditorInput) element,
					requestor);
		return null;
	}

	private ISourceModule createFakeSourceModule(
			NonExistingFileEditorInput editorInput, IProblemRequestor requestor) {
		try {
			final IPath path = editorInput.getPath(editorInput);
			URI uri = URIUtil.toURI(path);
			final IFileStore fileStore = EFS.getStore(uri);

			if (fileStore.getName() == null || path == null)
				return null;

			WorkingCopyOwner woc = new WorkingCopyOwner() {
				/*
				 * @see
				 * org.eclipse.jdt.core.WorkingCopyOwner#createBuffer(org.eclipse
				 * .jdt.core.ICompilationUnit)
				 * 
				 * @since 3.2
				 */
				@Override
				public IBuffer createBuffer(ISourceModule workingCopy) {
					return new DocumentAdapter(workingCopy, fileStore, path);
				}
			};

			IBuildpathEntry[] cpEntries = null;
			IScriptProject jp = findScriptProject(path);
			if (jp != null)
				cpEntries = jp.getResolvedBuildpath(true);

			if (cpEntries == null || cpEntries.length == 0)
				cpEntries = new IBuildpathEntry[] { ScriptRuntime
						.getDefaultInterpreterContainerEntry() };

			final ISourceModule cu = woc.newWorkingCopy(fileStore.getName(),
					cpEntries, requestor, getProgressMonitor());

			if (!isModifiable(editorInput))
				ScriptModelUtil.reconcile(cu);

			return cu;
		} catch (CoreException ex) {
			return null;
		}
	}

	private ISourceModule createFakeSourceModule(
			final IURIEditorInput editorInput, IProblemRequestor requestor) {
		try {
			final URI uri = editorInput.getURI();
			final IFileStore fileStore = EFS.getStore(uri);
			final IPath path = URIUtil.toPath(uri);
			final String fileStoreName = fileStore.getName();
			if (fileStoreName == null || path == null)
				return null;

			WorkingCopyOwner woc = new WorkingCopyOwner() {
				/*
				 * @see
				 * org.eclipse.jdt.core.WorkingCopyOwner#createBuffer(org.eclipse
				 * .jdt.core.ICompilationUnit)
				 * 
				 * @since 3.2
				 */
				@Override
				public IBuffer createBuffer(ISourceModule workingCopy) {
					return new DocumentAdapter(workingCopy, fileStore, path);
				}
			};

			IBuildpathEntry[] cpEntries = null;
			IScriptProject jp = findScriptProject(path);
			if (jp != null)
				cpEntries = jp.getResolvedBuildpath(true);

			if (cpEntries == null || cpEntries.length == 0)
				cpEntries = new IBuildpathEntry[] { ScriptRuntime
						.getDefaultInterpreterContainerEntry() };

			final ISourceModule cu = woc.newWorkingCopy(fileStoreName,
					cpEntries, requestor, getProgressMonitor());

			if (!isModifiable(editorInput))
				ScriptModelUtil.reconcile(cu);

			return cu;
		} catch (CoreException ex) {
			return null;
		}
	}

	private ISourceModule createFakeSourceModule(final IStorageEditorInput sei,
			boolean setContents, IProblemRequestor requestor) {
		try {
			final IStorage storage = sei.getStorage();
			final IPath storagePath = storage.getFullPath();
			if (storage.getName() == null || storagePath == null)
				return null;

			// final IPath documentPath;
			// if (storage instanceof IFileState)
			// documentPath = storagePath
			// .append(Long.toString(((IFileState) storage)
			// .getModificationTime()));
			// else
			// documentPath = storagePath;

			WorkingCopyOwner woc = new WorkingCopyOwner() {
				@Override
				public IBuffer createBuffer(ISourceModule workingCopy) {
					return BufferManager.createBuffer(workingCopy);
				}
			};

			IBuildpathEntry[] cpEntries = null;
			IScriptProject jp = findScriptProject(storagePath);
			if (jp != null)
				cpEntries = jp.getResolvedBuildpath(true);

			if (cpEntries == null || cpEntries.length == 0)
				cpEntries = new IBuildpathEntry[] { ScriptRuntime
						.getDefaultInterpreterContainerEntry() };

			final ISourceModule cu = woc.newWorkingCopy(storage.getName(),
					cpEntries, requestor, getProgressMonitor());
			if (setContents) {
				int READER_CHUNK_SIZE = 2048;
				int BUFFER_SIZE = 8 * READER_CHUNK_SIZE;
				Reader in = new BufferedReader(new InputStreamReader(
						storage.getContents()));
				StringBuffer buffer = new StringBuffer(BUFFER_SIZE);
				char[] readBuffer = new char[READER_CHUNK_SIZE];
				int n;
				try {
					n = in.read(readBuffer);
					while (n > 0) {
						buffer.append(readBuffer, 0, n);
						n = in.read(readBuffer);
					}
					in.close();
				} catch (IOException e) {
					DLTKUIPlugin.log(e);
				}
				cu.getBuffer().setContents(buffer.toString());
			}

			if (!isModifiable(sei))
				ScriptModelUtil.reconcile(cu);

			return cu;
		} catch (CoreException ex) {
			return null;
		}
	}

	/**
	 * Fuzzy search for script project in the workspace that matches the given
	 * path.
	 * 
	 * @param path
	 *            the path to match
	 * @return the matching script project or <code>null</code>
	 * 
	 */
	private IScriptProject findScriptProject(IPath path) {
		if (path == null)
			return null;

		String[] pathSegments = path.segments();
		IScriptModel model = DLTKCore.create(DLTKUIPlugin.getWorkspace()
				.getRoot());
		IScriptProject[] projects;
		try {
			projects = model.getScriptProjects();
		} catch (ModelException e) {
			return null; // ignore - use default RE
		}
		for (int i = 0; i < projects.length; i++) {
			IPath projectPath = projects[i].getProject().getFullPath();
			String projectSegment = projectPath.segments()[0];
			for (int j = 0; j < pathSegments.length; j++)
				if (projectSegment.equals(pathSegments[j]))
					return projects[i];
		}
		return null;
	}

	@Override
	public boolean isReadOnly(Object element) {
		if (element instanceof ExternalStorageEditorInput) {
			return true;
		}
		if (element instanceof FileStoreEditorInput) {
			ISourceModule module = DLTKUIPlugin
					.resolveSourceModule((FileStoreEditorInput) element);
			if (module != null) {
				return module.isReadOnly();
			}
		}
		return super.isReadOnly(element);
	}

	/**
	 * Notify post save listeners.
	 * <p>
	 * <strong>Note:</strong> Post save listeners are not allowed to save the
	 * file and they must not assumed to be called in the UI thread i.e. if they
	 * open a dialog they must ensure it ends up in the UI thread.
	 * </p>
	 * 
	 * @param info
	 *            compilation unit info
	 * @param changedRegions
	 *            the array with the changed regions
	 * @param listeners
	 *            the listeners to notify
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 *             if something goes wrong
	 * @see IPostSaveListener
	 * @since 3.0
	 */
	protected void notifyPostSaveListeners(final SourceModuleInfo info,
			final IRegion[] changedRegions, IPostSaveListener[] listeners,
			final IProgressMonitor monitor) throws CoreException {
		final ISourceModule unit = info.fCopy;
		final IBuffer buffer = unit.getBuffer();

		String message = DLTKEditorMessages.CompilationUnitDocumentProvider_error_saveParticipantProblem;
		final MultiStatus errorStatus = new MultiStatus(DLTKUIPlugin.PLUGIN_ID,
				IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION, message,
				null);

		monitor.beginTask(
				DLTKEditorMessages.CompilationUnitDocumentProvider_progressNotifyingSaveParticipants,
				listeners.length * 5);
		try {
			for (int i = 0; i < listeners.length; i++) {
				final IPostSaveListener listener = listeners[i];
				final String participantName = listener.getName();
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void run() {
						try {
							long stamp = unit.getResource()
									.getModificationStamp();

							listener.saved(unit, changedRegions,
									getSubProgressMonitor(monitor, 4));

							if (stamp != unit.getResource()
									.getModificationStamp()) {
								String msg = NLS
										.bind(DLTKEditorMessages.CompilationUnitDocumentProvider_error_saveParticipantSavedFile,
												participantName);
								errorStatus
										.add(new Status(
												IStatus.ERROR,
												DLTKUIPlugin.PLUGIN_ID,
												IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION,
												msg, null));
							}

							if (buffer.hasUnsavedChanges())
								buffer.save(getSubProgressMonitor(monitor, 1),
										true);

						} catch (CoreException ex) {
							handleException(ex);
						} finally {
							monitor.worked(1);
						}
					}

					@Override
					public void handleException(Throwable ex) {
						String msg = NLS
								.bind("The save participant ''{0}'' caused an exception: {1}", listener.getId(), ex.toString()); //$NON-NLS-1$
						DLTKUIPlugin
								.log(new Status(
										IStatus.ERROR,
										DLTKUIPlugin.PLUGIN_ID,
										IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION,
										msg, ex));

						msg = NLS
								.bind(DLTKEditorMessages.CompilationUnitDocumentProvider_error_saveParticipantFailed,
										participantName, ex.toString());
						errorStatus
								.add(new Status(
										IStatus.ERROR,
										DLTKUIPlugin.PLUGIN_ID,
										IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION,
										msg, null));

						// Revert the changes
						if (buffer.hasUnsavedChanges()) {
							try {
								info.fTextFileBuffer
										.revert(getSubProgressMonitor(monitor,
												1));
							} catch (CoreException e) {
								msg = NLS
										.bind("Error on revert after failure of save participant ''{0}''.", participantName); //$NON-NLS-1$
								IStatus status = new Status(
										IStatus.ERROR,
										DLTKUIPlugin.PLUGIN_ID,
										IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION,
										msg, ex);
								DLTKUIPlugin.getDefault().getLog().log(status);
							}

							if (info.fModel instanceof AbstractMarkerAnnotationModel) {
								AbstractMarkerAnnotationModel markerModel = (AbstractMarkerAnnotationModel) info.fModel;
								markerModel.resetMarkers();
							}
						}

						// XXX: Work in progress 'Save As' case
						// else if (buffer.hasUnsavedChanges()) {
						// try {
						// buffer.save(getSubProgressMonitor(monitor, 1), true);
						// } catch (JavaModelException e) {
						//								message= Messages.format("Error reverting changes after failure of save participant ''{0}''.", participantName); //$NON-NLS-1$
						// IStatus status= new Status(IStatus.ERROR,
						// JavaUI.ID_PLUGIN, IStatus.OK, message, ex);
						// JavaPlugin.getDefault().getLog().log(status);
						// }
						// }
					}
				});
			}
		} finally {
			monitor.done();
			if (!errorStatus.isOK())
				throw new CoreException(errorStatus);
		}
	}
}
