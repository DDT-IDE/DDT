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

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.lang.ide.ui.LangUIPlugin_Actual;
import melnorme.utilbox.core.Assert;
import mmrnmhrm.ui.editor.DeeSourceViewerConfiguration;

import org.eclipse.cdt.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeTextTools extends ScriptTextTools {
	
	public DeeTextTools(boolean autoDisposeOnDisplayDispose) {
		super(LangUIPlugin_Actual.LANG_PARTITIONING, LangUIPlugin_Actual.LEGAL_CONTENT_TYPES, 
			autoDisposeOnDisplayDispose);
	}
	
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return LangUIPlugin_Actual.createPartitionScanner();
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(IPreferenceStore preferenceStore, 
			ITextEditor editor, String partitioning) {
		Assert.isTrue(partitioning.equals(DeePartitions.PARTITIONING_ID));
		IColorManager colorManager = LangUIPlugin.getInstance().getColorManager();
		return new DeeSourceViewerConfiguration(colorManager, preferenceStore, editor);
	}
	
}