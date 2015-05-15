/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *     xored software, Inc. - fix tab handling (Bug# 200024) (Alex Panchenko) 
 *******************************************************************************/package _org.eclipse.dltk.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.editor.LangSourceViewer;

import org.eclipse.core.filebuffers.IPersistableAnnotationModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/* FIXME: DLTK: review this class. */
public class AdaptedSourceViewer extends LangSourceViewer implements ICompletionListener {
	
	protected static interface ITextConverter {
		void customizeDocumentCommand(IDocument document, DocumentCommand command);
	}
	
	private List<AdaptedSourceViewer.ITextConverter> fTextConverters;
	
	protected boolean fIgnoreTextConverters = false;
	protected boolean fInCompletionSession;
	
	protected final ScriptEditor editor;
	
	public AdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, IPreferenceStore store, ScriptEditor editor) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
		this.editor = editor;
	}

	@Override
	public void configure(SourceViewerConfiguration configuration) {
		super.configure(configuration);

		final IContentAssistant ca = getContentAssistant();
		if (ca instanceof IContentAssistantExtension2) {
			((IContentAssistantExtension2) ca).addCompletionListener(this);
		}
	}

	@Override
	public void unconfigure() {
		final IContentAssistant ca = getContentAssistant();
		if (ca instanceof IContentAssistantExtension2) {
			((IContentAssistantExtension2) ca)
					.removeCompletionListener(this);
		}

		super.unconfigure();
	}
	
	
	protected void hadnleElementContentReplaced() {
		IAnnotationModel annotationModel = getAnnotationModel();
		if (annotationModel instanceof IPersistableAnnotationModel) {
			IPersistableAnnotationModel persistableAnnotationModel = (IPersistableAnnotationModel) annotationModel;
			try {
				persistableAnnotationModel.reinitialize(getDocument());
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void doOperation(int operation) {

		if (getTextWidget() == null)
			return;

		switch (operation) {
//		case CONTENTASSIST_PROPOSALS:
//			fContentAssistant.showPossibleCompletions();
////			editor.setStatusLineErrorMessage(msg);
//			return;
//		case QUICK_ASSIST:
//			/*
//			 * XXX: We can get rid of this once the SourceViewer has a way
//			 * to update the status line
//			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=133787
//			 */
//			fQuickAssistAssistant.showPossibleQuickAssists();
//			editor.setStatusLineErrorMessage(msg);
//			return;
		case UNDO:
			fIgnoreTextConverters = true;
			super.doOperation(operation);
			fIgnoreTextConverters = false;
			return;
		case REDO:
			fIgnoreTextConverters = true;
			super.doOperation(operation);
			fIgnoreTextConverters = false;
			return;
		}

		super.doOperation(operation);
	}

	public void insertTextConverter(ITextConverter textConverter, int index) {
		throw new UnsupportedOperationException();
	}

	public void addTextConverter(ITextConverter textConverter) {
		if (fTextConverters == null) {
			fTextConverters = new ArrayList<ITextConverter>(1);
			fTextConverters.add(textConverter);
		} else if (!fTextConverters.contains(textConverter))
			fTextConverters.add(textConverter);
	}

	public void removeTextConverter(ITextConverter textConverter) {
		if (fTextConverters != null) {
			fTextConverters.remove(textConverter);
			if (fTextConverters.size() == 0)
				fTextConverters = null;
		}
	}

	/*
	 * @see TextViewer#customizeDocumentCommand(DocumentCommand)
	 */
	@Override
	protected void customizeDocumentCommand(DocumentCommand command) {
		super.customizeDocumentCommand(command);
		if (!fIgnoreTextConverters && fTextConverters != null) {
			for (ITextConverter c : fTextConverters)
				c.customizeDocumentCommand(getDocument(), command);
		}
	}

	@Override
	public boolean requestWidgetToken(IWidgetTokenKeeper requester) {
		if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
			return false;
		return super.requestWidgetToken(requester);
	}

	@Override
	public boolean requestWidgetToken(IWidgetTokenKeeper requester,
			int priority) {
		if (PlatformUI.getWorkbench().getHelpSystem()
				.isContextHelpDisplayed())
			return false;
		return super.requestWidgetToken(requester, priority);
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
		fInCompletionSession = false;
	}

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		fInCompletionSession = true;
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
	}
	
	@Override
	public IFormattingContext createFormattingContext() {
		IFormattingContext context = super.createFormattingContext();
		return context;
	}
	
}