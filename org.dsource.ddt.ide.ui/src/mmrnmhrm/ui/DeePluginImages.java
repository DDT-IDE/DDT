package mmrnmhrm.ui;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PluginImagesHelper;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class DeePluginImages {
	
	private DeePluginImages() {} // Don't instantiate
	
	
	public static final IPath ICONS_PATH= new Path("$nl$/icons/");
	
	private static final String ACTIONS_PATH = "action16e";
	
	private static final String T_OBJ = "obj16";
	private static final String T_OVR = "ovr16";
	
	// Registry/helper must be on top, to be initialized before createImage is used 
	private static final PluginImagesHelper helper = new PluginImagesHelper(
			DeeUI.getDefault().getBundle(), ICONS_PATH);
	
	private static ImageDescriptor createUnmanaged(String base, String imageName) {
		return helper.createUnManaged(base, imageName);
	}

	
	public static final String ELEM_PACKAGE = createImage(T_OBJ, "dee_package.gif");
	public static final String ELEM_FILE = createImage(T_OBJ, "dee_file.gif");
	
	
	public static final String ENT_ALIAS = createImage(T_OBJ, "ent_alias.gif");
	public static final String ENT_CLASS = createImage(T_OBJ, "ent_class.gif");
	public static final String ENT_ENUM = createImage(T_OBJ, "ent_enum.gif");
	public static final String ENT_INTERFACE = createImage(T_OBJ, "ent_interface.gif");
	public static final String ENT_STRUCT = createImage(T_OBJ, "ent_struct.gif");
	public static final String ENT_TEMPLATE= createImage(T_OBJ, "ent_template.gif");
	public static final String ENT_TYPEDEF = createImage(T_OBJ, "ent_typedef.gif");
	public static final String ENT_UNION = createImage(T_OBJ, "ent_union.gif");
	
	public static final String ENT_VARIABLE = createImage(T_OBJ, "ent_variable.gif");
	public static final String ENT_FUNCTION = createImage(T_OBJ, "ent_function.gif");
	
	public static final String NODE_MODULE_DEC = createImage(T_OBJ, "elem_module_dec.gif");
	public static final String NODE_IMPORT = createImage(T_OBJ, "elem_import.gif");
	public static final String NODE_IMPORTS = createImage(T_OBJ, "elem_imports.gif");
	
	public static final String NODE_OLDAST = createImage(T_OBJ, "node_oldast.gif");
	public static final String NODE_OTHER = createImage(T_OBJ, "node_other.gif");
	public static final String NODE_REF = createImage(T_OBJ, "node_ref.gif");
	
	
	public static final ImageDescriptor DESC_OVR_CONST = createUnmanaged(T_OVR, "ovr_const.gif");
	public static final ImageDescriptor DESC_OVR_FINAL = createUnmanaged(T_OVR, "ovr_final.gif");
	public static final ImageDescriptor DESC_OVR_IMMUTABLE = createUnmanaged(T_OVR, "ovr_immutable.gif");
	public static final ImageDescriptor DESC_OVR_STATIC = createUnmanaged(T_OVR, "ovr_static.png");
	//public static final ImageDescriptor DESC_OVR_STATIC = createUnmanaged(T_OVR, "ovr_static.gif");
	
	
	private static String createImage(String base, String imageName) {
		ImageDescriptor imgDesc = getImageDescriptor(base, imageName, false);
		if(imgDesc == null) {
			String imgPath = ICONS_PATH.append(base).append(imageName).toString();
			DeePlugin.log(new FileNotFoundException(imgPath));
		}
		String key = imageName;
		helper.createManaged(base, imageName, key);
		return key;
	}
	
	
	private static ImageDescriptor getImageDescriptor(String prefix, String name, 
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);
		Bundle bundle = DeeUI.getInstance().getBundle();
		return PluginImagesHelper.createImageDescriptor(bundle, path, useMissingImageDescriptor);
	}
	
	public static ImageDescriptor getActionImageDescriptor(String file, boolean useMissingImageDescriptor) {
		return getImageDescriptor(ACTIONS_PATH, file, useMissingImageDescriptor);
	}
	
	/** Gets the managed {@link Image} associated with the given key. */
	public static Image getManagedImage(String imageKey) {
		return helper.getImageRegistry().get(imageKey);
	}
	
	/** Gets the managed {@link ImageDescriptor} associated with the given key. */
	public static ImageDescriptor getManagedDescriptor(String imageKey) {
		return helper.getDescriptor(imageKey);
	}
	
	/* ------------------------------------------- */
	
	/** This is an alternate registry with keys based on ImageDescriptors.
	 * XXX: maybe this should be consolidate with the other String key-based registry */ 
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return DLTKUIPlugin.getImageDescriptorRegistry();
	}

}
