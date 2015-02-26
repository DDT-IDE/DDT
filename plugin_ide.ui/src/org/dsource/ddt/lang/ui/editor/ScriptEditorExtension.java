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


import melnorme.lang.ide.ui.editor.EditorUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.part.IShowInTargetList;

import _org.eclipse.dltk.internal.ui.editor.ScriptEditorErrorTickUpdater2;
import _org.eclipse.dltk.internal.ui.editor.ScriptEditor_Actions;

public abstract class ScriptEditorExtension extends ScriptEditor_Actions {
	
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
	
	@Override
	protected void createActions() {
		super.createActions();
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
	}
	
	// Prevent showing Script Explorer in "Show In"
	@Override
	public Object getAdapter(Class required) {
		if (required == IShowInTargetList.class) {
			return new IShowInTargetList() {
				@Override
				public String[] getShowInTargetIds() {
					return new String[] { IPageLayout.ID_OUTLINE };
				}
			};
		}
		return super.getAdapter(required);
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