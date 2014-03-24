package mmrnmhrm.ui;

import melnorme.lang.ide.ui.LangImages;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;

public abstract class DeePluginImages extends LangImages {
	
	protected static final String T_ACTIONS = "action16e";
	
	public static final String ELEM_PACKAGE = createManaged(T_OBJ, "dee_package.gif");
	public static final String ELEM_FILE = createManaged(T_OBJ, "dee_file.gif");
	
	public static final String NODE_MODULE_DEC = createManaged(T_OBJ, "ent_module.png");

	public static final String ENT_VARIABLE = createManaged(T_OBJ, "ent_variable.png");
	public static final String ENT_FUNCTION = createManaged(T_OBJ, "ent_function.png");
	public static final String ENT_CONSTRUCTOR = createManaged(T_OBJ, "ent_constructor.png");
	
	// We're using 'default' protection for 'package' protection in D
	// No special icon for 'export' protection in D, same as public
	public static final String IMG_METHOD_PUBLIC = createManaged(T_OBJ, "function_public.gif");
	public static final String IMG_METHOD_PROTECTED = createManaged(T_OBJ, "function_protected.gif");
	public static final String IMG_METHOD_PRIVATE = createManaged(T_OBJ, "function_private.gif");
	public static final String IMG_METHOD_DEFAULT = createManaged(T_OBJ, "function_default.gif");
	
	public static final String IMG_FIELD_PUBLIC = createManaged(T_OBJ, "field_public_obj.gif");
	public static final String IMG_FIELD_PROTECTED = createManaged(T_OBJ, "field_protected_obj.gif");
	public static final String IMG_FIELD_PRIVATE = createManaged(T_OBJ, "field_private_obj.gif");
	public static final String IMG_FIELD_DEFAULT = createManaged(T_OBJ, "field_default_obj.gif");
	
	
	public static final String ENT_NATIVE = createManaged(T_OBJ, "ent_native.png");
	public static final String ENT_STRUCT = createManaged(T_OBJ, "ent_struct.png");
	public static final String ENT_UNION = createManaged(T_OBJ, "ent_union.gif");
	public static final String ENT_CLASS = createManaged(T_OBJ, "ent_class.gif");
	public static final String ENT_INTERFACE = createManaged(T_OBJ, "ent_interface.png");
	public static final String ENT_TEMPLATE = createManaged(T_OBJ, "ent_namespace.png");
	public static final String ENT_MIXIN = createManaged(T_OBJ, "ent_namespace.png");
	public static final String ENT_ENUM = createManaged(T_OBJ, "ent_enum.gif");
	public static final String ENT_TYPE_PARAMETER = createManaged(T_OBJ, "ent_type_parameter.png");
	public static final String ENT_TUPLE = createManaged(T_OBJ, "ent_tuple.png");
//	public static final String ENT_UNKOWN_ALIAS = createImage(T_OBJ, "ent_alias.png");
	
	
	public static final String NODE_IMPORT = createManaged(T_OBJ, "elem_import.gif");
	public static final String NODE_IMPORTS = createManaged(T_OBJ, "elem_imports.gif");
	
	public static final String NODE_OTHER = createManaged(T_OBJ, "node_other.gif");
	public static final String NODE_REF = createManaged(T_OBJ, "node_ref.gif");
	
	
	public static final ImageDescriptor DESC_OVR_PRIVATE = createUnmanaged(T_OVR, "aggregate-private.png");
	public static final ImageDescriptor DESC_OVR_PROTECTED = createUnmanaged(T_OVR, "aggregate-protected.png");
	public static final ImageDescriptor DESC_OVR_DEFAULT = createUnmanaged(T_OVR, "aggregate-default.png");
	
	public static final ImageDescriptor DESC_OVR_FINAL = createUnmanaged(T_OVR, "ovr_final.png");
	public static final ImageDescriptor DESC_OVR_STATIC = createUnmanaged(T_OVR, "ovr_static.png");
	public static final ImageDescriptor DESC_OVR_ABSTRACT = createUnmanaged(T_OVR, "ovr_abstract.png");
	
	public static final ImageDescriptor DESC_OVR_CONST = createUnmanaged(T_OVR, "ovr_const.png");
	public static final ImageDescriptor DESC_OVR_IMMUTABLE = createUnmanaged(T_OVR, "ovr_immutable.png");
	
	public static final ImageDescriptor DESC_OVR_TEMPLATED = createUnmanaged(T_OVR, "ovr_templated.png");
	public static final ImageDescriptor DESC_OVR_ALIAS = createUnmanaged(T_OVR, "ovr_alias_arrow.png");
	
	
	/* ---------- DUB elements ---------- */
	
	protected static final String T_OBJ_DUB = "obj16_dub";
	
	public static final String DUB_DEPENDENCIES_CONTAINER = createManaged(T_OBJ_DUB, "DubDependenciesContainer.png");
	public static final String DUB_BUNDLE_DEP = createManaged(T_OBJ_DUB, "DubBundleDep.png");
	public static final String DUB_RAW_DEP = createManaged(T_OBJ_DUB, "DubRawDep.png");
	public static final String DUB_ERROR_ELEMENT = createFromPlatformSharedImage(T_OBJ_DUB, "DubErrorElement", 
		ISharedImages.IMG_OBJS_ERROR_TSK);
	public static final String DUB_MANIFEST = createManaged(T_OBJ_DUB, "DubManifest.png");
	public static final String SOURCE_FOLDER = createManaged(T_OBJ_DUB, "SourceFolder.png");
	public static final String BINARY_FOLDER = createManaged(T_OBJ_DUB, "BinaryFolder.png");
	
	public static final String DUB_PROCESS = createManaged(T_OBJ_DUB, "DubProcess.png");
	
	/* ------------------------------------------- */
	
	public static final ImageDescriptor OPEN_DEF_DESC = createUnmanaged(T_ACTIONS, "gotodef.gif");
	
	@Deprecated
	public static ImageDescriptor getActionImageDescriptor(String file, boolean useMissingImageDescriptor) {
		return helper.createUnmanaged(T_ACTIONS, file, useMissingImageDescriptor);
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