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

import org.eclipse.core.filebuffers.IPersistableAnnotationModel;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
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

import melnorme.lang.ide.ui.editor.LangSourceViewer;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;

/* TODO: DLTK: review this class. */
public class AdaptedSourceViewer extends LangSourceViewer implements ICompletionListener {
	
	protected static interface ITextConverter {
		void customizeDocumentCommand(IDocument document, DocumentCommand command);
	}
	
	protected boolean fInCompletionSession;
	
	protected final AbstractLangStructureEditor editor;
	
	public AdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles, ScriptEditor editor) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		this.editor = editor;
	}
	
	@Override
	public void doConfigure(SourceViewerConfiguration configuration) {
		super.doConfigure(configuration);
		
		final IContentAssistant ca = getContentAssistant();
		if (ca instanceof IContentAssistantExtension2) {
			((IContentAssistantExtension2) ca).addCompletionListener(this);
		}
	}

	@Override
	public void unconfigure() {
		final IContentAssistant ca = getContentAssistant();
		if (ca instanceof IContentAssistantExtension2) {
			((IContentAssistantExtension2) ca).removeCompletionListener(this);
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
	public boolean requestWidgetToken(IWidgetTokenKeeper requester) {
		if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
			return false;
		return super.requestWidgetToken(requester);
	}

	@Override
	public boolean requestWidgetToken(IWidgetTokenKeeper requester,
			int priority) {
		if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
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
	
	/* ----------------- setVisibleDocument optimization ----------------- */
	
	/**
	 * Whether to delay setting the visual document until the projection has been computed.
	 * <p>
	 * Added for performance optimization.
	 * </p>
	 * @see #prepareDelayedProjection()
	 * @since 3.1
	 */
	private boolean fIsSetVisibleDocumentDelayed= false;

	/**
	 * Delays setting the visual document until after the projection has been computed.
	 * This method must only be called before the document is set on the viewer.
	 * <p>
	 * This is a performance optimization to reduce the computation of
	 * the text presentation triggered by <code>setVisibleDocument(IDocument)</code>.
	 * </p>
	 *
	 * @see #setVisibleDocument(IDocument)
	 * @since 3.1
	 */
	public void prepareDelayedProjection() {
		Assert.isTrue(!fIsSetVisibleDocumentDelayed);
		fIsSetVisibleDocumentDelayed= true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This is a performance optimization to reduce the computation of
	 * the text presentation triggered by {@link #setVisibleDocument(IDocument)}
	 * </p>
	 * @see #prepareDelayedProjection()
	 * @since 3.1
	 */
	@Override
	protected void setVisibleDocument(IDocument document) {
		if (fIsSetVisibleDocumentDelayed) {
			fIsSetVisibleDocumentDelayed= false;
			IDocument previous= getVisibleDocument();
			enableProjection(); // will set the visible document if anything is folded
			IDocument current= getVisibleDocument();
			// if the visible document was not replaced, continue as usual
			if (current != null && current != previous)
				return;
		}

		super.setVisibleDocument(document);
	}
	
}