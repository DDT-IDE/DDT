/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.text;

import melnorme.lang.ide.ui.editor.text.LangReconciler;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;

import _org.eclipse.dltk.internal.ui.editor.EditorUtility;
import _org.eclipse.dltk.internal.ui.text.ScriptCompositeReconcilingStrategy;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import _org.eclipse.jdt.internal.ui.text.CompositeReconcilingStrategy;

public class ScriptReconciler extends LangReconciler {
	
	/**
	 * Creates a new reconciler.
	 * 
	 * @param editor
	 *            the editor
	 * @param strategy
	 *            the reconcile strategy
	 * @param isIncremental
	 *            <code>true</code> if this is an incremental reconciler
	 */
	public ScriptReconciler(ITextEditor editor, CompositeReconcilingStrategy strategy,
			boolean isIncremental) {
		super(strategy, isIncremental, editor);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=63898
		// when re-using editors, a new reconciler is set up by the source
		// viewer
		// and the old one uninstalled. However, the old reconciler may still be
		// running.
		// To avoid having to reconcilers calling
		// CompilationUnitEditor.reconciled,
		// we synchronized on a lock object provided by the editor.
		// The critical section is really the entire run() method of the
		// reconciler
		// thread, but synchronizing process() only will keep
		// JavaReconcilingStrategy
		// from running concurrently on the same editor.
		// TODO remove once we have ensured that there is only one reconciler
		// per editor.
		if (editor instanceof ScriptEditor)
			fMutex = ((ScriptEditor) editor).getReconcilerLock();
		else
			fMutex = new Object(); // Null Object
	}

	/**
	 * Internal part listener for activating the reconciler.
	 */
	private class PartListener implements IPartListener {

		/*
		 * @see IPartListener#partActivated(IWorkbenchPart)
		 */
		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part == fTextEditor) {
				if (hasModelChanged())
					ScriptReconciler.this.forceReconciling();
				setEditorActive(true);
			}
		}

		/*
		 * @see IPartListener#partBroughtToTop(IWorkbenchPart)
		 */
		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		/*
		 * @see IPartListener#partClosed(IWorkbenchPart)
		 */
		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		/*
		 * @see IPartListener#partDeactivated(IWorkbenchPart)
		 */
		@Override
		public void partDeactivated(IWorkbenchPart part) {
			if (part == fTextEditor) {
				setModelChanged(false);
				setEditorActive(false);
			}
		}

		/*
		 * @see IPartListener#partOpened(IWorkbenchPart)
		 */
		@Override
		public void partOpened(IWorkbenchPart part) {
		}
	}

	/**
	 * Internal Shell activation listener for activating the reconciler.
	 */
	private class ActivationListener extends ShellAdapter {

		private Control fControl;

		public ActivationListener(Control control) {
			Assert.isNotNull(control);
			fControl = control;
		}

		@Override
		public void shellActivated(ShellEvent e) {
			if (!fControl.isDisposed() && fControl.isVisible()) {
				if (hasModelChanged())
					ScriptReconciler.this.forceReconciling();
				setEditorActive(true);
			}
		}

		@Override
		public void shellDeactivated(ShellEvent e) {
			if (!fControl.isDisposed() && fControl.getShell() == e.getSource()) {
				setModelChanged(false);
				setEditorActive(false);
			}
		}
	}

	/**
	 * Internal script element changed listener
	 * 
	 * @since 3.0
	 */
	private class ElementChangedListener implements IElementChangedListener {
		/*
		 * @see IElementChangedListener#elementChanged(ElementChangedEvent)
		 */
		@Override
		public void elementChanged(ElementChangedEvent event) {
			if (isRunningInReconcilerThread())
				return;

			if (canIgnore(event.getDelta().getAffectedChildren()))
				return;

			setModelChanged(true);
			if (isEditorActive())
				ScriptReconciler.this.forceReconciling();
		}

		/**
		 * Check whether the given delta has been sent when saving this
		 * reconciler's editor.
		 * 
		 * @param delta
		 *            the deltas
		 * @return <code>true</code> if the given delta
		 * @since 5.0
		 */
		private boolean canIgnore(IModelElementDelta[] delta) {
			if (delta.length != 1)
				return false;

			// become working copy
			if (delta[0].getFlags() == IModelElementDelta.F_PRIMARY_WORKING_COPY)
				return true;

			// save
			if (delta[0].getFlags() == IModelElementDelta.F_PRIMARY_RESOURCE
					&& delta[0].getElement().equals(fReconciledElement))
				return true;

			return canIgnore(delta[0].getAffectedChildren());
		}

	}

	/**
	 * Internal resource change listener.
	 * 
	 * @since 3.0
	 */
	class ResourceChangeListener implements IResourceChangeListener {

		private IResource getResource() {
			IEditorInput input = fTextEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				return fileInput.getFile();
			}
			return null;
		}

		/*
		 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
		 */
		@Override
		public void resourceChanged(IResourceChangeEvent e) {
			if (isRunningInReconcilerThread())
				return;

			IResourceDelta delta = e.getDelta();
			IResource resource = getResource();
			if (delta != null && resource != null) {
				IResourceDelta child = delta.findMember(resource.getFullPath());
				if (child != null) {
					IMarkerDelta[] deltas = child.getMarkerDeltas();
					int i = deltas.length;
					while (--i >= 0) {
						if (deltas[i].isSubtypeOf(IMarker.PROBLEM)) {
							forceReconciling();
							return;
						}
					}
				}
			}
		}
	}

	/** The part listener */
	private IPartListener fPartListener;
	/** The shell listener */
	private ShellListener fActivationListener;
	/**
	 * The mutex that keeps us from running multiple reconcilers on one editor.
	 */
	private Object fMutex;
	/**
	 * The script element changed listener.
	 * 
	 * @since 3.0
	 */
	private IElementChangedListener fScriptElementChangedListener;
	/**
	 * Tells whether the script model sent out a changed event.
	 * 
	 * @since 3.0
	 */
	private volatile boolean fHasModelChanged = true;
	/**
	 * Tells whether this reconciler's editor is active.
	 * 
	 * @since 3.1
	 */
	private volatile boolean fIsEditorActive = true;
	/**
	 * The resource change listener.
	 * 
	 * @since 3.0
	 */
	private IResourceChangeListener fResourceChangeListener;
	/**
	 * The property change listener.
	 * 
	 * @since 3.3
	 */
	private IPropertyChangeListener fPropertyChangeListener;

	private boolean fIninitalProcessDone = false;

	/**
	 * The element that this reconciler reconciles.
	 * 
	 * @since 3.4
	 */
	private ISourceModule fReconciledElement;

	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);

		fPartListener = new PartListener();
		IWorkbenchPartSite site = fTextEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().addPartListener(fPartListener);

		fActivationListener = new ActivationListener(textViewer.getTextWidget());
		Shell shell = window.getShell();
		shell.addShellListener(fActivationListener);

		fScriptElementChangedListener = new ElementChangedListener();
		DLTKCore.addElementChangedListener(fScriptElementChangedListener);

		fResourceChangeListener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				fResourceChangeListener);

		final IPreferenceStore store = getCombinedPreferenceStore();
		if (store != null) {
			fPropertyChangeListener = new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (SpellingService.PREFERENCE_SPELLING_ENABLED
							.equals(event.getProperty())
							|| SpellingService.PREFERENCE_SPELLING_ENGINE
									.equals(event.getProperty()))
						forceReconciling();
				}
			};
			store.addPropertyChangeListener(fPropertyChangeListener);
		}

		fReconciledElement = EditorUtility.getEditorInputModelElement(
				fTextEditor, false);
	}

	protected IPreferenceStore getCombinedPreferenceStore() {
		// JavaPlugin.getDefault().getCombinedPreferenceStore()
		// TODO implement when needed
		return null;
	}

	@Override
	public void uninstall() {

		IWorkbenchPartSite site = fTextEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().removePartListener(fPartListener);
		fPartListener = null;

		Shell shell = window.getShell();
		if (shell != null && !shell.isDisposed())
			shell.removeShellListener(fActivationListener);
		fActivationListener = null;

		DLTKCore.removeElementChangedListener(fScriptElementChangedListener);
		fScriptElementChangedListener = null;

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				fResourceChangeListener);
		fResourceChangeListener = null;

		if (fPropertyChangeListener != null) {
			final IPreferenceStore store = getCombinedPreferenceStore();
			if (store != null) {
				store.removePropertyChangeListener(fPropertyChangeListener);
			}
			fPropertyChangeListener = null;
		}

		super.uninstall();
	}

	@Override
	protected void forceReconciling() {
		if (!fIninitalProcessDone)
			return;

		super.forceReconciling();
		ScriptCompositeReconcilingStrategy strategy = (ScriptCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.notifyListeners(false);
	}

	@Override
	protected void aboutToBeReconciled() {
		ScriptCompositeReconcilingStrategy strategy = (ScriptCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.aboutToBeReconciled();
	}

	@Override
	protected void reconcilerReset() {
		super.reconcilerReset();
		ScriptCompositeReconcilingStrategy strategy = (ScriptCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.notifyListeners(true);
	}

	@Override
	protected void initialProcess() {
		synchronized (fMutex) {
			super.initialProcess();
		}
		fIninitalProcessDone = true;
	}

	/**
	 * Tells whether the script Model has changed or not.
	 * 
	 * @return <code>true</code> iff the script Model has changed
	 * @since 3.0
	 */
	private synchronized boolean hasModelChanged() {
		return fHasModelChanged;
	}

	/**
	 * Sets whether the script Model has changed or not.
	 * 
	 * @param state
	 *            <code>true</code> iff the script model has changed
	 * @since 3.0
	 */
	private synchronized void setModelChanged(boolean state) {
		fHasModelChanged = state;
	}

	/**
	 * Tells whether this reconciler's editor is active.
	 * 
	 * @return <code>true</code> iff the editor is active
	 * @since 3.1
	 */
	private synchronized boolean isEditorActive() {
		return fIsEditorActive;
	}

	/**
	 * Sets whether this reconciler's editor is active.
	 * 
	 * @param state
	 *            <code>true</code> iff the editor is active
	 * @since 3.1
	 */
	private synchronized void setEditorActive(boolean state) {
		fIsEditorActive = state;
	}
}
