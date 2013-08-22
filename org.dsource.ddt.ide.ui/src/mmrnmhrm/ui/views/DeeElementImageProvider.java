package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.core.model_elements.DeeModelElementUtil;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import dtool.ast.ASTNode;
import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;

public class DeeElementImageProvider {
	
	public static final Point SMALL_SIZE= new Point(16, 16);
	public static final Point BIG_SIZE= new Point(22, 16);
	
	public static Image getElementImage(IElement element) {
		if (element instanceof ASTNode) {
			return getNodeImage((ASTNode) element);
		}
		return null;
	}
	
	public static Image getNodeImage(ASTNode node) {
		ElementIconsStyle iconStyle = DeeElementImageProvider.getIconStylePreference();
		ImageDescriptor imageDescriptor = getNodeImageDescriptor(node, iconStyle);
		return DeePluginImages.getImageDescriptorRegistry().get(imageDescriptor);
	}
	
	public static ElementIconsStyle getIconStylePreference() {
		String iconStyleStr = DeePlugin.getPrefStore().getString(DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE);
		return ElementIconsStyle.fromString(iconStyleStr, ElementIconsStyle.DDT);
	}
	
	public static ImageDescriptor getNodeImageDescriptor(ASTNode node, ElementIconsStyle iconStyle) {
		if(node instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) node;
			return getDefUnitImageDescriptor(defUnit, iconStyle);
		}
		
		return DeePluginImages.getManagedDescriptor(getNodeImageDescriptorKey(node));
	}
	
	public static ImageDescriptor getDefUnitImageDescriptor(DefUnit defUnit, ElementIconsStyle iconStyle) {
		DefElementDescriptor defDescriptor = new DefElementDescriptor(defUnit);
		return getDefUnitImageDescriptor(defDescriptor, iconStyle);
	}
	
	public static ImageDescriptor getDefUnitImageDescriptor(DefElementDescriptor defDescriptor,
		ElementIconsStyle iconStyle) {
		Point imageSize = DeeElementImageProvider.SMALL_SIZE;
		return new DeeElementImageProvider().getImageDescriptor(defDescriptor, imageSize, iconStyle);
	}
	
	/* ---------------------------------------------- */
	
	protected static String getNodeImageDescriptorKey(ASTNode node) {
		switch (node.getNodeType()) {
		case DECLARATION_IMPORT:
			return DeePluginImages.NODE_IMPORT;
		case DECLARATION_MODULE:
			return DeePluginImages.NODE_MODULE_DEC;
		default:
			break;
		}
		
		if (node instanceof Reference) {
			return DeePluginImages.NODE_REF;
		} 
		
		return DeePluginImages.NODE_OTHER;
	}
	
	public ImageDescriptor getImageDescriptor(DefElementDescriptor elementDesc, Point imageSize,
		ElementIconsStyle iconStyle) {
		assertNotNull(iconStyle);
		EArcheType archeType = elementDesc.getArcheType();
		int elementFlags = elementDesc.modifierFlags;
		if(archeType == null) {
			// archetype can be null if elementFlags is somehow wrongly created
			// for example, can happen if elementFlags is serialized/deserialized with incompatible DDT versions  
			return DeePluginImages.getIDEInternalErrorImageDescriptor();
		}
		
		ImageDescriptor baseImage = getBaseImageDescriptor(elementDesc, iconStyle);
		
		EProtection prot = null;
		if (iconStyle != ElementIconsStyle.JDTLIKE || 
				(archeType != EArcheType.Variable && archeType != EArcheType.Function)) {
			prot = DeeModelElementUtil.elementFlagsToProtection(elementFlags, null);
		}
		
		int imageFlags = getImageAdornmentFlags(elementFlags); // XXX flaw here, potential BUG 
		return new DeeElementImageDescriptor(baseImage, imageFlags, prot, imageSize);
	}
	
	protected ImageDescriptor getBaseImageDescriptor(DefElementDescriptor elementDesc, ElementIconsStyle iconStyle) {
		EArcheType archeType = elementDesc.getArcheType();
		int flags = elementDesc.modifierFlags;
		
		if(elementDesc.isNative()) {
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ELEM_PRIMITIVE);
		}
		
		switch (archeType) {
		case Package:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ELEM_PACKAGE);
		case Module:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.NODE_MODULE_DEC);
			
		case Variable:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleFieldImageDescriptor(flags); 
			}
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_VARIABLE);
			
		case Function:
		case Constructor:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_FUNCTION);
		case Struct:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_STRUCT);
		case Union:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_UNION);
		case Class:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_CLASS);
		case Interface:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_INTERFACE);
			
		case Template:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_TEMPLATE);
		case Mixin:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_MIXIN);
		case Enum:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_ENUM);
		case Alias:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_ALIAS);
			
		case Tuple:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_TEMPLATE);
		case TypeParameter:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_ALIAS);
		case EnumMember:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_VARIABLE);
		}
		
		throw assertFail();
		
	}
	
	public ImageDescriptor getJDTStyleFieldImageDescriptor(int flags) {
		switch (DeeModelElementUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_FIELD_PRIVATE);
		case PROTECTED:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_FIELD_PROTECTED);
		case PACKAGE: 
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_FIELD_DEFAULT);
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_FIELD_PUBLIC);
		}
		throw assertUnreachable();
	}
	
	public ImageDescriptor getJDTStyleMethodImageDescriptor(int flags) {
		switch (DeeModelElementUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_METHOD_PRIVATE);
		case PROTECTED:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_METHOD_PROTECTED);
		case PACKAGE: 
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_METHOD_DEFAULT);
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.IMG_METHOD_PUBLIC);
		}
		throw assertUnreachable();
	}
	
	protected int getImageAdornmentFlags(int modifiers) {
		int imageFlags = 0;
		
		if (DeeModelElementUtil.isConstructor(modifiers)) {
			imageFlags |= ScriptElementImageDescriptor.CONSTRUCTOR; // TODO: this should be its own base image
		}
		
		if(Flags.isAbstract(modifiers)) {
			imageFlags |= ScriptElementImageDescriptor.ABSTRACT;
		}
		if(Flags.isFinal(modifiers)) {
			imageFlags |= ScriptElementImageDescriptor.FINAL;
		}
		if(Flags.isStatic(modifiers)) {
			imageFlags |= ScriptElementImageDescriptor.STATIC;
		}
		
		return imageFlags;
	}
	
}
