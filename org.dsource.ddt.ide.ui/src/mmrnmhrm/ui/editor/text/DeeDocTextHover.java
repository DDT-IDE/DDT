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
 *  TODO Learn more about DefaultTextHover
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
	
	public DeeDocTextHover(ITextEditor textEditor) {
		super();
		Assert.isNotNull(textEditor);
		this.fEditor = textEditor;
	}

	private ASTNeoNode getNodeAtOffset(int offset) {
		Module module = EditorUtil.getNeoModuleFromEditor(fEditor);
		if(module == null)
			return null;

		ASTNeoNode node;
		node = ASTNodeFinder.findNeoElement(module, offset, false);
		return node;
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		ASTNeoNode node = getNodeAtOffset(offset);
		if(node == null)
			return null;
		
		if(!(node instanceof DefSymbol || node instanceof Reference))
			return null;
		
		return new NodeRegion(node);
	}


	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if(!(hoverRegion instanceof NodeRegion))
			return null;
		ASTNeoNode node = ((NodeRegion) hoverRegion).node;
		
		String info = null;
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getDefUnit();
			info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
		} else if (node instanceof Reference) {
			DefUnit defUnit;
			try {
				defUnit = ((Reference) node).findTargetDefUnit();
				if(defUnit != null)
					info= HoverUtil.getDefUnitHoverInfoWithDeeDoc(defUnit);
				else
					info= "404 DefUnit not found";
			} catch (UnsupportedOperationException uoe) {
				info= "UnsupportedOperationException:\n" + uoe;
			}
		}
		if(info != null)
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		
		return null;
	}


}
