package mmrnmhrm.lang.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ui.PluginImagesHelper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

public class PluginImagesHelperExtension extends PluginImagesHelper {
	
	protected final IPath iconsPath;
	protected final Bundle bundle;
	
	public PluginImagesHelperExtension(Bundle bundle, IPath iconsPath) {
		super(bundle, iconsPath);
		this.bundle = bundle;
		this.iconsPath = iconsPath;
	}
	
	protected ImageDescriptor doCreate(String prefix, String name, boolean useMissingImageDescriptor) {
		IPath path = iconsPath.append(prefix).append(name);
		return createImageDescriptor(bundle, path, useMissingImageDescriptor);
	}
	
	public ImageDescriptor createUnManaged(String prefix, String name, boolean useMissingImageDescriptor) {
		return doCreate(prefix, name, useMissingImageDescriptor);
	}
	
}