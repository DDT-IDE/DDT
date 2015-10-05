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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.swt.widgets.Display;

import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;
import melnorme.lang.ide.ui.editor.hover.BestMatchHover;
import melnorme.lang.ide.ui.editor.hover.HoverInformationProvider;
import melnorme.lang.ide.ui.editor.structure.AbstractLangStructureEditor;
import melnorme.lang.ide.ui.text.AbstractLangScanner;
import melnorme.lang.ide.ui.text.AbstractLangSourceViewerConfiguration;
import melnorme.lang.ide.ui.text.coloring.SingleTokenScanner;
import melnorme.lang.ide.ui.text.coloring.TokenRegistry;
import melnorme.lang.ide.ui.text.completion.LangContentAssistProcessor.ContentAssistCategoriesBuilder;
import melnorme.util.swt.jface.text.ColorManager2;
import mmrnmhrm.ui.editor.codeassist.DeeCompletionProposalComputer;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeColorPreferences;

public class DeeSourceViewerConfiguration extends AbstractLangSourceViewerConfiguration {
	
	public DeeSourceViewerConfiguration(ColorManager2 colorManager, IPreferenceStore preferenceStore, 
			AbstractLangStructureEditor editor) {
		super(preferenceStore, colorManager, editor);
	}
	
	@Override
	protected AbstractLangScanner createScannerFor(Display current, LangPartitionTypes partitionType,
			TokenRegistry tokenStore) {
		switch (partitionType) {
		case DEE_CODE:
			return new DeeCodeScanner(tokenStore);
			
		case DEE_SINGLE_COMMENT:
		case DEE_MULTI_COMMENT:
		case DEE_NESTED_COMMENT:
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.COMMENT);
			
		case DEE_SINGLE_DOCCOMMENT:
		case DEE_MULTI_DOCCOMMENT:
		case DEE_NESTED_DOCCOMMENT:
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.DOC_COMMENT);
			
		case DEE_STRING:
		case DEE_RAW_STRING:
		case DEE_RAW_STRING2:
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.STRING);
			
		case DEE_DELIM_STRING:
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.DELIM_STRING);
		case DEE_CHARACTER:
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.CHARACTER_LITERALS);
		}
		throw assertFail();
	}
	
	@Override
	protected String getToggleCommentPrefix() {
		return "//";
	}
	
	// ================ Information provider
	
	@Override
	protected IInformationProvider getInformationProvider(String contentType) {
		if(contentType.equals(LangPartitionTypes.DEE_CODE.getId())) {
			return new HoverInformationProvider(new BestMatchHover(getEditor()));
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