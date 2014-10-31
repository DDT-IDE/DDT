/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.actions;

import org.eclipse.dltk.internal.ui.actions.QuickMenuAction;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.text.ScriptWordFinder;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditor2;

public abstract class DLTKQuickMenuAction extends QuickMenuAction {

	private ScriptEditor2 fEditor;

	public DLTKQuickMenuAction(String commandId) {
		super(commandId);
	}

	public DLTKQuickMenuAction(ScriptEditor2 editor, String commandId) {
		super(commandId);
		fEditor = editor;
	}

	protected ScriptEditor2 getScriptEditor() {
		return fEditor;
	}

	@Override
	protected Point computeMenuLocation(StyledText text) {
		if (fEditor == null || text != fEditor.getViewer().getTextWidget())
			return null;
		return computeWordStart();
	}

	private Point computeWordStart() {
		ITextSelection selection = (ITextSelection) fEditor
				.getSelectionProvider().getSelection();
		IRegion textRegion = ScriptWordFinder.findWord(fEditor.getViewer()
				.getDocument(), selection.getOffset());
		if (textRegion == null)
			return null;

		IRegion widgetRegion = modelRange2WidgetRange(textRegion);
		if (widgetRegion == null)
			return null;

		int start = widgetRegion.getOffset();

		StyledText styledText = fEditor.getViewer().getTextWidget();
		Point result = styledText.getLocationAtOffset(start);
		result.y += styledText.getLineHeight(start);

		if (!styledText.getClientArea().contains(result))
			return null;
		return result;
	}

	private IRegion modelRange2WidgetRange(IRegion region) {
		ISourceViewer viewer = fEditor.getViewer();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			return extension.modelRange2WidgetRange(region);
		}

		IRegion visibleRegion = viewer.getVisibleRegion();
		int start = region.getOffset() - visibleRegion.getOffset();
		int end = start + region.getLength();
		if (end > visibleRegion.getLength())
			end = visibleRegion.getLength();

		return new Region(start, end - start);
	}
}
