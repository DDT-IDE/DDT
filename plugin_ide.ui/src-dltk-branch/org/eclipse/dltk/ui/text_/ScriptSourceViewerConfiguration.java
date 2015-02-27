/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text_;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import melnorme.lang.ide.core.LangNature;
import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.annotations.Nullable;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlinkDetector;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.ScriptCompositeReconcilingStrategy;
import org.eclipse.dltk.internal.ui.text.ScriptElementProvider;
import org.eclipse.dltk.internal.ui.text.ScriptReconciler;
import org.eclipse.dltk.internal.ui.text.hover.EditorTextHoverDescriptor;
import org.eclipse.dltk.internal.ui.text.hover.EditorTextHoverProxy;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.dltk.ui.text.ScriptOutlineInformationControl;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.dltk.ui.text.spelling.SpellCheckDelegate;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jdt.internal.ui.text_.HTMLAnnotationHover;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.HyperlinkDetectorRegistry;
import org.eclipse.ui.texteditor.ITextEditor;

import _org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;

/* FIXME: DLTK review uses of other DLTK internal classes, possibly add them. */
public abstract class ScriptSourceViewerConfiguration extends AbstractLangSourceViewerConfiguration {

	private String fDocumentPartitioning;

	public ScriptSourceViewerConfiguration(IPreferenceStore preferenceStore, IColorManager colorManager, 
			AbstractDecoratedTextEditor editor, String partitioning) {
		super(preferenceStore, colorManager, editor);
		
		fDocumentPartitioning = assertNotNull(partitioning);

		initializeScanners();
	}

	protected void initializeScanners() {
	}
	
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return fDocumentPartitioning;
	}
	
	protected IColorManager getColorManager() {
		return getColorManager2();
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		final ITextEditor editor = getEditor();
		if (editor != null && editor.isEditable()) {
			/* FIXME: DLTK: review ScriptCompositeReconcilingStrategy */
			ScriptCompositeReconcilingStrategy strategy = new ScriptCompositeReconcilingStrategy(
					editor, getConfiguredDocumentPartitioning(sourceViewer),
					createSpellCheckDelegate());
			ScriptReconciler reconciler = new ScriptReconciler(editor, strategy, false);
			reconciler.setIsAllowedToModifyDocument(false);
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setProgressMonitor(new NullProgressMonitor());
			reconciler.setDelay(500);

			return reconciler;
		}
		return null;
	}
	
	protected SpellCheckDelegate createSpellCheckDelegate() {
		return new SpellCheckDelegate();
	}
	
	/* FIXME: DLTK: review ToggleCommentAction */
	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { getCommentPrefix(), "" };
	}

	/**
	 * Returns the comment prefix.
	 * 
	 * <p>
	 * Default implementation returns a <code>#</code>, sub-classes may override
	 * if their language uses a different prefix.
	 * </p>
	 */
	protected abstract String getCommentPrefix();

	/**
	 * Returns the outline presenter control creator. The creator is a factory
	 * creating outline presenter controls for the given source viewer. This
	 * implementation always returns a creator for
	 * <code>ScriptOutlineInformationControl</code> instances.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @param commandId
	 *            the ID of the command that opens this control
	 * @return an information control creator
	 * 
	 */
	/* FIXME: DLTK: review ScriptOutlineInformationControl */
	protected IInformationControlCreator getOutlinePresenterControlCreator(
			ISourceViewer sourceViewer, final String commandId) {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
				return new ScriptOutlineInformationControl(parent, shellStyle,
						treeStyle, commandId, fPreferenceStore);
			}
		};
	}
	
	/* FIXME: DLTK: getOutlinePresenter */
	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
		InformationPresenter presenter;
		if (doCodeResolve) {
			presenter = new InformationPresenter(
					getOutlinePresenterControlCreator(sourceViewer, IScriptEditorActionDefinitionIds.OPEN_STRUCTURE));
		} else {
			presenter = new InformationPresenter(
					getOutlinePresenterControlCreator(sourceViewer, IScriptEditorActionDefinitionIds.SHOW_OUTLINE));
		}
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider = new ScriptElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
		initializeQuickOutlineContexts(presenter, provider);
		for (String contentType : getOutlinePresenterContentTypes(sourceViewer, doCodeResolve)) {
			if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType))
				continue;
			if (presenter.getInformationProvider(contentType) != null)
				continue;
			presenter.setInformationProvider(provider, contentType);
		}

		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	
	protected String[] getOutlinePresenterContentTypes(ISourceViewer sourceViewer, boolean doCodeResolve) {
		return getConfiguredContentTypes(sourceViewer);
	}

	@Deprecated
	protected abstract void initializeQuickOutlineContexts(InformationPresenter presenter, IInformationProvider provider);

	public abstract IInformationPresenter getHierarchyPresenter(ScriptSourceViewer viewer, boolean b);
	
	/* FIXME: DLTK: getHyperlinkDetectors review this code */
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		final IHyperlinkDetector[] inheritedDetectors = super.getHyperlinkDetectors(sourceViewer);

		if (getEditor() == null) {
			return inheritedDetectors;
		}

		int resultLength = 1;
		if (inheritedDetectors != null) {
			resultLength += inheritedDetectors.length;
		}
		final IHyperlinkDetector[] additionalDetectors = getAdditionalRegisteredHyperlinkDetectors(sourceViewer);
		if (additionalDetectors != null) {
			resultLength += additionalDetectors.length;
		}
		final IHyperlinkDetector[] detectors = new IHyperlinkDetector[resultLength];
		int resultIndex = 0;
		if (inheritedDetectors != null) {
			System.arraycopy(inheritedDetectors, 0, detectors, resultIndex, inheritedDetectors.length);
			resultIndex += inheritedDetectors.length;
		}
		detectors[resultIndex++] = new ModelElementHyperlinkDetector(getEditor());
		if (additionalDetectors != null) {
			System.arraycopy(additionalDetectors, 0, detectors, resultIndex, additionalDetectors.length);
			resultIndex += additionalDetectors.length;
		}
		return detectors;
	}

	/**
	 * Returns the additional registered hyperlink detectors which are used to
	 * detect hyperlinks in the given source viewer.
	 * 
	 * @param sourceViewer
	 *            the source viewer to be configured by this configuration
	 * @return an array with hyperlink detectors or <code>null</code> if no
	 *         hyperlink detectors are registered
	 * @since 5.0
	 */
	@Nullable
	private final IHyperlinkDetector[] getAdditionalRegisteredHyperlinkDetectors(ISourceViewer sourceViewer) {
		final Map<String, IAdaptable> targets = getAdditionalHyperlinkDetectorTargets(sourceViewer);
		Assert.isNotNull(targets);
		if (targets.isEmpty()) {
			return null;
		}
		final HyperlinkDetectorRegistry registry = EditorsUI.getHyperlinkDetectorRegistry();
		List<IHyperlinkDetector> result = null;
		for (Map.Entry<String, IAdaptable> target : targets.entrySet()) {
			final IHyperlinkDetector[] detectors = registry.createHyperlinkDetectors(target.getKey(),target.getValue());
			if (detectors != null && detectors.length != 0) {
				if (result == null) {
					result = new ArrayList<IHyperlinkDetector>();
				}
				Collections.addAll(result, detectors);
			}
		}
		return result != null ? result.toArray(new IHyperlinkDetector[result.size()]) : null;
	}

	/**
	 * Similar to {@link #getHyperlinkDetectorTargets(ISourceViewer)}, but these
	 * detectors are always added in the end.
	 * 
	 * @since 5.0
	 */
	protected Map<String, IAdaptable> getAdditionalHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		return new HashMap<String, IAdaptable>();
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
	public ContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (getEditor() != null) {
			ContentAssistant assistant = new ContentAssistant();
			
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			assistant.setRestoreCompletionProposalSize(LangUIPlugin.getDialogSettings("completion_proposal_size"));
			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			
			alterContentAssistant(assistant);
			
			getContentAssistPreference().configure(assistant, fPreferenceStore);
			
			return assistant;
		}
		
		return null;
	}
	
	protected abstract ContentAssistPreference getContentAssistPreference();
	
	protected abstract void alterContentAssistant(ContentAssistant assistant);

	/* FIXME: DLTK: review this code*/
	public String getFontPropertyPreferenceKey() {
		return JFaceResources.TEXT_FONT;
	}

	public void changeContentAssistantConfiguration(ContentAssistant ca, PropertyChangeEvent event) {
		getContentAssistPreference().changeConfiguration(ca, fPreferenceStore, event);
	}

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		return super.getContentFormatter(sourceViewer);
	}

	/* FIXME: DLTK: review this code getIndentPrefixes */
	@Override
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		if (fPreferenceStore == null) {
			return super.getIndentPrefixes(sourceViewer, contentType);
		}
		final TabStyle tabStyle = getTabStyle();
		final int tabWidth = getTabWidth(sourceViewer);
		final int indentWidth = getIndentationSize(sourceViewer);
		if (tabStyle != TabStyle.TAB && indentWidth < tabWidth) {
			return new String[] { AutoEditUtils.getNSpaces(indentWidth), "\t", Util.EMPTY_STRING };
		} else if (tabStyle == TabStyle.TAB) {
			return getIndentPrefixesForTab(tabWidth);
		} else {
			return getIndentPrefixesForSpaces(tabWidth);
		}
	}

	protected TabStyle getTabStyle() {
		if (fPreferenceStore != null) {
			TabStyle tabStyle = TabStyle.forName(fPreferenceStore
					.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR));
			if (tabStyle != null) {
				return tabStyle;
			}
		}
		return TabStyle.TAB;
	}

	@Override
	public int getTabWidth(ISourceViewer sourceViewer) {
		if (fPreferenceStore == null)
			return super.getTabWidth(sourceViewer);
		return fPreferenceStore.getInt(CodeFormatterConstants.FORMATTER_TAB_SIZE);
	}

	protected int getIndentationSize(ISourceViewer sourceViewer) {
		if (fPreferenceStore == null)
			return super.getTabWidth(sourceViewer);
		return fPreferenceStore
				.getInt(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/**
	 * Computes and returns the indent prefixes for space indentation and the
	 * given <code>tabWidth</code>.
	 * 
	 * @param tabWidth
	 *            the display tab width
	 * @return the indent prefixes
	 * @see #getIndentPrefixes(ISourceViewer, String)
	 */
	protected String[] getIndentPrefixesForSpaces(int tabWidth) {
		final String[] indentPrefixes = new String[tabWidth + 2];
		indentPrefixes[0] = AutoEditUtils.getNSpaces(tabWidth);
		for (int i = 0; i < tabWidth; i++) {
			indentPrefixes[i + 1] = AutoEditUtils.getNSpaces(i) + '\t';
		}
		indentPrefixes[tabWidth + 1] = ""; //$NON-NLS-1$
		return indentPrefixes;
	}

	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, false);
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

	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
		return super.getQuickAssistAssistant(sourceViewer);
	}
	
}