package mmrnmhrm.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.FileNotFoundException;

import mmrnmhrm.lang.ui.PluginImagesHelperExtension;

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
	private static final PluginImagesHelperExtension helper = new PluginImagesHelperExtension(
			DeeUI.getDefault().getBundle(), ICONS_PATH);
	
	private static ImageDescriptor createUnmanaged(String base, String imageName) {
		// Hard fail if image not found.
		return assertNotNull(helper.createUnManaged(base, imageName, false));
	}
	
	public static final String ELEM_PACKAGE = createImage(T_OBJ, "dee_package.gif");
	public static final String ELEM_FILE = createImage(T_OBJ, "dee_file.gif");
	
	
	public static final String ENT_VARIABLE = createImage(T_OBJ, "ent_variable.gif");
	public static final String ENT_FUNCTION = createImage(T_OBJ, "ent_function.gif");
	
	// We're using 'default' protection for 'package' protection in D
	// No special icon for 'export' protection in D, same as public
	public static final String IMG_METHOD_PUBLIC = createImage(T_OBJ, "function_public.gif");
	public static final String IMG_METHOD_PROTECTED = createImage(T_OBJ,"function_protected.gif");
	public static final String IMG_METHOD_PRIVATE = createImage(T_OBJ, "function_private.gif");
	public static final String IMG_METHOD_DEFAULT = createImage(T_OBJ,"function_default.gif");
	
	public static final String IMG_FIELD_PUBLIC = createImage(T_OBJ, "field_public_obj.gif");
	public static final String IMG_FIELD_PROTECTED = createImage(T_OBJ, "field_protected_obj.gif");
	public static final String IMG_FIELD_PRIVATE = createImage(T_OBJ, "field_private_obj.gif");
	public static final String IMG_FIELD_DEFAULT = createImage(T_OBJ, "field_default_obj.gif");
	
	public static final String ELEM_PRIMITIVE = createImage(T_OBJ, "ent_struct.gif"); // TODO
	
	public static final String ENT_STRUCT = createImage(T_OBJ, "ent_struct.gif");
	public static final String ENT_UNION = createImage(T_OBJ, "ent_union.gif");
	public static final String ENT_CLASS = createImage(T_OBJ, "ent_class.gif");
	public static final String ENT_INTERFACE = createImage(T_OBJ, "ent_interface.png");
	public static final String ENT_TEMPLATE = createImage(T_OBJ, "ent_template.gif");
	public static final String ENT_MIXIN = createImage(T_OBJ, "ent_namespace.gif");
	public static final String ENT_ENUM = createImage(T_OBJ, "ent_enum.gif");
	public static final String ENT_ALIAS = createImage(T_OBJ, "ent_alias.gif");
	
	public static final String NODE_MODULE_DEC = createImage(T_OBJ, "elem_module_dec.gif");
	public static final String NODE_IMPORT = createImage(T_OBJ, "elem_import.gif");
	public static final String NODE_IMPORTS = createImage(T_OBJ, "elem_imports.gif");
	
	public static final String NODE_OTHER = createImage(T_OBJ, "node_other.gif");
	public static final String NODE_REF = createImage(T_OBJ, "node_ref.gif");
	
	
	public static final ImageDescriptor DESC_OVR_FINAL = createUnmanaged(T_OVR, "ovr_final.gif");
	public static final ImageDescriptor DESC_OVR_STATIC = createUnmanaged(T_OVR, "ovr_static.png");
	
	public static final ImageDescriptor DESC_OVR_CONST = createUnmanaged(T_OVR, "ovr_const.png");
	public static final ImageDescriptor DESC_OVR_IMMUTABLE = createUnmanaged(T_OVR, "ovr_immutable.png");
	public static final ImageDescriptor DESC_OVR_OVERRIDE = createUnmanaged(T_OVR, "ovr_override.png");
	
	public static final ImageDescriptor DESC_OVR_PRIVATE = createUnmanaged(T_OVR, "aggregate-private.png");
	public static final ImageDescriptor DESC_OVR_PROTECTED = createUnmanaged(T_OVR, "aggregate-protected.png");
	public static final ImageDescriptor DESC_OVR_DEFAULT = createUnmanaged(T_OVR, "aggregate-default.png");
	
	
	public static final ImageDescriptor OPEN_DEF_DESC = getActionImageDescriptor("gotodef.gif", true);
	
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
		return helper.get(imageKey);
	}
	
	/** Gets the managed {@link ImageDescriptor} associated with the given key. */
	public static ImageDescriptor getManagedDescriptor(String imageKey) {
		return helper.getDescriptor(imageKey);
	}
	
	/* ------------------------------------------- */
	
	/** This is an alternate registry with keys based on ImageDescriptors.
	 * XXX: maybe this should be consolidated with the other String key-based registry */ 
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		return DLTKUIPlugin.getImageDescriptorRegistry();
	}
	
	/* ------------------------------------------- */
	
	public static ImageDescriptor getIDEInternalErrorImageDescriptor() {
		// BM: maybe there's some other image we could use
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
}