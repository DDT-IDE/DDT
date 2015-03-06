/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.dltk.ui.templates.ScriptTemplateProposal;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IWorkbenchPartOrientation;

public abstract class ScriptTemplateCompletionProcessor extends
		TemplateCompletionProcessor {

	private static final class ProposalComparator implements
			Comparator<TemplateProposal> {
		@Override
		public int compare(TemplateProposal o1, TemplateProposal o2) {
			return o2.getRelevance() - o1.getRelevance();
		}
	}

	private static final Comparator<TemplateProposal> comparator = new ProposalComparator();

	private final ScriptContentAssistInvocationContext context;

	public ScriptTemplateCompletionProcessor(
			ScriptContentAssistInvocationContext context) {
		Assert.isNotNull(context);
		this.context = context;
	}

	protected ScriptContentAssistInvocationContext getContext() {
		return this.context;
	}

	private static final String $_LINE_SELECTION = "${" + GlobalTemplateVariables.LineSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String $_WORD_SELECTION = "${" + GlobalTemplateVariables.WordSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		ITextSelection selection = (ITextSelection) viewer
				.getSelectionProvider().getSelection();

		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset)
			offset = selection.getOffset() + selection.getLength();

		List<TemplateProposal> matches = new ArrayList<TemplateProposal>();

		if (selection.getLength() == 0) {
			String prefix = extractPrefix(viewer, offset);
			if (!isValidPrefix(prefix)) {
				return new ICompletionProposal[0];
			}
			IRegion region = new Region(offset - prefix.length(),
					prefix.length());
			TemplateContext context = createContext(viewer, region);
			if (context == null)
				return new ICompletionProposal[0];
			// name of the selection variables {line, word}_selection
			context.setVariable("selection", selection.getText()); //$NON-NLS-1$
			Template[] templates = getTemplates(context.getContextType()
					.getId());
			for (int i = 0; i != templates.length; i++) {
				final Template template = templates[i];
				try {
					context.getContextType().validate(template.getPattern());
				} catch (TemplateException e) {
					continue;
				}
				if (isMatchingTemplate(template, prefix, context)) {
					matches.add((TemplateProposal) createProposal(template,
							context, region, getRelevance(template, prefix)));
				}
			}
		} else {
			IRegion region = new Region(offset - selection.getLength(),
					selection.getLength());
			TemplateContext context = createContext(viewer, region);
			if (context == null)
				return new ICompletionProposal[0];
			// name of the selection variables {line, word}_selection
			context.setVariable("selection", selection.getText()); //$NON-NLS-1$
			Template[] templates = getTemplates(context.getContextType()
					.getId());
			final boolean multipleLinesSelected = areMultipleLinesSelected(viewer);
			for (int i = 0; i != templates.length; i++) {
				final Template template = templates[i];
				try {
					context.getContextType().validate(template.getPattern());
				} catch (TemplateException e) {
					continue;
				}
				if (!multipleLinesSelected
						&& template.getPattern().indexOf($_WORD_SELECTION) != -1
						|| (multipleLinesSelected && template.getPattern()
								.indexOf($_LINE_SELECTION) != -1)) {
					matches.add((TemplateProposal) createProposal(template,
							context, region, getRelevance(template)));
				}
			}
		}

		Collections.sort(matches, comparator);

		final IInformationControlCreator controlCreator = getInformationControlCreator();
		for (TemplateProposal proposal : matches) {
			proposal.setInformationControlCreator(controlCreator);
		}

		return matches.toArray(new ICompletionProposal[matches.size()]);
	}

	/**
	 * Returns <code>true</code> if one line is completely selected or if
	 * multiple lines are selected. Being completely selected means that all
	 * characters except the new line characters are selected.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @return <code>true</code> if one or multiple lines are selected
	 * @since 2.1
	 */
	private boolean areMultipleLinesSelected(ITextViewer viewer) {
		if (viewer == null)
			return false;
		Point s = viewer.getSelectedRange();
		if (s.y == 0)
			return false;
		try {
			IDocument document = viewer.getDocument();
			int startLine = document.getLineOfOffset(s.x);
			int endLine = document.getLineOfOffset(s.x + s.y);
			IRegion line = document.getLineInformation(startLine);
			return startLine != endLine
					|| (s.x == line.getOffset() && s.y == line.getLength());
		} catch (BadLocationException x) {
			return false;
		}
	}

	protected boolean isValidPrefix(String prefix) {
		return prefix.length() != 0;
	}

	protected boolean isMatchingTemplate(Template template, String prefix,
			TemplateContext context) {
		return template.getName().startsWith(prefix)
				&& template.matches(prefix, context.getContextType().getId());
	}

	@Override
	protected TemplateContext createContext(ITextViewer viewer, IRegion region) {
		TemplateContextType contextType = getContextType(viewer, region);
		if (contextType instanceof ScriptTemplateContextType) {
			IDocument document = viewer.getDocument();

			ISourceModule sourceModule = getContext().getSourceModule();
			if (sourceModule == null) {
				return null;
			}
			return ((ScriptTemplateContextType) contextType).createContext(
					document, region.getOffset(), region.getLength(),
					sourceModule);
		}
		return null;
	}

	@Override
	protected ICompletionProposal createProposal(Template template,
			TemplateContext context, IRegion region, int relevance) {
		return new ScriptTemplateProposal(template, context, region,
				getImage(template), relevance);
	}

	protected IInformationControlCreator getInformationControlCreator() {
		int orientation = Window.getDefaultOrientation();
		IEditorPart editor = getContext().getEditor();
		if (editor == null)
			editor = DLTKUIPlugin.getActivePage().getActiveEditor();
		if (editor instanceof IWorkbenchPartOrientation)
			orientation = ((IWorkbenchPartOrientation) editor).getOrientation();
		return new TemplateInformationControlCreator(orientation);
	}

	protected abstract String getContextTypeId();

	protected abstract ScriptTemplateAccess getTemplateAccess();

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		return getTemplateAccess().getTemplateStore().getTemplates(
				contextTypeId);
	}

	protected char[] getIgnore() {
		return CharOperation.NO_CHAR;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		if (isValidLocation(viewer, region)) {
			return getTemplateAccess().getContextTypeRegistry().getContextType(
					getContextTypeId());
		}
		return null;
	}

	/**
	 * Validates the current location
	 * 
	 * @param viewer
	 * @param region
	 * @return <code>true</code> if the location is valid and could be used to
	 *         display template proposals or <code>false</code> if not
	 */
	protected boolean isValidLocation(ITextViewer viewer, IRegion region) {
		try {
			final String trigger = getTrigger(viewer, region);
			final char[] ignore = getIgnore();
			for (int i = 0; i < ignore.length; i++) {
				if (trigger.indexOf(ignore[i]) != -1) {
					return false;
				}
			}
		} catch (BadLocationException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	@Override
	protected Image getImage(Template template) {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_TEMPLATE);
	}

	protected String getTrigger(ITextViewer viewer, IRegion region)
			throws BadLocationException {
		final IDocument doc = viewer.getDocument();
		final int regionEnd = region.getOffset() + region.getLength();
		final IRegion line = doc.getLineInformationOfOffset(regionEnd);
		final String s = doc
				.get(line.getOffset(), regionEnd - line.getOffset());
		final int spaceIndex = s.lastIndexOf(' ');
		if (spaceIndex != -1) {
			return s.substring(spaceIndex);
		} else {
			return s;
		}
	}

	/**
	 * Returns the relevance of a template. The default implementation returns
	 * zero.
	 * 
	 * @param template
	 *            the template to compute the relevance for
	 * @return the relevance of <code>template</code>
	 */
	protected int getRelevance(Template template) {
		return 0;
	}
}
