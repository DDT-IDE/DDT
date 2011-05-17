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

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeTextTools extends ScriptTextTools {
	
	protected static final String[] LEGAL_CONTENT_TYPES = 
		ArrayUtil.remove(DeePartitions.DEE_PARTITION_TYPES, DeePartitions.DEE_CODE);
	
	
	public DeeTextTools(boolean autoDisposeOnDisplayDispose) {
		super(DeePartitions.DEE_PARTITIONING, LEGAL_CONTENT_TYPES, autoDisposeOnDisplayDispose);
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		Assert.isTrue(partitioning.equals(DeePartitions.DEE_PARTITIONING));
		return new DeeSourceViewerConfiguration(getColorManager(), preferenceStore, editor,
				DeePartitions.DEE_PARTITIONING);
	}
	
}
