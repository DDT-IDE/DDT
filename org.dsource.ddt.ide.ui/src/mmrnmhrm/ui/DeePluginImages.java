package mmrnmhrm.ui;

import java.io.FileNotFoundException;

import mmrnmhrm.lang.ui.LangPluginImages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class DeePluginImages {
	

	public static final IPath ICONS_PATH= new Path("$nl$/icons/");

	private static final String ACTIONS_PATH = "action16e";
	
	private static final String T_OBJ = "obj16";
	private static final String T_OVR = "ovr16";

	// Registry must be on top, to be initialized firt 
	private static ImageRegistry registry = DeePlugin.getInstance().getImageRegistry();

	
	public static final String ELEM_MODULE = createImage(T_OBJ, "ent_module.gif");
	public static final String ELEM_SOURCEFOLDER = createImage(T_OBJ, "dee_packagefolder.gif");
	public static final String ELEM_LIBRARY = createImage(T_OBJ, "dee_library.gif");
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
	public static final String NODE_COND = createImage(T_OBJ, "elem_cond.gif");
	
	public static final String NODE_OLDAST = createImage(T_OBJ, "node_oldast.gif");
	public static final String NODE_UNKNOWN = createImage(T_OBJ, "node_unknown.gif");
	public static final String NODE_OTHER = createImage(T_OBJ, "node_other.gif");
	public static final String NODE_BASEREF = createImage(T_OBJ, "node_baseref.gif");
	public static final String NODE_REF = createImage(T_OBJ, "node_ref.gif");



	private DeePluginImages() {} // Don't instantiate
	
	private static String createImage(String base, String imageName) {
		String imgPath = ICONS_PATH.append(base).append(imageName).toString();
		ImageDescriptor imgDesc = DeePlugin.imageDescriptorFromPlugin(DeePlugin.PLUGIN_ID, imgPath);
		if(imgDesc == null) {
			DeePlugin.log(new FileNotFoundException(imgPath));
		}
		registry.put(imageName, imgDesc);
		return imageName;
	}
	
	/** Gets the shared imaged associated with the given key. */
	public static Image getImage(String imageKey) {
		return registry.get(imageKey);
	}


	private static ImageDescriptor createImageDescriptor(String prefix,
			String name, boolean useMissingImageDescriptor) {
		IPath path= ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(path, useMissingImageDescriptor);
	}

	private static ImageDescriptor createImageDescriptor(IPath path,
			boolean useMissingImageDescriptor) {
		return LangPluginImages.createImageDescriptor(DeePlugin.getInstance().getBundle(),
				path, useMissingImageDescriptor);
	}
	
	public static ImageDescriptor createActionImageDescriptor(String file,
			boolean useMissingImageDescriptor) {
		return DeePluginImages.createImageDescriptor(ACTIONS_PATH, file, useMissingImageDescriptor);
	}

	public static void setupActionImages(IAction action, String file) {
		ImageDescriptor imgDesc = createActionImageDescriptor(file, true); 
		action.setImageDescriptor(imgDesc);
	}

}
