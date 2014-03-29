/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;

import melnorme.lang.ide.ui.editor.text.LangAutoEditStrategyExt;
import mmrnmhrm.ui.text.DeePartitions;

public class DeeAutoEditStrategy extends LangAutoEditStrategyExt {
	
	public DeeAutoEditStrategy(IPreferenceStore store, String contentType, ITextViewer viewer) {
		super(store, DeePartitions.DEE_PARTITIONING, contentType, viewer);
	}
	
}