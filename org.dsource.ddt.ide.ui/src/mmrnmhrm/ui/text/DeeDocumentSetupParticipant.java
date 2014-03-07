/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *******************************************************************************/
package mmrnmhrm.ui.text;

import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.IDocument;

public class DeeDocumentSetupParticipant implements IDocumentSetupParticipant {
	
	@Override
	public void setup(IDocument document) {
		ScriptTextTools tools = DeeUIPlugin.getDefault().getTextTools();
		tools.setupDocumentPartitioner(document, DeePartitions.DEE_PARTITIONING);
	}

}
