package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.CoreUtil.downCast;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.dltk.ui.ScriptElementImageDescriptor_Extension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

import dtool.ast.declarations.AttribProtection.EProtection;

public class DeeElementImageDescriptor extends ScriptElementImageDescriptor_Extension {
	
	protected final EProtection prot;
	
	public DeeElementImageDescriptor(ImageDescriptor baseImage, int flags, EProtection prot, Point size) {
		super(baseImage, flags, size);
		this.prot = prot;
	}
	
	@Override
	public boolean equalsPeer(ScriptElementImageDescriptor_Extension object) {
		DeeElementImageDescriptor other = downCast(object);
		return this.prot == other.prot && super.equalsPeer(other);
	}
	
	@Override
	public int hashCode() {
		return MiscUtil.combineHashCodes(prot == null ? 0 : prot.hashCode(), super.hashCode());
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
