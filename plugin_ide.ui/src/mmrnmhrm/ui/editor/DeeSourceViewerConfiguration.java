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

import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;
import melnorme.lang.ide.ui.text.completion.LangContentAssistProcessor.ContentAssistCategoriesBuilder;
import mmrnmhrm.core.text.DeePartitions;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposalComputer;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeColorPreferences;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import _org.eclipse.jdt.internal.ui.text.java.hover.LangInformationProvider;

public class DeeSourceViewerConfiguration extends AbstractLangSourceViewerConfiguration {
	
	public DeeSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, 
			AbstractDecoratedTextEditor editor) {
		super(preferenceStore, colorManager, editor);
	}
	
	@Override
	protected void createScanners() {
		
		addScanner(new DeeCodeScanner(getTokenStoreFactory()), 
				DeePartitions.DEE_CODE);
		
		addScanner(createSingleTokenScanner(DeeColorPreferences.COMMENT.key), 
				DeePartitions.DEE_SINGLE_COMMENT, 
				DeePartitions.DEE_MULTI_COMMENT, 
				DeePartitions.DEE_NESTED_COMMENT);
		
		addScanner(createSingleTokenScanner(DeeColorPreferences.DOCCOMMENT.key), 
				DeePartitions.DEE_SINGLE_DOCCOMMENT, 
				DeePartitions.DEE_MULTI_DOCCOMMENT, 
				DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		addScanner(createSingleTokenScanner(DeeColorPreferences.STRING.key), 
				DeePartitions.DEE_STRING,
				DeePartitions.DEE_RAW_STRING,
				DeePartitions.DEE_RAW_STRING2);
		
		addScanner(createSingleTokenScanner(DeeColorPreferences.DELIM_STRING.key), 
				DeePartitions.DEE_DELIM_STRING);
		
		addScanner(createSingleTokenScanner(DeeColorPreferences.CHARACTER_LITERALS.key),
				DeePartitions.DEE_CHARACTER);
	}
	
	@Override
	protected String getToggleCommentPrefix() {
		return "//";
	}
	
	// ================ Information provider
	
	@Override
	protected IInformationProvider getInformationProvider(String contentType) {
		if(contentType.equals(DeePartitions.DEE_CODE)) {
			return new LangInformationProvider(getEditor());
		}
		return null;
	}
	
	// ================ Content Assist
	
	@Override
	protected ContentAssistCategoriesBuilder getContentAssistCategoriesProvider() {
		return new DeeContentAssistCategoriesBuilder();
	}
	
	public static class DeeContentAssistCategoriesBuilder extends ContentAssistCategoriesBuilder {
		@Override
		protected DeeCompletionProposalComputer createDefaultSymbolsProposalComputer() {
			return new DeeCompletionProposalComputer();
		}
	}
	
}