/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.text;

import melnorme.utilbox.collections.ArrayList2;

import org.eclipse.dltk.internal.ui.text.IProblemRequestorExtension;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import _org.eclipse.jdt.internal.ui.text.CompositeReconcilingStrategy;

public class ScriptCompositeReconcilingStrategy extends CompositeReconcilingStrategy {
	private ITextEditor fEditor;
	private ScriptReconcilingStrategy fScriptStrategy;

	public ScriptCompositeReconcilingStrategy(ITextEditor editor, IReconcilingStrategy... other) {
		fEditor = editor;
		fScriptStrategy = new ScriptReconcilingStrategy(editor);
		ArrayList2<IReconcilingStrategy> strategies = new ArrayList2<IReconcilingStrategy>();
		strategies.add(fScriptStrategy);
		strategies.addElements(other);
		setReconcilingStrategies(strategies.toArray(IReconcilingStrategy.class));
	}

	private IProblemRequestorExtension getProblemRequestorExtension() {
		if (fEditor == null) {
			return null;
		}

		IDocumentProvider p = fEditor.getDocumentProvider();
		if (p == null) {
			p = DLTKUIPlugin.getDefault().getSourceModuleDocumentProvider();
		}
		IAnnotationModel m = p.getAnnotationModel(fEditor.getEditorInput());
		if (m instanceof IProblemRequestorExtension)
			return (IProblemRequestorExtension) m;
		return null;
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		IProblemRequestorExtension e = getProblemRequestorExtension();
		if (e != null) {
			try {
				e.beginReportingSequence();
				super.reconcile(dirtyRegion, subRegion);
			} finally {
				e.endReportingSequence();
			}
		} else {
			super.reconcile(dirtyRegion, subRegion);
		}
	}

	@Override
	public void reconcile(IRegion partition) {
		IProblemRequestorExtension e = getProblemRequestorExtension();
		if (e != null) {
			try {
				e.beginReportingSequence();
				super.reconcile(partition);
			} finally {
				e.endReportingSequence();
			}
		} else {
			super.reconcile(partition);
		}
	}

	@Override
	public void initialReconcile() {
		IProblemRequestorExtension e = getProblemRequestorExtension();
		if (e != null) {
			try {
				e.beginReportingSequence();
				super.initialReconcile();
			} finally {
				e.endReportingSequence();
			}
		} else {
			super.initialReconcile();
		}
	}

	/**
	 * Tells this strategy whether to inform its listeners.
	 * 
	 * @param notify
	 *            <code>true</code> if listeners should be notified
	 */
	public void notifyListeners(boolean notify) {
		fScriptStrategy.notifyListeners(notify);
	}

	/**
	 * Called before reconciling is started.
	 * 
	 * 
	 */
	public void aboutToBeReconciled() {
		fScriptStrategy.aboutToBeReconciled();
	}
}