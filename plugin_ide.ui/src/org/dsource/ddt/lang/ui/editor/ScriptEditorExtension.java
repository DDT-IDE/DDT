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


import melnorme.lang.ide.ui.TextSettings_Actual;
import melnorme.lang.ide.ui.editor.EditorUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.IEditorInput;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditorErrorTickUpdater2;
import _org.eclipse.dltk.internal.ui.editor.ScriptEditor_Actions;

public abstract class ScriptEditorExtension extends ScriptEditor_Actions {
	
	@Override
	protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
		if(document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			String partitioning = TextSettings_Actual.PARTITIONING_ID;
			if(extension.getDocumentPartitioner(partitioning) == null) {
				getTextTools().setupDocumentPartitioner(document, partitioning);
			}
		}
	}
	
	/* -----------------  ----------------- */
	
	protected ScriptEditorErrorTickUpdater2 fScriptEditorErrorTickUpdater = new ScriptEditorErrorTickUpdater2(this);
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		
		fScriptEditorErrorTickUpdater.updateEditorImage(EditorUtils.getAssociatedFile(input));
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public void dispose() {
		if (fScriptEditorErrorTickUpdater != null) {
			fScriptEditorErrorTickUpdater.dispose();
			fScriptEditorErrorTickUpdater = null;
		}
		super.dispose();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ICharacterPairMatcher createBracketMatcher() {
		return super.createBracketMatcher();
	}
	
}