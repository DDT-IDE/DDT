/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *     xored software, Inc. - indenting tab policy fixes (Alex Panchenko) 
 *******************************************************************************/
package _org.eclipse.dltk.ui.templates;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.ui.templates.IScriptTemplateIndenter;
import org.eclipse.dltk.ui.templates.NopScriptTemplateIndenter;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

public class ScriptTemplateContext extends DocumentTemplateContext {
	/**
	 * <code>true</code> if the context has a managed (i.e. added to the
	 * document) position, <code>false</code> otherwise.
	 */
	protected final boolean fIsManaged;

	protected ScriptTemplateContext(TemplateContextType type, IDocument document, 
			int completionOffset, int completionLength) {
		super(type, document, completionOffset, completionLength);
		fIsManaged = false;
	}

	protected ScriptTemplateContext(TemplateContextType type, IDocument document, Position position) {
		super(type, document, position);
		fIsManaged = true;
	}

	/**
	 * Tests if specified char is tab or space
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isSpaceOrTab(char ch) {
		return ch == ' ' || ch == '\t';
	}

	protected static String calculateIndent(IDocument document, int offset) {
		try {
			final IRegion region = document.getLineInformationOfOffset(offset);
			String indent = document.get(region.getOffset(),
					offset - region.getOffset());
			int i = 0;
			while (i < indent.length() && isSpaceOrTab(indent.charAt(i))) {
				++i;
			}
			if (i > 0) {
				return indent.substring(0, i);
			}
		} catch (BadLocationException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return ""; //$NON-NLS-1$
	}

	@Override
	public TemplateBuffer evaluate(Template template)
			throws BadLocationException, TemplateException {
		if (!canEvaluate(template)) {
			return null;
		}
		final String[] lines = TextUtils.splitLines(template.getPattern());
		if (lines.length > 1) {
			final String delimeter = TextUtilities
					.getDefaultLineDelimiter(getDocument());
			final String indent = calculateIndent(getDocument(), getStart());
			final IScriptTemplateIndenter indenter = getIndenter();
			final StringBuffer buffer = new StringBuffer(lines[0]);

			// Except first line
			for (int i = 1; i < lines.length; i++) {
				buffer.append(delimeter);
				indenter.indentLine(buffer, indent, lines[i]);
			}

			template = new Template(template.getName(),
					template.getDescription(), template.getContextTypeId(),
					buffer.toString(), template.isAutoInsertable());
		}

		return super.evaluate(template);
	}

	/**
	 * @return
	 */
	protected IScriptTemplateIndenter getIndenter() {
		return new NopScriptTemplateIndenter();
	}

	@Override
	public int getStart() {

		if (fIsManaged && getCompletionLength() > 0)
			return super.getStart();

		try {
			IDocument document = getDocument();

			int start = getCompletionOffset();
			int end = getCompletionOffset() + getCompletionLength();

			while (start != 0
					&& Character.isUnicodeIdentifierPart(document
							.getChar(start - 1)))
				start--;

			while (start != end
					&& Character.isWhitespace(document.getChar(start)))
				start++;

			if (start == end)
				start = getCompletionOffset();

			return start;

		} catch (BadLocationException e) {
			return super.getStart();
		}
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.corext.template.DocumentTemplateContext#getEnd()
	 */
	@Override
	public int getEnd() {

		if (fIsManaged || getCompletionLength() == 0)
			return super.getEnd();

		try {
			IDocument document = getDocument();

			int start = getCompletionOffset();
			int end = getCompletionOffset() + getCompletionLength();

			while (start != end
					&& Character.isWhitespace(document.getChar(end - 1)))
				end--;

			return end;

		} catch (BadLocationException e) {
			return super.getEnd();
		}
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.corext.template.DocumentTemplateContext#getKey()
	 */
	@Override
	public String getKey() {

		if (getCompletionLength() == 0)
			return super.getKey();

		try {
			IDocument document = getDocument();

			int start = getStart();
			int end = getCompletionOffset();
			return start <= end ? document.get(start, end - start) : ""; //$NON-NLS-1$

		} catch (BadLocationException e) {
			return super.getKey();
		}
	}
}
