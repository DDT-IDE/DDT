package mmrnmhrm.ui.views;

import mmrnmhrm.ui.DeePluginImages;

import org.dsource.ddt.ide.core.model.ProtectionAttribute;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor_Extension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

public class DeeElementImageDescriptor extends ScriptElementImageDescriptor_Extension {
	
	protected final ProtectionAttribute prot;
	
	public DeeElementImageDescriptor(ImageDescriptor baseImage, int flags, ProtectionAttribute prot, Point size) {
		super(baseImage, flags, size);
		this.prot = prot;
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		super.drawCompositeImage(width, height);
		if(prot == null)
			return;
		
		Point pos;
		switch (prot) {
		
		case PRIVATE:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PRIVATE, pos);
			break;
			
		case PROTECTED:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PROTECTED, pos);
			break;
		
		case PACKAGE:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_DEFAULT, pos);
			break;
		
		case PUBLIC:
		case EXPORT: // TODO?
			break;
		}
	}
}
