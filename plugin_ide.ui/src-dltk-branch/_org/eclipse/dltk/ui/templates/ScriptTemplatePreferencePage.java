/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.templates;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.templates.CodeTemplateSourceViewerConfigurationAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import _org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;

public abstract class ScriptTemplatePreferencePage extends
		TemplatePreferencePage implements IWorkbenchPreferencePage {
	protected class ScriptEditTemplateDialog extends EditTemplateDialog {
		public ScriptEditTemplateDialog(Shell parent, Template template,
				boolean edit, boolean isNameModifiable,
				ContextTypeRegistry registry) {
			super(parent, template, edit, isNameModifiable, registry);
		}

		// protected SourceViewer createViewer(Composite parent) {
		// return ScriptTemplatePreferencePage.this.createViewer(parent);
		// }

		/**
		 * Creates the viewer to be used to display the pattern. Subclasses may
		 * override.
		 * 
		 * @param parent
		 *            the parent composite of the viewer
		 * @return a configured <code>SourceViewer</code>
		 */
		@Override
		protected SourceViewer createViewer(Composite parent) {
			IPreferenceStore store = getPreferenceStore();
			SourceViewer viewer = new ScriptSourceViewer(parent, null, null,
					false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, store);
			SourceViewerConfiguration origConfig = createSourceViewerConfiguration();
			SourceViewerConfiguration configuration = new CodeTemplateSourceViewerConfigurationAdapter(
					origConfig, getTemplateProcessor());
			IDocument document = new Document();
			setDocumentPartitioner(document);

			viewer.configure(configuration);
			if (origConfig instanceof ScriptSourceViewerConfiguration) {
				final String fontKey = ((ScriptSourceViewerConfiguration) origConfig)
						.getFontPropertyPreferenceKey();
				viewer.getTextWidget().setFont(JFaceResources.getFont(fontKey));
			}
			viewer.setDocument(document);
			return viewer;
		}
	}

	public ScriptTemplatePreferencePage() {
		setPreferenceStore();

		ScriptTemplateAccess tplAccess = getTemplateAccess();
		setTemplateStore(tplAccess.getTemplateStore());
		setContextTypeRegistry(tplAccess.getContextTypeRegistry());
	}

	@Override
	protected Template editTemplate(Template template, boolean edit,
			boolean isNameModifiable) {
		EditTemplateDialog dialog = new ScriptEditTemplateDialog(getShell(),
				template, edit, isNameModifiable, getContextTypeRegistry());
		if (dialog.open() == Window.OK) {
			return dialog.getTemplate();
		}
		return null;
	}

	@Override
	protected SourceViewer createViewer(Composite parent) {
		IPreferenceStore store = getPreferenceStore();
		ScriptSourceViewerConfiguration configuration = createSourceViewerConfiguration();

		IDocument document = new Document();
		setDocumentPartitioner(document);

		SourceViewer viewer = new ScriptSourceViewer(parent, null, null, false,
				SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, store);

		viewer.configure(configuration);
		viewer.setEditable(false);
		viewer.setDocument(document);

		Control control = viewer.getControl();
		control.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_VERTICAL));

		// Font font = JFaceResources
		// .getFont(TPreferenceConstants.EDITOR_TEXT_FONT);
		// viewer.getTextWidget().setFont(font);

		// new ScriptSourcePreviewerUpdater(viewer, configuration, store);

		return viewer;
	}

	@Override
	protected void updateViewerInput() {
		IStructuredSelection selection = (IStructuredSelection) getTableViewer()
				.getSelection();
		SourceViewer viewer = getViewer();

		if (selection.size() == 1
				&& selection.getFirstElement() instanceof TemplatePersistenceData) {
			final TemplatePersistenceData data = (TemplatePersistenceData) selection
					.getFirstElement();
			final Template template = data.getTemplate();
			final IViewerInputUpdater updater = getViewerInputUpdater(viewer,
					template);
			if (updater != null) {
				updater.updateInput(viewer, template);
			} else {
				final IDocument doc = viewer.getDocument();
				doc.set(template.getPattern());
				viewer.setDocument(doc, 0, doc.getLength());
			}
		} else {
			viewer.getDocument().set(Util.EMPTY_STRING);
		}
	}

	/**
	 * {@link SourceViewer} content updater by the specified {@link Template}
	 * content
	 */
	protected static interface IViewerInputUpdater {

		/**
		 * Updates the <code>viewer</code> content
		 * 
		 * @param viewer
		 * @param template
		 */
		void updateInput(SourceViewer viewer, Template template);

	}

	/**
	 * {@link IViewerInputUpdater} implementation adding hidden prefix and
	 * suffix to the template content. Can be used to "javadoc" style comments,
	 * so template content is colored like inside "javadoc", but prefix and
	 * suffix strings are invisible.
	 */
	protected static class ViewerInputDecorations implements
			IViewerInputUpdater {
		private final String prefix;
		private final String suffix;

		public ViewerInputDecorations(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}

		@Override
		public void updateInput(SourceViewer viewer, Template template) {
			StringBuilder sb = new StringBuilder();
			final int offset;
			if (prefix != null) {
				sb.append(prefix);
				offset = prefix.length();
			} else {
				offset = 0;
			}
			sb.append(template.getPattern());
			final int endOffset;
			if (suffix != null) {
				sb.append(suffix);
				endOffset = suffix.length();
			} else {
				endOffset = 0;
			}
			final IDocument doc = viewer.getDocument();
			doc.set(sb.toString());
			viewer.setDocument(doc, offset, doc.getLength() - offset
					- endOffset);
		}

	}

	/**
	 * Returns the {@link IViewerInputUpdater} for the specified template or
	 * <code>null</code> if template content should be used directly without any
	 * modifications.
	 * 
	 * @param viewer
	 * @param template
	 * @return
	 */
	protected IViewerInputUpdater getViewerInputUpdater(SourceViewer viewer,
			Template template) {
		return null;
	}

	@Override
	protected boolean isShowFormatterSetting() {
		return false;
	}

	protected abstract ScriptSourceViewerConfiguration createSourceViewerConfiguration();

	protected abstract ScriptTemplateAccess getTemplateAccess();

	protected abstract void setDocumentPartitioner(IDocument document);

	protected abstract void setPreferenceStore();
}
