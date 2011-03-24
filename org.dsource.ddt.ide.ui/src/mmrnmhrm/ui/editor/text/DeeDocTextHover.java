/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package mmrnmhrm.ui.editor.text;

import mmrnmhrm.lang.ui.EditorUtil;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;

/**
 * Standard documentation hover for DDoc
 */
public class DeeDocTextHover extends AbstractTextHover {
	
	public static class NodeRegion implements IRegion {
		
		public ASTNeoNode node;
		
		public NodeRegion(ASTNeoNode node) {
			this.node = node;
		}
		
		@Override
		public int getLength() {
			return node.getLength();
		}
		
		@Override
		public int getOffset() {
			return node.getOffset();
		}
	}
	
	public DeeDocTextHover() {
	}
	
	public DeeDocTextHover(ITextEditor textEditor) {
		super();
		Assert.isNotNull(textEditor);
		this.fEditor = textEditor;
	}
	
	private ASTNeoNode getNodeAtOffset(int offset) {
		Module module = EditorUtil.getNeoModuleFromEditor(fEditor == null ? getEditor() : fEditor);
		if(module == null)
			return null;
		
		return ASTNodeFinder.findNeoElement(module, offset, false);
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		ASTNeoNode node = getNodeAtOffset(offset);
		if(node == null)
			return null;
		
		if(!(node instanceof DefSymbol || node instanceof Reference))
			return null;
		
		return new NodeRegion(node); // Hum, perhaps it's not a very good idea to hold to an instance of the node
	}
	
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		ASTNeoNode node;
		if(hoverRegion instanceof NodeRegion) {
			node = ((NodeRegion) hoverRegion).node;
		} else {
			node = getNodeAtOffset(hoverRegion.getOffset());
		}
		
		String info = getDocInfoForNode(node);
		if(info != null)
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		
		return null;
	}
	
	public static String getDocInfoForNode(ASTNeoNode node) {
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getDefUnit();
			return HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
		} else if(node instanceof Reference) {
			DefUnit defUnit = ((Reference) node).findTargetDefUnit();
			if(defUnit != null) {
				return HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
			} else {
				return null;
			}
		}
		return null;
	}
	
}
