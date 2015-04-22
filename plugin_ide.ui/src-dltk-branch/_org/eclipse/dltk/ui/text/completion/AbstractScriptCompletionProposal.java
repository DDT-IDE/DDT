/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text.completion;

import melnorme.lang.ide.ui.text.completion.LangCompletionProposal;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.lang.tooling.ToolCompletionProposal;
import mmrnmhrm.ui.editor.hover.HoverUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.ui.text.hover.DocumentationHover;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IInformationControlCreator;
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
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public abstract class AbstractScriptCompletionProposal extends LangCompletionProposal {

	protected char[] fTriggerCharacters;

	protected StyleRange fRememberedStyleRange;

	private IInformationControlCreator fCreator;


	public AbstractScriptCompletionProposal(ToolCompletionProposal proposal, String additionalProposalInfo,
			Image image, IContextInformation contextInformation) {
		super(proposal, additionalProposalInfo, image, contextInformation);
	}

	@Override
	public char[] getTriggerCharacters() {
		return fTriggerCharacters;
	}

	/**
	 * Sets the trigger characters.
	 * 
	 * @param triggerCharacters
	 *            The set of characters which can trigger the application of
	 *            this completion proposal
	 */
	public void setTriggerCharacters(char[] triggerCharacters) {
		fTriggerCharacters = triggerCharacters;
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
		//if (getProposalInfo() != null) {
			String info= getProposalInfoString(monitor);
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		//}
	}
	
	protected abstract String getProposalInfoString(IProgressMonitor monitor);
	

	
	/* -----------------  ----------------- */
	

	/**
	 * Gets the replacement offset.
	 * 
	 * @return Returns a int
	 */
	public int getReplacementOffset() {
		return getReplaceOffset();
	}

	/**
	 * Gets the replacement length.
	 * 
	 * @return Returns a int
	 */
	public int getReplacementLength() {
		return getReplaceLength();
	}


	protected IPreferenceStore getPreferenceStore() {
		return DLTKUIPlugin.getDefault().getPreferenceStore();
	}

	protected boolean insertCompletion() {
		return getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	}

	protected Color getForegroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_FOREGROUND);
		ScriptTextTools textTools = getTextTools();
		if (textTools == null) {
			return null;
		}
		return textTools.getColorManager().getColor(rgb);
	}

	protected ScriptTextTools getTextTools() {
		return null;
	}

	protected Color getBackgroundColor(StyledText text) {

		RGB rgb = PreferenceConverter.getColor(getPreferenceStore(),
				PreferenceConstants.CODEASSIST_REPLACEMENT_BACKGROUND);
		ScriptTextTools textTools = getTextTools();
		if (textTools != null)
			return textTools.getColorManager().getColor(rgb);
		return null;
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

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		Shell shell = DLTKUIPlugin.getActiveWorkbenchShell();
		if (shell == null || !BrowserInformationControl.isAvailable(shell))
			return null;
		if (fCreator == null) {
			DocumentationHover.PresenterControlCreator presenterControlCreator = 
					new DocumentationHover.PresenterControlCreator(WorkbenchUtils.getActiveSite());
			fCreator = new DocumentationHover.HoverControlCreator(presenterControlCreator, true);
		}
		return fCreator;
	}
	
}