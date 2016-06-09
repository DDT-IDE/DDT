/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.text;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;
import melnorme.lang.ide.ui.text.coloring.SingleTokenScanner;
import melnorme.lang.ide.ui.text.coloring.TokenRegistry;
import mmrnmhrm.ui.text.DeeCodeScanner;
import mmrnmhrm.ui.text.DeeColorPreferences;

public abstract class LangCommonSourceViewerConfiguration extends AbstractSimpleLangSourceViewerConfiguration {
	
	public LangCommonSourceViewerConfiguration(IPreferenceStore preferenceStore) {
		super(preferenceStore);
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
			return new SingleTokenScanner(tokenStore, DeeColorPreferences.CHARACTER);
		}
		throw assertFail();
	}
	
}