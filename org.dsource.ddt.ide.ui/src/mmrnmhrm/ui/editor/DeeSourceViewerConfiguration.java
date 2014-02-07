/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Map;

import melnorme.lang.ide.ui.editors.BestMatchHover;
import melnorme.utilbox.core.CoreUtil;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.codeassist.DeeCodeCompletionProcessor;
import mmrnmhrm.ui.editor.codeassist.DeeContentAssistPreference;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;
import mmrnmhrm.ui.editor.text.DeeAutoEditStrategy;
import mmrnmhrm.ui.editor.text.DeeHyperlinkDetector;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.dsource.ddt.lang.ui.editor.ScriptSourceViewerConfigurationExtension;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlinkDetector;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.text.hover.ScriptInformationProvider_Mod;
import org.eclipse.dltk.internal.ui.typehierarchy.HierarchyInformationControl;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.dltk.ui.text.hover.IScriptEditorTextHover;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceViewerConfiguration extends ScriptSourceViewerConfigurationExtension {
	
	public DeeSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return DeePartitions.DEE_PARTITION_TYPES;
	}
	
	@Override
	protected void createScanners() {
		
		addScanner(new DeeCodeScanner(getColorManager(), fPreferenceStore), 
				DeePartitions.DEE_CODE);
		
		addScanner(createSingleTokenScriptScanner(IDeeColorConstants.DEE_COMMENT), 
				DeePartitions.DEE_SINGLE_COMMENT, 
				DeePartitions.DEE_MULTI_COMMENT, 
				DeePartitions.DEE_NESTED_COMMENT);
		
		addScanner(createSingleTokenScriptScanner(IDeeColorConstants.DEE_DOCCOMMENT), 
				DeePartitions.DEE_SINGLE_DOCCOMMENT, 
				DeePartitions.DEE_MULTI_DOCCOMMENT, 
				DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		addScanner(createSingleTokenScriptScanner(IDeeColorConstants.DEE_STRING), 
				DeePartitions.DEE_STRING,
				DeePartitions.DEE_RAW_STRING,
				DeePartitions.DEE_RAW_STRING2);
		
		addScanner(createSingleTokenScriptScanner(IDeeColorConstants.DEE_DELIM_STRING), 
				DeePartitions.DEE_DELIM_STRING);
		
		addScanner(createSingleTokenScriptScanner(IDeeColorConstants.DEE_CHARACTER_LITERALS),
				DeePartitions.DEE_CHARACTER);
	}
	
	@Override
	protected String getCommentPrefix() {
		return "//";
	}
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
		assertTrue(DeePartitions.DEE_PARTITIONING.equals(partitioning));
		
		if (DeePartitions.DEE_CHARACTER.equals(contentType)) {
			return new IAutoEditStrategy[] { };
		} else if (DeePartitions.$Methods.isString(contentType)) {
			return new IAutoEditStrategy[] { };
		} else {
			return new IAutoEditStrategy[] { new DeeAutoEditStrategy(DeePlugin.getPrefStore(), contentType, sourceViewer) };
		}
	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		if(contentType.equals(DeePartitions.DEE_CODE)) {
			return new BestMatchHover(getEditor(), stateMask);
		} 
		return null;
	}
	
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		InformationPresenter informationPresenter = (InformationPresenter) super.getInformationPresenter(sourceViewer);
		ScriptInformationProvider_Mod sip = new ScriptInformationProvider_Mod(getEditor()) { 
			@Override
			protected IScriptEditorTextHover createImplementation() {
				return new DeeDocTextHover();
			}
		};
		informationPresenter.setInformationProvider(sip, IDocument.DEFAULT_CONTENT_TYPE);
		return informationPresenter;
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
	protected void initializeQuickOutlineContexts(InformationPresenter presenter, IInformationProvider provider) {
		String[] contentTypes = DeePartitions.DEE_PARTITION_TYPES;
		for (int i= 0; i < contentTypes.length; i++) {
			presenter.setInformationProvider(provider, contentTypes[i]);
		}
	}
	
	// ================ Content Assist
	
	@Override
	protected void alterContentAssistant(ContentAssistant assistant) {
		IContentAssistProcessor deeCodeCompletionProcessor = new DeeCodeCompletionProcessor(
				getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(deeCodeCompletionProcessor, IDocument.DEFAULT_CONTENT_TYPE);
	}
	
	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		return DeeContentAssistPreference.getDefault();
	}
	
	// ================
	
	@Override 
	protected Map<String, ITextEditor> getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map<String, ITextEditor> targets = CoreUtil.downCast(super.getHyperlinkDetectorTargets(sourceViewer));
		targets.put(DeeHyperlinkDetector.DEE_EDITOR_TARGET, getEditor()); 
		return targets;
	}
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] hyperlinkDetectors = super.getHyperlinkDetectors(sourceViewer);
		if(hyperlinkDetectors != null) {
			for (int i = 0; i < hyperlinkDetectors.length; i++) {
				if(hyperlinkDetectors[i] instanceof ModelElementHyperlinkDetector) {
					// Remove ModelElementHyperlinkDetector cause it sucks
					// Creating a new array is not necessary I think
					hyperlinkDetectors[i] = null; 
				}
			}
		}
		return hyperlinkDetectors;
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return super.getContentAssistant(sourceViewer);
	}
	
	// ================
	
	protected IInformationControlCreator getHierarchyPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
				return new HierarchyInformationControl(parent, shellStyle, treeStyle) {
					@Override
					protected IPreferenceStore getPreferenceStore() {
						return DeePlugin.getDefault().getPreferenceStore();
					}
				};
			}
		};
	}
	
	
	@Override
	public IInformationPresenter getHierarchyPresenter(ScriptSourceViewer sourceViewer, boolean doCodeResolve) {
		// Do not create hierarchy presenter if there's no Compilation Unit.
		if (getEditor() != null
				&& getEditor().getEditorInput() != null
				&& EditorUtility.getEditorInputModelElement(getEditor(), true) == null)
			return null;
		
		InformationPresenter presenter = new InformationPresenter(getHierarchyPresenterControlCreator());
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		@SuppressWarnings("restriction")
		IInformationProvider provider = new org.eclipse.dltk.internal.ui.text.ScriptElementProvider(getEditor(), doCodeResolve);
//		IInformationProvider provider = new ScriptElementProvider(getEditor(), doCodeResolve);
		
		presenter.setInformationProvider(provider, DeePartitions.DEE_CODE);
		
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	
}
