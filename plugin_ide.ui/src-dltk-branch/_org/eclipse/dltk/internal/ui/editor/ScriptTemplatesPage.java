/*******************************************************************************
 * Copyright (c) 2007, 2009 Dakshinamurthy Karra, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dakshinamurthy Karra (Jalian Systems) - Templates View - https://bugs.eclipse.org/bugs/show_bug.cgi?id=69581
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import melnorme.lang.ide.ui.templates.LangTemplateContextType;
import melnorme.lang.ide.ui.templates.LangTemplatePreferencePage.LangEditTemplateDialog;
import melnorme.lang.ide.ui.templates.TemplateRegistry;
import mmrnmhrm.ui.DeeUILanguageToolkit;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.dltk.ui.text.templates.TemplateVariableProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.templates.AbstractTemplatesPage;

import _org.eclipse.dltk.internal.ui.preferences.ScriptSourcePreviewerUpdater;

/**
 * The templates page for the Script editor.
 * 
 * @since 3.0
 */
/* FIXME: DLTK: review this class*/
public class ScriptTemplatesPage extends AbstractTemplatesPage {

	private final TemplateVariableProcessor fTemplateProcessor;
	private final ScriptEditor fScriptEditor;
	private final TemplateRegistry fTemplateAccess;

	/**
	 * Create a new AbstractTemplatesPage for the JavaEditor
	 * 
	 * @param scriptEditor
	 *            the java editor
	 */
	public ScriptTemplatesPage(ScriptEditor scriptEditor, TemplateRegistry templateAccess) {
		super(scriptEditor, scriptEditor.getSourceViewer_());
		fScriptEditor = scriptEditor;
		fTemplateProcessor = new TemplateVariableProcessor();
		fTemplateAccess = templateAccess;
	}

	@Override
	protected void insertTemplate(Template template, IDocument document) {
		if (!fScriptEditor.validateEditorInputState())
			return;

		ISourceViewer contextViewer = fScriptEditor.getSourceViewer_();
		ITextSelection textSelection = (ITextSelection) contextViewer
				.getSelectionProvider().getSelection();
		if (!isValidTemplate(document, template, textSelection.getOffset(),
				textSelection.getLength()))
			return;
		beginCompoundChange(contextViewer);
		/*
		 * The Editor checks whether a completion for a word exists before it
		 * allows for the template to be applied. We pickup the current text at
		 * the selection position and replace it with the first char of the
		 * template name for this to succeed. Another advantage by this method
		 * is that the template replaces the selected text provided the
		 * selection by itself is not used in the template pattern.
		 */
		String savedText;
		try {
			savedText = document.get(textSelection.getOffset(),
					textSelection.getLength());
			if (savedText.length() == 0) {
				String prefix = getIdentifierPart(document, template,
						textSelection.getOffset(), textSelection.getLength());
				if (prefix.length() > 0
						&& !template.getName().startsWith(prefix.toString())) {
					return;
				}
				if (prefix.length() > 0) {
					contextViewer.setSelectedRange(textSelection.getOffset()
							- prefix.length(), prefix.length());
					textSelection = (ITextSelection) contextViewer
							.getSelectionProvider().getSelection();
				}
			}
			document.replace(textSelection.getOffset(),
					textSelection.getLength(),
					template.getName().substring(0, 1));
		} catch (BadLocationException e) {
			endCompoundChange(contextViewer);
			return;
		}
		Position position = new Position(textSelection.getOffset() + 1, 0);
		Region region = new Region(textSelection.getOffset() + 1, 0);
		contextViewer.getSelectionProvider().setSelection(new TextSelection(textSelection.getOffset(), 1));

		TemplateContextType type = getContextTypeRegistry().getContextType(template.getContextTypeId());
		
		DocumentTemplateContext context = ((LangTemplateContextType) type).createContext(document, position, null);
		context.setVariable("selection", savedText); //$NON-NLS-1$
		if (context.getKey().length() == 0) {
			try {
				document.replace(textSelection.getOffset(), 1, savedText);
			} catch (BadLocationException e) {
				endCompoundChange(contextViewer);
				return;
			}
		}
		TemplateProposal proposal = new TemplateProposal(template, context,
				region, null);
		fScriptEditor.getSite().getPage().activate(fScriptEditor);
		proposal.apply(fScriptEditor.getSourceViewer_(), ' ', 0, region.getOffset());
		endCompoundChange(contextViewer);
	}

	@Override
	protected ContextTypeRegistry getContextTypeRegistry() {
		return fTemplateAccess.getContextTypeRegistry();
	}

	@Override
	protected IPreferenceStore getTemplatePreferenceStore() {
		return fTemplateAccess.getPreferenceStore();
	}

	@Override
	public TemplateStore getTemplateStore() {
		return fTemplateAccess.getTemplateStore();
	}

	@Override
	protected boolean isValidTemplate(IDocument document, Template template,
			int offset, int length) {
		String[] contextIds = getContextTypeIds(document, offset);
		for (int i = 0; i < contextIds.length; i++) {
			if (contextIds[i].equals(template.getContextTypeId())) {
				DocumentTemplateContext context = getContext(document,
						template, offset, length);
				return context.canEvaluate(template)
						|| isTemplateAllowed(context, template);
			}
		}
		return false;
	}

	@Override
	protected SourceViewer createPatternViewer(Composite parent) {
		IDocument document = new Document();
		DeeTextTools tools = DeeUIPlugin.getDefault().getTextTools();
		tools.setupDocumentPartitioner(document);
		IPreferenceStore store = uiToolkit().getCombinedPreferenceStore();
		ScriptSourceViewer viewer = new ScriptSourceViewer(parent, null, null,
				false, SWT.V_SCROLL | SWT.H_SCROLL, store);

		DeeSourceViewerConfiguration configuration = uiToolkit().createSourceViewerConfiguration2();
		viewer.configure(configuration);
		viewer.setEditable(false);
		viewer.setDocument(document);

		Font font = JFaceResources.getFont(fScriptEditor.getSymbolicFontName());
		viewer.getTextWidget().setFont(font);
		new ScriptSourcePreviewerUpdater(viewer, configuration, store);

		Control control = viewer.getControl();
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL);
		control.setLayoutData(data);

		viewer.setEditable(false);
		return viewer;
	}

	@Override
	protected Image getImage(Template template) {
		return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_TEMPLATE);
	}

	@Override
	protected Template editTemplate(Template template, boolean edit,
			boolean isNameModifiable) {
		LangEditTemplateDialog dialog = new LangEditTemplateDialog(
				getSite().getShell(), template, edit, isNameModifiable, 
				getContextTypeRegistry());
		if (dialog.open() == Window.OK)
			return dialog.getTemplate();
		return null;
	}

	protected DeeUILanguageToolkit uiToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}

	@Override
	protected void updatePatternViewer(Template template) {
		if (template == null) {
			getPatternViewer().getDocument().set(""); //$NON-NLS-1$
			return;
		}
		String contextId = template.getContextTypeId();
		TemplateContextType type = getContextTypeRegistry().getContextType(
				contextId);
		fTemplateProcessor.setContextType(type);

		IDocument doc = getPatternViewer().getDocument();

		String start = null;
		if ("javadoc".equals(contextId)) { //$NON-NLS-1$
			start = "/**" + doc.getLegalLineDelimiters()[0]; //$NON-NLS-1$
		} else
			start = ""; //$NON-NLS-1$

		doc.set(start + template.getPattern());
		int startLen = start.length();
		getPatternViewer().setDocument(doc, startLen,
				doc.getLength() - startLen);
	}

	@Override
	protected String getPreferencePageId() {
		return uiToolkit().getEditorTemplatesPreferencePageId();
	}

	/**
	 * Undomanager - end compound change
	 * 
	 * @param viewer
	 *            the viewer
	 */
	protected void endCompoundChange(ISourceViewer viewer) {
		if (viewer instanceof ITextViewerExtension)
			((ITextViewerExtension) viewer).getRewriteTarget()
					.endCompoundChange();
	}

	/**
	 * Undomanager - begin a compound change
	 * 
	 * @param viewer
	 *            the viewer
	 */
	protected void beginCompoundChange(ISourceViewer viewer) {
		if (viewer instanceof ITextViewerExtension)
			((ITextViewerExtension) viewer).getRewriteTarget()
					.beginCompoundChange();
	}

	/**
	 * Check whether the template is allowed even though the context can't
	 * evaluate it. This is needed because the Dropping of a template is more
	 * lenient than Ctrl-space invoked code assist.
	 * 
	 * @param context
	 *            the template context
	 * @param template
	 *            the template
	 * @return true if the template is allowed
	 */
	private boolean isTemplateAllowed(DocumentTemplateContext context,
			Template template) {
		int offset;
		try {
			// if (template.getContextTypeId().equals(JavaDocContextType.ID)) {
			// return (offset = context.getCompletionOffset()) > 0
			// && Character.isWhitespace(context.getDocument()
			// .getChar(offset - 1));
			// } else {
			return ((offset = context.getCompletionOffset()) > 0 && !isTemplateNamePart(context
					.getDocument().getChar(offset - 1)));
			// }
		} catch (BadLocationException e) {
		}
		return false;
	}

	/**
	 * Checks whether the character is a valid character in Java template names
	 * 
	 * @param ch
	 *            the character
	 * @return <code>true</code> if the character is part of a template name
	 */
	protected boolean isTemplateNamePart(char ch) {
		return !Character.isWhitespace(ch) && ch != '(' && ch != ')'
				&& ch != '{' && ch != '}' && ch != ';';
	}

	protected DocumentTemplateContext getContext(IDocument document, Template template, final int offset, int length) {
		final ScriptTemplateContextType contextType = (ScriptTemplateContextType) getContextTypeRegistry()
				.getContextType(template.getContextTypeId());
		return contextType.createContext(document, offset, length, null);
	}

	/**
	 * Get the active contexts for the given position in the document.
	 * <p>
	 * FIXME: (DLTK) should trigger code assist to get the context.
	 * </p>
	 * 
	 * @param document
	 *            the document
	 * @param offset
	 *            the offset
	 * @return an array of valid context id
	 */
	@Override
	protected String[] getContextTypeIds(IDocument document, int offset) {
		final Set<String> ids = new HashSet<String>();
		
		final Iterator<TemplateContextType> i = getContextTypeRegistry().contextTypes();
		while (i.hasNext()) {
			ids.add(i.next().getId());
		}
		return ids.toArray(new String[ids.size()]);
		// try {
		// String partition = TextUtilities.getContentType(document,
		// IJavaPartitions.JAVA_PARTITIONING, offset, true);
		// String[] ids = new String[] { JavaContextType.ID_ALL,
		// JavaContextType.ID_MEMBERS, JavaContextType.ID_STATEMENTS,
		// SWTContextType.ID_ALL, SWTContextType.ID_STATEMENTS,
		// SWTContextType.ID_MEMBERS };
		// if (partition.equals(IJavaPartitions.JAVA_DOC))
		// ids = new String[] { JavaDocContextType.ID };
		// return ids;
		// } catch (BadLocationException e) {
		// return new String[0];
		// }
	}

	/**
	 * Get the Java identifier terminated at the given offset
	 * 
	 * @param document
	 *            the document
	 * @param template
	 *            the template
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the identifier part the Java identifier
	 */
	private String getIdentifierPart(IDocument document, Template template,
			int offset, int length) {
		return getContext(document, template, offset, length).getKey();
	}
}
