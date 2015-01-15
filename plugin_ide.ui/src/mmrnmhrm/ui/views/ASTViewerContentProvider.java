/*******************************************************************************
 * Copyright (c) 2012, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.views;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.utilbox.tree.IElement;
import dtool.parser.DeeParserResult;

public class ASTViewerContentProvider extends NodeElementContentProvider {
	
	protected ASTViewer view;
	
	public ASTViewerContentProvider(ASTViewer view) {
		this.view = view;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		DeeParserResult deeModuleDecl = view.fDeeModule;
		if(deeModuleDecl == null) {
			return IElement.NO_ELEMENTS;
		}
		IASTNode input = deeModuleDecl.getModuleNode(); 
		return input.getChildren();
	}
	
}