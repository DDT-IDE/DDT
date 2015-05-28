/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.ui.text;

import melnorme.lang.ide.core.LangNature;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.hover.EditorTextHoverDescriptor;
import org.eclipse.dltk.internal.ui.text.hover.EditorTextHoverProxy;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import _org.eclipse.jdt.internal.ui.text.HTMLAnnotationHover;

/* FIXME: DLTK review uses of other DLTK internal classes, possibly add them. */
public abstract class ScriptSourceViewerConfiguration extends AbstractLangSourceViewerConfiguration {

	public ScriptSourceViewerConfiguration(IPreferenceStore preferenceStore, IColorManager colorManager, 
			AbstractDecoratedTextEditor editor) {
		super(preferenceStore, colorManager, editor);
	}
	
	/* FIXME: DLTK: review text hovers */
	@Override
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		final String natureId = getNatureId();
		
		EditorTextHoverDescriptor[] hoverDescs = 
				DLTKUIPlugin.getDefault().getEditorTextHoverDescriptors(fPreferenceStore, natureId);
		int stateMasks[] = new int[hoverDescs.length];
		int stateMasksLength = 0;
		for (int i = 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled()) {
				int j = 0;
				int stateMask = hoverDescs[i].getStateMask();
				while (j < stateMasksLength) {
					if (stateMasks[j] == stateMask)
						break;
					j++;
				}
				if (j == stateMasksLength)
					stateMasks[stateMasksLength++] = stateMask;
			}
		}
		if (stateMasksLength == hoverDescs.length)
			return stateMasks;

		int[] shortenedStateMasks = new int[stateMasksLength];
		System.arraycopy(stateMasks, 0, shortenedStateMasks, 0, stateMasksLength);
		return shortenedStateMasks;
	}

	@Override
	public final ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		return getTextHover_do(sourceViewer, contentType, stateMask);
	}
	
	@Override
	public final ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return getTextHover_do(sourceViewer, contentType, ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
	}
	
	@SuppressWarnings("unused")
	protected ITextHover getTextHover_do(ISourceViewer sourceViewer, String contentType, int stateMask) {
		final String natureId = getNatureId();
		if (natureId == null) {
			return null;
		}
		EditorTextHoverDescriptor[] hoverDescs = DLTKUIPlugin.getDefault()
				.getEditorTextHoverDescriptors(fPreferenceStore, natureId);
		int i = 0;
		while (i < hoverDescs.length) {
			if (hoverDescs[i].isEnabled() && hoverDescs[i].getStateMask() == stateMask)
				return new EditorTextHoverProxy(hoverDescs[i], getEditor(), fPreferenceStore);
			i++;
		}

		return null;
	}

	private String getNatureId() {
		return LangNature.NATURE_ID;
	}

	/* FIXME: DLTK: review getInformationPresenter */
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		InformationPresenter presenter = new InformationPresenter(
				getInformationPresenterControlCreator(sourceViewer));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setInformationProvider(getInformationProvider(), IDocument.DEFAULT_CONTENT_TYPE);

		presenter.setSizeConstraints(60, 10, true, true);
		return presenter;
	}
	
	protected abstract IInformationProvider getInformationProvider();
	
	/**
	 * Returns the information presenter control creator. The creator is a
	 * factory creating the presenter controls for the given source viewer. This
	 * implementation always returns a creator for
	 * <code>DefaultInformationControl</code> instances.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an information control creator
	 * 
	 */
	protected IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE | SWT.TOOL;
				int style = SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
			}
		};
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new HTMLAnnotationHover(false) {
			@Override
			protected boolean isIncluded(Annotation annotation) {
				return isShowInVerticalRuler(annotation);
			}
		};
	}
	
	@Override
	public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) {
		return new HTMLAnnotationHover(true) {
			@Override
			protected boolean isIncluded(Annotation annotation) {
				return isShowInOverviewRuler(annotation);
			}
		};
	}
	
}