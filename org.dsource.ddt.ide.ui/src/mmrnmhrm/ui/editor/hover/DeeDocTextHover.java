/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package mmrnmhrm.ui.editor.hover;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.editor.BestMatchHover;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.editor.doc.DeeDocumentationProvider;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;

/**
 * Standard documentation hover for DDoc.
 * Used instead of {@link DeeDocumentationProvider} due to API limitation, review in the future.
 * (used in editor hovers extensions, and editor information provider (F2))
 */
public class DeeDocTextHover extends AbstractDocTextHover {
	
	public DeeDocTextHover() {
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return BestMatchHover.doGetHoverRegion(textViewer, offset);
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		IEditorPart editor = getEditor();
		ISourceModule sourceModule = EditorUtility.getEditorInputModelElement(editor, false);
		if(sourceModule == null) {
			return null;
		}
		
		int offset = hoverRegion.getOffset();
		
		assertTrue(Display.getCurrent() == null);
		String info = DToolClient.getDDocHTMLView(sourceModule, offset);
		
		if(info != null) {
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		}
		
		return null;
	}
	
}