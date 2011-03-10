/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.lang.ui.editor;


import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;

public abstract class ScriptEditorLangExtension extends ScriptEditor {
	
	@Override
	protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
		if(document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			String partitioning = getPartitioningToConnect();
			if(extension.getDocumentPartitioner(partitioning) == null) {
				getTextTools().setupDocumentPartitioner(document, partitioning);
			}
		}
	}
	
	protected abstract String getPartitioningToConnect();
	
}