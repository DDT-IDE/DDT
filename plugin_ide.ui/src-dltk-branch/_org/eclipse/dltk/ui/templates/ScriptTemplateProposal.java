package _org.eclipse.dltk.ui.templates;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class ScriptTemplateProposal extends TemplateProposal implements
		ICompletionProposalExtension4 {

	public ScriptTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}

	private boolean isRelevanceOverriden;
	private int relevanceOverride;

	/*
	 * @see org.eclipse.jface.text.templates.TemplateProposal#getRelevance()
	 */
	@Override
	public int getRelevance() {
		return isRelevanceOverriden ? relevanceOverride : super.getRelevance();
	}

	public void setRelevance(int value) {
		this.relevanceOverride = value;
		this.isRelevanceOverriden = true;
	}

	@Override
	public String getAdditionalProposalInfo() {
		TemplateContext context = getContext();
		if (context instanceof ScriptTemplateContext) {
			ScriptTemplateContext scriptContext = (ScriptTemplateContext) context;

			try {
				getContext().setReadOnly(true);
				TemplateBuffer templateBuffer;
				templateBuffer = scriptContext.evaluate(getTemplate());

				// restore indenting
				IDocument document = scriptContext.getDocument();
				String indenting = ScriptTemplateContext.calculateIndent(
						document, scriptContext.getStart());
				String delimeter = TextUtilities
						.getDefaultLineDelimiter(document);

				String info = templateBuffer.getString();
				return info.replaceAll(delimeter + indenting, delimeter);
			} catch (BadLocationException e) {
			} catch (TemplateException e1) {
			}
		}
		return null;
	}

	public String getTemplateName() {
		return getTemplate().getName();
	}

	public String getPattern() {
		return getTemplate().getPattern();
	}

	@Override
	public boolean isAutoInsertable() {
		if (isSelectionTemplate())
			return false;
		return getTemplate().isAutoInsertable();
	}

	/**
	 * Returns <code>true</code> if the proposal has a selection, e.g. will wrap
	 * some code.
	 * 
	 * @return <code>true</code> if the proposals completion length is non zero
	 */
	private boolean isSelectionTemplate() {
		if (getContext() instanceof DocumentTemplateContext) {
			DocumentTemplateContext ctx = (DocumentTemplateContext) getContext();
			if (ctx.getCompletionLength() > 0)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return getDisplayString();
	}

}
