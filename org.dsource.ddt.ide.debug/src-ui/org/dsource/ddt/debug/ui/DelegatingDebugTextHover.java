/*******************************************************************************
 * Copyright (c) 2010 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Wind River Systems - initial API and implementation
 *     Bruno Medeiros - adapted to DLTK's IScriptEditorTextHover
 *******************************************************************************/
package org.dsource.ddt.debug.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.cdt.ui.text.c.hover.ICEditorTextHover;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.dltk.ui.text.hover.IScriptEditorTextHover;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

// TODO: add this hover to the D editor
/**
 * Common debug text hover delegating to debugger specific implementations
 * based on active debug context.
 *
 * @since 7.0
 */
public class DelegatingDebugTextHover implements IScriptEditorTextHover, ITextHoverExtension, ITextHoverExtension2 {
	
	protected IEditorPart fEditor;
	protected ICEditorTextHover fDelegate;
	
	@Override
	public final void setEditor(IEditorPart editor) {
		fEditor = editor;
	}
	
	@Override
	public void setPreferenceStore(IPreferenceStore store) {
		// Not necessary.
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer viewer, int offset) {
		fDelegate = getDelegate();
		if (fDelegate != null) {
			return fDelegate.getHoverRegion(viewer, offset);
		}
		return null;
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		throw assertFail();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		fDelegate = getDelegate();
		if (fDelegate instanceof ITextHoverExtension2) {
			return ((ITextHoverExtension2) fDelegate).getHoverInfo2(textViewer, hoverRegion);
		}
		// fall back to legacy method
		if (fDelegate != null) {
			return fDelegate.getHoverInfo(textViewer, hoverRegion);
		}
		return null;
	}
	
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fDelegate instanceof ITextHoverExtension) {
			return ((ITextHoverExtension) fDelegate).getHoverControlCreator();
		}
		return null;
	}
	
	protected ICEditorTextHover getDelegate() {
		IAdaptable context = DebugUITools.getDebugContext();
		if (context != null) {
			ICEditorTextHover hover = (ICEditorTextHover) context.getAdapter(ICEditorTextHover.class);
			if (hover != null) {
				hover.setEditor(fEditor);
			}
			return hover;
		}
		return null;
	}
	
}