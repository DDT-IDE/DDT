package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.ide.ui.utils.PluginImagesHelper.ImageHandle;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.core.model_elements.DefElementFlagConstants;
import mmrnmhrm.core.model_elements.DefElementFlagsUtil;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;

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
		String iconStyleStr = DeeUIPlugin.getPrefStore().getString(DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE);
		return ElementIconsStyle.fromString(iconStyleStr, ElementIconsStyle.DDT);
	}
	
	public static ImageDescriptor getNodeImageDescriptor(ASTNode node, ElementIconsStyle iconStyle) {
		if(node instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) node;
			return getDefUnitImageDescriptor(defUnit, iconStyle);
		}
		
		return getNodeImageDescriptorKey(node).getDescriptor();
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
	
	protected static ImageHandle getNodeImageDescriptorKey(ASTNode node) {
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
		int elementFlags = elementDesc.elementFlags;
		if(archeType == null) {
			// archetype can be null if elementFlags is somehow wrongly created
			// for example, can happen if elementFlags is serialized/deserialized with incompatible DDT versions  
			return DeePluginImages.getIDEInternalErrorImageDescriptor();
		}
		
		ImageDescriptor baseImage = getBaseImageDescriptor(elementDesc, iconStyle);
		
		EProtection prot;
		if (iconStyle == ElementIconsStyle.JDTLIKE && 
			(archeType == EArcheType.Variable || archeType == EArcheType.Function)) {
			prot = null; // Don't render protection adornment
		} else {
			prot = DefElementFlagsUtil.elementFlagsToProtection(elementFlags, null);
			
			if(elementDesc.getArcheType() == EArcheType.Constructor) {
				// This is to prevent drawing the constructor 'C' adornment
				elementDesc.setArcheType(DefElementFlagConstants.FLAG_KIND_FUNCTION);
			}
		}
		
		return new DeeElementImageDescriptor(baseImage, elementDesc, prot, imageSize);
	}
	
	protected ImageDescriptor getBaseImageDescriptor(DefElementDescriptor elementDesc, ElementIconsStyle iconStyle) {
		EArcheType archeType = elementDesc.getArcheType();
		int flags = elementDesc.elementFlags;
		
		if(elementDesc.isNative()) {
			return DeePluginImages.ENT_NATIVE.getDescriptor();
		}
		
		switch (archeType) {
		case Package:
			return DeePluginImages.ELEM_PACKAGE.getDescriptor();
		case Module:
			return DeePluginImages.NODE_MODULE_DEC.getDescriptor();
			
		case Variable:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleFieldImageDescriptor(flags); 
			}
			return DeePluginImages.ENT_VARIABLE.getDescriptor();
			
		case Function:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeePluginImages.ENT_FUNCTION.getDescriptor();
		case Constructor:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeePluginImages.ENT_CONSTRUCTOR.getDescriptor();
		case Struct:
			return DeePluginImages.ENT_STRUCT.getDescriptor();
		case Union:
			return DeePluginImages.ENT_UNION.getDescriptor();
		case Class:
			return DeePluginImages.ENT_CLASS.getDescriptor();
		case Interface:
			return DeePluginImages.ENT_INTERFACE.getDescriptor();
			
		case Template:
			return DeePluginImages.ENT_TEMPLATE.getDescriptor();
		case Mixin:
			return DeePluginImages.ENT_MIXIN.getDescriptor();
		case Enum:
			return DeePluginImages.ENT_ENUM.getDescriptor();
		case Alias:
			return DeePluginImages.ENT_NATIVE.getDescriptor();
			
		case Tuple:
			return DeePluginImages.ENT_TUPLE.getDescriptor();
		case TypeParameter:
			return DeePluginImages.ENT_TYPE_PARAMETER.getDescriptor();
		case EnumMember:
			return DeePluginImages.ENT_VARIABLE.getDescriptor();
		}
		
		throw assertFail();
		
	}
	
	public ImageDescriptor getJDTStyleFieldImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.IMG_FIELD_PRIVATE.getDescriptor();
		case PROTECTED:
			return DeePluginImages.IMG_FIELD_PROTECTED.getDescriptor();
		case PACKAGE: 
			return DeePluginImages.IMG_FIELD_DEFAULT.getDescriptor();
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.IMG_FIELD_PUBLIC.getDescriptor();
		}
		throw assertUnreachable();
	}
	
	public ImageDescriptor getJDTStyleMethodImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.IMG_METHOD_PRIVATE.getDescriptor();
		case PROTECTED:
			return DeePluginImages.IMG_METHOD_PROTECTED.getDescriptor();
		case PACKAGE: 
			return DeePluginImages.IMG_METHOD_DEFAULT.getDescriptor();
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.IMG_METHOD_PUBLIC.getDescriptor();
		}
		throw assertUnreachable();
	}
	
}