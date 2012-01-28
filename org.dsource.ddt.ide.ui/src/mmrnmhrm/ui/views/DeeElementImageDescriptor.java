package mmrnmhrm.ui.views;

import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.dltk.ui.ScriptElementImageDescriptor_Fix;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

import descent.internal.compiler.parser.PROT;

public class DeeElementImageDescriptor extends ScriptElementImageDescriptor_Fix {
	
	/** Code for the protection modifier. They are mutually exclusive therefore couldn't */
	public enum Protection {
		PRIVATE,
		PROTECTED,
		PUBLIC
	}
	
	private final Protection prot;
	
	public DeeElementImageDescriptor(ImageDescriptor baseImage, int flags, Protection prot, Point size) {
		super(baseImage, flags, size);
		this.prot = prot;
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		super.drawCompositeImage(width, height);
		Point pos;
		switch (prot) {
		
		case PRIVATE:
			pos = new Point (fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PRIVATE, pos);
			break;
			
		case PROTECTED:
			pos = new Point (fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PROTECTED, pos);
			break;
		
		case PUBLIC:
			break;
		}
	}
}
