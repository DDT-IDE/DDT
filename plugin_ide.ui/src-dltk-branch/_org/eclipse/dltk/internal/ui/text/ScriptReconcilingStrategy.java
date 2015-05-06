/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.text;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.text.IProblemRequestorExtension;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IWorkingCopyManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class ScriptReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	private ITextEditor fEditor;

	private IWorkingCopyManager fManager;

	private IDocumentProvider fDocumentProvider;

	private IProgressMonitor fProgressMonitor;

	private IScriptReconcilingListener fScriptReconcilingListener;
	private boolean fIsScriptReconcilingListener;

	private boolean fNotify = true;

	public ScriptReconcilingStrategy(ITextEditor editor) {
		fEditor = editor;
		fManager = DLTKUIPlugin.getDefault().getWorkingCopyManager();
		fDocumentProvider = DLTKUIPlugin.getDefault().getSourceModuleDocumentProvider();
		
		fIsScriptReconcilingListener = fEditor instanceof IScriptReconcilingListener;
		if (fIsScriptReconcilingListener) {
			fScriptReconcilingListener = (IScriptReconcilingListener) fEditor;
		}
	}

	// private static class ReconcilerFeedback extends Job {
	//
	// public ReconcilerFeedback() {
	// super(TextMessages.ScriptReconcilingStrategy_ReconcilingJobName);
	// setPriority(Job.SHORT);
	// schedule(1000);
	// }
	//
	// @Override
	// protected IStatus run(IProgressMonitor monitor) {
	// for (;;) {
	// if (monitor.isCanceled()) {
	// break;
	// }
	// synchronized (lock) {
	// if (canceled) {
	// break;
	// }
	// try {
	// lock.wait(1000);
	// } catch (InterruptedException e) {
	// break;
	// }
	// }
	// }
	// return Status.OK_STATUS;
	// }
	//
	// private final Object lock = new Object();
	// private boolean canceled = false;
	//
	// public void stop() {
	// synchronized (lock) {
	// canceled = true;
	// lock.notify();
	// }
	// cancel();
	// }
	//
	// }
	
	private IProblemRequestorExtension getProblemRequestorExtension() {
		IAnnotationModel model = fDocumentProvider.getAnnotationModel(fEditor.getEditorInput());
		if (model instanceof IProblemRequestorExtension)
			return (IProblemRequestorExtension) model;
		return null;
	}

	protected void reconcile(final boolean initialReconcile) {
		if (fEditor == null) {
			return;
		}

		final ISourceModule unit = fManager.getWorkingCopy(fEditor.getEditorInput());

		if (unit == null) {
			return;
		}

		try {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void run() throws ModelException {
					// final ReconcilerFeedback feedback = new
					// ReconcilerFeedback();
					// try {
					reconcile(unit, initialReconcile);
					// } finally {
					// feedback.stop();
					// }
				}

				@Override
				public void handleException(Throwable ex) {
					DLTKUIPlugin.logErrorMessage(
							"Error in DLTK Core during reconcile", ex);//$NON-NLS-1$
				}
			});
		} finally {
			// Always notify listeners, see
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=55969 for the final
			// solution
			try {
				if (fIsScriptReconcilingListener) {
					IProgressMonitor pm = fProgressMonitor;
					if (pm == null)
						pm = new NullProgressMonitor();
					fScriptReconcilingListener.reconciled(unit, !fNotify, pm);
				}
			} finally {
				fNotify = true;
			}
		}
	}

	private void reconcile(ISourceModule unit, boolean initialReconcile) throws ModelException {
		/* fix for missing cancel flag communication */
		IProblemRequestorExtension extension = getProblemRequestorExtension();
		if (extension != null) {
			extension.setProgressMonitor(fProgressMonitor);
			extension.setIsActive(true);
		}

		try {
			// reconcile
			unit.reconcile(true, null, fProgressMonitor);
		} catch (OperationCanceledException ex) {
			Assert.isTrue(fProgressMonitor == null
					|| fProgressMonitor.isCanceled());
		} finally {
			/* fix for missing cancel flag communication */
			if (extension != null) {
				extension.setProgressMonitor(null);
				extension.setIsActive(false);
			}
		}
	}

	public void aboutToBeReconciled() {
		if (fIsScriptReconcilingListener) {
			fScriptReconcilingListener.aboutToBeReconciled();
		}
	}

	@Override
	public void setDocument(IDocument document) {

	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(false);
	}

	@Override
	public void reconcile(IRegion partition) {
		reconcile(false);
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

	@Override
	public void initialReconcile() {
		reconcile(true);
	}

	public void notifyListeners(boolean notify) {
		fNotify = notify;
	}
	
}