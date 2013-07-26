package mmrnmhrm.ui.views;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class DeeModelElementLabelProvider extends LabelProvider implements ILabelProvider {
	
	public DeeModelElementLabelProvider() {
	}
	
	@Override
	public String getText(Object object) {
		return null; // Use DLTK defaults
	}
	
	public ImageDescriptorRegistry getImageDescriptorRegistry() {
		return DeePluginImages.getImageDescriptorRegistry();
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof IMember) {
			IMember member = (IMember) object;
			
			// XXX: Due to a DLTK limitation we don't know what image size is preferred. 
			// BM: so we do this awful hack to try to figure it out, 
			// I'm particularly concerned about performance, but since it is UI elements code, it should be
			// called a limited number of times 
			
			Point imageSize = DeeElementImageProvider.BIG_SIZE;
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < 5; i++) {
				if(stackTrace[i].getClassName().startsWith(
						"org.eclipse.dltk.internal.ui.navigator.ScriptExplorerLabelProvider")) {
					imageSize = DeeElementImageProvider.SMALL_SIZE; // Use small size for the Script Explorer
				}
			}
			
			ImageDescriptor imageDescriptor = getImageDescriptor(member, imageSize);
			
			return getImageDescriptorRegistry().get(imageDescriptor);
		} else {
			return null;
		}
	}
	
	public ImageDescriptor getImageDescriptor(IMember member, Point imageSize) {
		
		try {
			DefElementDescriptor elementDescriptor = DeeSourceElementProvider.toElementDescriptor(member);
			return getImageDescriptor(elementDescriptor, imageSize);
		} catch (ModelException e) {
			DeeCore.logError(e);
			return DeePluginImages.getIDEInternalErrorImageDescriptor();
		}
	}
	
	public ImageDescriptor getImageDescriptor(DefElementDescriptor elementDesc, Point imageSize) {
		ElementIconsStyle iconStyle = getIconStylePreference();
		return new DeeElementImageProvider().getImageDescriptor(elementDesc, imageSize, iconStyle);
	}
	
	protected ElementIconsStyle getIconStylePreference() {
		return DeeElementImageProvider.getIconStylePreference();
	}
	
}