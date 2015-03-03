/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.editor;


import melnorme.lang.ide.ui.LangUIPlugin;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class AbstractLangEditorActionContributor extends LangEditorActionContributorHelper {
	
	public static final String SOURCE_MENU_ID = LangUIPlugin.PLUGIN_ID + ".sourceMenu";
	
	public static final String SOURCE_MENU__FORMAT = "format";
	
	public AbstractLangEditorActionContributor() {
		super();
	}
	
	@Override
	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);
		
		IMenuManager sourceMenu = menu.findMenuUsingPath(SOURCE_MENU_ID);
		
		sourceMenu.appendToGroup(SOURCE_MENU__FORMAT, createEditorContribution(
			ITextEditorActionDefinitionIds.SHIFT_LEFT, ITextEditorActionConstants.SHIFT_LEFT));
		sourceMenu.appendToGroup(SOURCE_MENU__FORMAT, createEditorContribution(
			ITextEditorActionDefinitionIds.SHIFT_RIGHT, ITextEditorActionConstants.SHIFT_RIGHT));
		
		
		IMenuManager editMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.appendToGroup(ITextEditorActionConstants.GROUP_ASSIST, createEditorContribution(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, 
				ITextEditorActionConstants.CONTENT_ASSIST));
			
			editMenu.appendToGroup(ITextEditorActionConstants.GROUP_ASSIST, createEditorContribution(
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION, 
				ITextEditorActionConstants.CONTENT_ASSIST_CONTEXT_INFORMATION));
		}
		
		IMenuManager navigateMenu= menu.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			
		}
		
	}
	
}