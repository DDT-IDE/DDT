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
package mmrnmhrm.ui.views;

import melnorme.lang.tooling.ast.IASTNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class NodeElementContentProvider implements ITreeContentProvider {
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return ((IASTNode) element).hasChildren();
	}
	
	@Override
	public Object getParent(Object element) {
		return ((IASTNode) element).getLexicalParent();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return ((IASTNode) parentElement).getChildren();
	}
	
}
