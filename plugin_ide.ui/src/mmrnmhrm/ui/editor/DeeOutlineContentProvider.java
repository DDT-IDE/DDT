/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.editor;

import java.util.ArrayList;

import melnorme.util.swt.jface.ElementContentProvider;
import melnorme.utilbox.tree.IElement;

import org.eclipse.jface.viewers.Viewer;

import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.declarations.AbstractConditionalDeclaration;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.definitions.DeclarationEnum;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;

public class DeeOutlineContentProvider extends ElementContentProvider {
	
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	@Override
	public Object[] getChildren(Object element) {
		if(element instanceof Module || isSignificantDeclarationBlock(element)) {
			ASTNode node = (ASTNode) element;
			return filterElements(node.getChildren());
		} else {
			return ASTNode.NO_ELEMENTS;
		}
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Module || isSignificantDeclarationBlock(element)) {
			IASTNode node = (IASTNode) element;
			return filterElements(node.getChildren()).length > 0;
		} else {
			return false;
		}
	}
	
	public static boolean isSignificantDeclarationBlock(Object element) {
		return 
			element instanceof DeclarationAttrib || 
			element instanceof AbstractConditionalDeclaration; 
	}
	
	public static Object[] filterElements(IElement[] elements) {
		ArrayList<IElement> deeElems = new ArrayList<IElement>();
		for(IElement element : elements) {
			if(element instanceof DefUnit || 
				element instanceof DeclarationImport ||  
				element instanceof DeclarationModule || 
				element instanceof DeclarationAttrib || 
				element instanceof AbstractConditionalDeclaration || 
				element instanceof DeclarationEnum
				) {
				deeElems.add(element);
			}
		}
		return deeElems.toArray();
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		/*if(newInput instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) newInput;
	    	DeeDocumentProvider docProvider = DeeUIPlugin.getDeeDocumentProvider();
	    	root = docProvider.getCompilationUnit(input);
	    } else {
	    	root = null;
	    }*/
	}
	
}
