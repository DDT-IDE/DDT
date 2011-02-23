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
package mmrnmhrm.ui.text;

import java.util.HashSet;
import java.util.Map;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeeUI;
import mmrnmhrm.ui.editor.text.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeDocTextHover;
import mmrnmhrm.ui.editor.text.DeeHyperlinkDetector;
import mmrnmhrm.ui.internal.text.DeeAutoEditStrategy;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlinkDetector;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.typehierarchy.HierarchyInformationControl;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
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
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceViewerConfiguration extends ScriptSourceViewerConfiguration {
	
	protected AbstractScriptScanner fCodeScanner;
	protected AbstractScriptScanner fCommentScanner;
	protected AbstractScriptScanner fDocCommentScanner;
	protected AbstractScriptScanner fStringScanner;
	protected AbstractScriptScanner fRawStringScanner;
	protected AbstractScriptScanner fDelimStringScanner;
	protected AbstractScriptScanner fCharScanner;
	protected HashSet<AbstractScriptScanner> scanners;
	
	public DeeSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return DeePartitions.DEE_PARTITION_TYPES;
	}
	
	@Override
	protected void initializeScanners() {
		scanners = new HashSet<AbstractScriptScanner>();
		
		fCodeScanner = new DeeCodeScanner(getColorManager(), fPreferenceStore);
		scanners.add(fCodeScanner);
		fCommentScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_COMMENT);
		scanners.add(fCommentScanner);
		fDocCommentScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_DOCCOMMENT);
		scanners.add(fDocCommentScanner);
		fStringScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_STRING);
		scanners.add(fStringScanner);
		fRawStringScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_RAW_STRING);
		scanners.add(fRawStringScanner);
		fDelimStringScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_DELIM_STRING);
		scanners.add(fDelimStringScanner);
		fCharScanner = new SingleTokenScriptScanner(getColorManager(), fPreferenceStore, IDeeColorConstants.DEE_CHARACTER_LITERALS);
		scanners.add(fCharScanner);
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		DefaultDamagerRepairer dr;
		
		dr = new DefaultDamagerRepairer(fCodeScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_CODE);
		reconciler.setRepairer(dr, DeePartitions.DEE_CODE);
		
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_SINGLE_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_SINGLE_COMMENT);
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_MULTI_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_MULTI_COMMENT);
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_NESTED_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_NESTED_COMMENT);
		
		dr = new DefaultDamagerRepairer(fDocCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_SINGLE_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_SINGLE_DOCCOMMENT);
		dr = new DefaultDamagerRepairer(fDocCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_MULTI_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_MULTI_DOCCOMMENT);
		dr = new DefaultDamagerRepairer(fDocCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_NESTED_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		dr = new DefaultDamagerRepairer(fStringScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_STRING);
		reconciler.setRepairer(dr, DeePartitions.DEE_STRING);
		dr = new DefaultDamagerRepairer(fRawStringScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_RAW_STRING);
		reconciler.setRepairer(dr, DeePartitions.DEE_RAW_STRING);
		dr = new DefaultDamagerRepairer(fDelimStringScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_DELIM_STRING);
		reconciler.setRepairer(dr, DeePartitions.DEE_DELIM_STRING);
		dr = new DefaultDamagerRepairer(fCharScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_CHARACTER);
		reconciler.setRepairer(dr, DeePartitions.DEE_CHARACTER);
		
		return reconciler;
	}
	
	
	@Override
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		for (AbstractScriptScanner scanner : scanners) {
			if(scanner.affectsBehavior(event))
				return true;
		}
		return false;
	}
	
	@Override
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		for (AbstractScriptScanner scanner : scanners) {
			if (scanner.affectsBehavior(event))
				scanner.adaptToPreferenceChange(event);
		}
	}
	
	
	@Override
	protected String getCommentPrefix() {
		return "//";
	}
	
	
	@Override 
	protected Map<String, ITextEditor> getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		@SuppressWarnings("unchecked")
		Map<String, ITextEditor> targets = super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(DeeHyperlinkDetector.DEE_EDITOR_TARGET, getEditor()); 
		return targets;
	}
	
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] hyperlinkDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int i = 0; i < hyperlinkDetectors.length; i++) {
			if(hyperlinkDetectors[i] instanceof ModelElementHyperlinkDetector) {
				// Remove ModelElementHyperlinkDetector cause it sucks
				// Creating a new array is not necessary I think
				hyperlinkDetectors[i] = null; 
			}
		}
		return hyperlinkDetectors;
	}
	
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return super.getContentAssistant(sourceViewer);
	}
	
	@Override
	protected void alterContentAssistant(ContentAssistant assistant) {
		super.alterContentAssistant(assistant);
		IContentAssistProcessor deeContentAssistProcessor = new DeeCodeContentAssistProcessor(
				assistant, getEditor());
		assistant.setContentAssistProcessor(deeContentAssistProcessor, DeePartitions.DEE_CODE);
		
		// assistant.setStatusLineVisible(true);
	}
	
	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		return DeeContentAssistPreference.getDefault();
	}
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
		return new IAutoEditStrategy[] { new DeeAutoEditStrategy(partitioning, DeeUI.getPrefStore() ) };
	}
	
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// TODO: Note: we are currently using own TextHover, not DLTK's. maybe can change
		return new DeeDocTextHover(getEditor());
	}
	
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		return super.getInformationPresenter(sourceViewer);
	}
	
	@Override
	protected void initializeQuickOutlineContexts(InformationPresenter presenter, IInformationProvider provider) {
		String[] contentTypes = DeePartitions.DEE_PARTITION_TYPES;
		for (int i= 0; i < contentTypes.length; i++)
			presenter.setInformationProvider(provider, contentTypes[i]);
	}
	
	private IInformationControlCreator getHierarchyPresenterControlCreator() {
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
		
		InformationPresenter presenter = new InformationPresenter(
				getHierarchyPresenterControlCreator());
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		@SuppressWarnings("restriction")
		IInformationProvider provider = new org.eclipse.dltk.internal.ui.text.ScriptElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, DeePartitions.DEE_CODE);
		
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	
	
	// XXX: use DTLK default method?
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@Override
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent,
						new org.eclipse.jface.internal.text.html.HTMLTextPresenter(true));
			}
		};
	}
	
	
}
