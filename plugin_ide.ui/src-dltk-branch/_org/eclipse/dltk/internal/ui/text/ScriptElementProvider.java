/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.text;


import melnorme.lang.ide.ui.text.util.JavaWordFinder;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;


/**
 * Provides a Script element to be displayed in by an information presenter.
 */
public class ScriptElementProvider implements IInformationProvider, IInformationProviderExtension {

	protected final AbstractDecoratedTextEditor fEditor;
	protected final boolean fUseCodeResolve;

	public ScriptElementProvider(AbstractDecoratedTextEditor editor) {
		this(editor, false);
	}

	public ScriptElementProvider(AbstractDecoratedTextEditor editor, boolean useCodeResolve) {
		fEditor = editor;
		fUseCodeResolve= useCodeResolve;
	}

	@Override
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		if (textViewer != null && fEditor != null) {
			IRegion region= JavaWordFinder.findWord(textViewer.getDocument(), offset);
			if (region != null)
				return region;
			else
				return new Region(offset, 0);
		}
		return null;
	}

	@Override
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return getInformation2(textViewer, subject).toString();
	}

	@Override
	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		if (fEditor == null)
			return null;

		try {
			if (fUseCodeResolve) {
				IStructuredSelection sel= SelectionConverter.getStructuredSelection(fEditor);
				if (!sel.isEmpty())
					return sel.getFirstElement();
			}
			IModelElement element= SelectionConverter.getElementAtOffset(fEditor);
			if (element != null)
				return element;
			
			return EditorUtility.getEditorInputModelElement(fEditor, false);
		} catch (ModelException e) {
			return null;
		}
	}
}
