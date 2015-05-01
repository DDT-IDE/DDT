package mmrnmhrm.ui.views;

import melnorme.lang.ide.ui.LangImages;
import melnorme.util.swt.jface.resources.ImageDescriptorRegistry;
import melnorme.util.swt.jface.resources.LangElementImageDescriptor;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeeImages;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
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
		return LangImages.getImageDescriptorRegistry();
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof IMember) {
			IMember member = (IMember) object;
			
			ImageDescriptor imageDescriptor = getImageDescriptor(member, LangElementImageDescriptor.DEFAULT_SIZE);
			
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
			DeeCore.logWarning("Error opening element for element descriptor", e);
			return DeeImages.getIDEInternalErrorImageDescriptor();
		}
	}
	
	public ImageDescriptor getImageDescriptor(DefElementDescriptor elementDesc, Point imageSize) {
		return new DeeElementImageProvider().getImageDescriptor(elementDesc, imageSize);
	}
	
}