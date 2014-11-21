package mmrnmhrm.ui.views;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast_actual.ASTNode;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;

public class ASTViewerLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

	
	protected final Color cNoSourceRangeColor;
	protected final Color cDefUnitColor;
	protected final Color cEntityColor;
	private ASTViewer viewer;
	
	public ASTViewerLabelProvider(ASTViewer viewer) {
		this.viewer = viewer;
		cNoSourceRangeColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);

		cDefUnitColor = Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA);
		cEntityColor = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	}
	
	@Override
	public Image getImage(Object element) {
		return DeeElementImageProvider.getElementImage((IASTNode) element);
	}

	@Override
	public String getText(Object elem) {
		ASTNode astNode = (ASTNode) elem;
		
		String str = astNode.toStringClassName();
		str += " ["+ astNode.getStartPos() +"+"+ astNode.getLength() +"]";
		return str;
	}
	
	@Override
	public Color getBackground(Object element) {
		ASTNode node = (ASTNode) element;
		if(!node.hasSourceRangeInfo())
			return cNoSourceRangeColor;
		
		//int offset = EditorUtil.getSelection(viewer.fEditor).getOffset();
		//ASTNode selNode = ASTNodeFinder.findElement(viewer.fCUnit.getModule(), offset);
		
		if(viewer.selNode == node)
			return Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		
		return null;
	}
	
	@Override
	public Color getForeground(Object element) {
		if(element instanceof DefUnit) {
			return cDefUnitColor;
		}
		if(element instanceof Reference) {
			return cEntityColor;
		}
		return null;
	}
	
	@Override
	public Font getFont(Object element) {
		return null;
	}

}
