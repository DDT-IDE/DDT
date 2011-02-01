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

	// Registry must be on top, to be initialized firt 
	private static ImageRegistry registry = DeePlugin.getInstance().getImageRegistry();

	
	public static final String ELEM_MODULE = createImage_Obj("ent_module.gif");
	public static final String ELEM_SOURCEFOLDER = createImage_Obj("dee_packagefolder.gif");
	public static final String ELEM_LIBRARY = createImage_Obj("dee_library.gif");
	public static final String ELEM_PACKAGE = createImage_Obj("dee_package.gif");
	public static final String ELEM_FILE = createImage_Obj("dee_file.gif");

	
	public static final String ENT_ALIAS = createImage_Obj("ent_alias.gif");
	public static final String ENT_CLASS = createImage_Obj("ent_class.gif");
	public static final String ENT_ENUM = createImage_Obj("ent_enum.gif");
	public static final String ENT_INTERFACE = createImage_Obj("ent_interface.gif");
	public static final String ENT_STRUCT = createImage_Obj("ent_struct.gif");
	public static final String ENT_TEMPLATE= createImage_Obj("ent_template.gif");
	public static final String ENT_TYPEDEF = createImage_Obj("ent_typedef.gif");
	public static final String ENT_UNION = createImage_Obj("ent_union.gif");

	public static final String ENT_VARIABLE = createImage_Obj("ent_variable.gif");
	public static final String ENT_FUNCTION = createImage_Obj("ent_function.gif");

	public static final String NODE_MODULE_DEC = createImage_Obj("elem_module_dec.gif");
	public static final String NODE_IMPORT = createImage_Obj("elem_import.gif");
	public static final String NODE_COND = createImage_Obj("elem_cond.gif");
	
	public static final String NODE_OLDAST = createImage_Obj("node_oldast.gif");
	public static final String NODE_UNKNOWN = createImage_Obj("node_unknown.gif");
	public static final String NODE_OTHER = createImage_Obj("node_other.gif");
	public static final String NODE_BASEREF = createImage_Obj("node_baseref.gif");
	public static final String NODE_REF = createImage_Obj("node_ref.gif");



	private DeePluginImages() {} // Don't instantiate
	
	private static String createImage_Obj(String imageName) {
		String imgPath = ICONS_PATH + "obj16/" + imageName;
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
