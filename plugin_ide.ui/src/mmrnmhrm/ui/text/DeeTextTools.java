/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.lang.ide.ui.TextSettings_Actual;
import melnorme.lang.ide.ui.text.LangDocumentPartitionerSetup;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

@Deprecated
public class DeeTextTools extends ScriptTextTools {
	
	public DeeTextTools(boolean autoDisposeOnDisplayDispose) {
		super(DeePartitions.PARTITIONING_ID, LangDocumentPartitionerSetup.LEGAL_CONTENT_TYPES, 
			autoDisposeOnDisplayDispose);
	}
	
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return TextSettings_Actual.createPartitionScanner();
	}
	
	@Override
	public org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		throw assertFail();
	}
	
}