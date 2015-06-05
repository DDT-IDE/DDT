/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.text.completion.LangCompletionProposal;
import melnorme.lang.tooling.ToolCompletionProposal;
import mmrnmhrm.ui.editor.hover.HoverUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import _org.eclipse.cdt.ui.text.IColorManager;
import _org.eclipse.dltk.ui.PreferenceConstants;


/* TODO: DLTK review this code*/
public abstract class AbstractScriptCompletionProposal extends LangCompletionProposal {

	protected StyleRange fRememberedStyleRange;


	public AbstractScriptCompletionProposal(ToolCompletionProposal proposal, String additionalProposalInfo,
			Image image, IContextInformation contextInformation) {
		super(proposal, additionalProposalInfo, image, contextInformation);
	}

	/* --------------------------------- */
	
	/** The CSS used to format javadoc information. */
	private static String fgCSSStyles;
	
	/** Returns the style information for displaying HTML (Javadoc) content. */
	protected String getCSSStyles() {
		if (fgCSSStyles == null) {
			fgCSSStyles= HoverUtil.getDDocPreparedCSS("/JavadocHoverStyleSheet.css");
		}
		return fgCSSStyles;
	}
	
	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		String info= getProposalInfoString(monitor);
		return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
	}
	
	protected abstract String getProposalInfoString(IProgressMonitor monitor);
	

	
	/* -----------------  ----------------- */
	

	public int getReplacementOffset() {
		return getReplaceOffset();
	}

	public int getReplacementLength() {
		return getReplaceLength();
	}


	protected IPreferenceStore getPreferenceStore() {
		return LangUIPlugin.getDefault().getPreferenceStore();
	}

	protected boolean insertCompletion() {
		return getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	}

	protected Color getForegroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_FOREGROUND);
		IColorManager colorManager = LangUIPlugin.getDefault().getColorManager();
		return colorManager.getColor(rgb);
	}

	protected Color getBackgroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_BACKGROUND);
		IColorManager colorManager = LangUIPlugin.getDefault().getColorManager();
		return colorManager.getColor(rgb);
	}

	private void repairPresentation(ITextViewer viewer) {
		if (fRememberedStyleRange != null) {
			if (viewer instanceof ITextViewerExtension2) {
				// attempts to reduce the redraw area
				ITextViewerExtension2 viewer2 = (ITextViewerExtension2) viewer;

				if (viewer instanceof ITextViewerExtension5) {

					ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
					IRegion modelRange = extension
							.widgetRange2ModelRange(new Region(
									fRememberedStyleRange.start,
									fRememberedStyleRange.length));
					if (modelRange != null)
						viewer2.invalidateTextPresentation(
								modelRange.getOffset(), modelRange.getLength());

				} else {
					viewer2.invalidateTextPresentation(
							fRememberedStyleRange.start
									+ viewer.getVisibleRegion().getOffset(),
							fRememberedStyleRange.length);
				}

			} else
				viewer.invalidateTextPresentation();
		}
	}

	private void updateStyle(ITextViewer viewer) {

		StyledText text = viewer.getTextWidget();
		if (text == null || text.isDisposed())
			return;

		int widgetCaret = text.getCaretOffset();

		int modelCaret = 0;
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			modelCaret = extension.widgetOffset2ModelOffset(widgetCaret);
		} else {
			IRegion visibleRegion = viewer.getVisibleRegion();
			modelCaret = widgetCaret + visibleRegion.getOffset();
		}

		if (modelCaret >= getReplacementOffset() + getReplacementLength()) {
			repairPresentation(viewer);
			return;
		}

		int offset = widgetCaret;
		int length = getReplacementOffset() + getReplacementLength()
				- modelCaret;

		Color foreground = getForegroundColor(text);
		Color background = getBackgroundColor(text);

		StyleRange range = text.getStyleRangeAtOffset(offset);
		int fontStyle = range != null ? range.fontStyle : SWT.NORMAL;

		repairPresentation(viewer);
		fRememberedStyleRange = new StyleRange(offset, length, foreground,
				background, fontStyle);
		if (range != null) {
			fRememberedStyleRange.strikeout = range.strikeout;
			fRememberedStyleRange.underline = range.underline;
		}

		// http://dev.eclipse.org/bugs/show_bug.cgi?id=34754
		try {
			text.setStyleRange(fRememberedStyleRange);
		} catch (IllegalArgumentException x) {
			// catching exception as offset + length might be outside of the
			// text widget
			fRememberedStyleRange = null;
		}
	}

	@Override
	public void selected(ITextViewer viewer, boolean smartToggle) {
		if (!insertCompletion() ^ smartToggle)
			updateStyle(viewer);
		else {
			repairPresentation(viewer);
			fRememberedStyleRange = null;
		}
	}

	@Override
	public void unselected(ITextViewer viewer) {
		repairPresentation(viewer);
		fRememberedStyleRange = null;
	}

}