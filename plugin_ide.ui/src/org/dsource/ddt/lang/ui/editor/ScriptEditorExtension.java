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


import static melnorme.utilbox.core.CoreUtil.areEqual;
import mmrnmhrm.org.eclipse.dltk.ui.actions.ReferencesSearchGroup;

import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

public abstract class ScriptEditorExtension extends ScriptEditor {
	
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
	
	protected ActionGroup fReferencesGroup;
	
	
	@Override
	protected void createActions() {
		super.createActions();
		
		// This will deactivate the keybindings for these actions
		setAction("OpenTypeHierarchy", null);
		setAction("OpenCallHierarchy", null);
		
		fReferencesGroup = new ReferencesSearchGroup(this, this.getLanguageToolkit());
	}
	
	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		
		menu.remove("OpenEditor");
		menu.remove("OpenTypeHierarchy");
		menu.remove("OpenCallHierarchy");
		
		menu.remove(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY); // This is quick hierarchy action
		menu.remove("org.eclipse.dltk.ui.refactoring.menu");
		
		IContributionItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			if (areEqual(item.getId(), ITextEditorActionConstants.GROUP_FIND) && item instanceof IMenuManager) {
				menu.remove(item);
				break;
			}
		}
		
		fReferencesGroup.fillContextMenu(menu);
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
	
}