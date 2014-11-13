package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.ide.ui.utils.PluginImagesHelper.ImageHandle;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.core.model_elements.DefElementFlagConstants;
import mmrnmhrm.core.model_elements.DefElementFlagsUtil;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

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
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor);
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
			return DeeImages.NODE_IMPORT;
		case DECLARATION_MODULE:
			return DeeImages.NODE_MODULE_DEC;
		default:
			break;
		}
		
		if (node instanceof Reference) {
			return DeeImages.NODE_REF;
		} 
		
		return DeeImages.NODE_OTHER;
	}
	
	public ImageDescriptor getImageDescriptor(DefElementDescriptor elementDesc, Point imageSize,
		ElementIconsStyle iconStyle) {
		assertNotNull(iconStyle);
		EArcheType archeType = elementDesc.getArcheType();
		int elementFlags = elementDesc.elementFlags;
		if(archeType == null) {
			// archetype can be null if elementFlags is somehow wrongly created
			// for example, can happen if elementFlags is serialized/deserialized with incompatible DDT versions  
			return DeeImages.getIDEInternalErrorImageDescriptor();
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
			return DeeImages.ENT_NATIVE.getDescriptor();
		}
		
		switch (archeType) {
		case Package:
			return DeeImages.ELEM_PACKAGE.getDescriptor();
		case Module:
			return DeeImages.NODE_MODULE_DEC.getDescriptor();
			
		case Variable:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleFieldImageDescriptor(flags); 
			}
			return DeeImages.ENT_VARIABLE.getDescriptor();
			
		case Function:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeeImages.ENT_FUNCTION.getDescriptor();
		case Constructor:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeeImages.ENT_CONSTRUCTOR.getDescriptor();
		case Struct:
			return DeeImages.ENT_STRUCT.getDescriptor();
		case Union:
			return DeeImages.ENT_UNION.getDescriptor();
		case Class:
			return DeeImages.ENT_CLASS.getDescriptor();
		case Interface:
			return DeeImages.ENT_INTERFACE.getDescriptor();
			
		case Template:
			return DeeImages.ENT_TEMPLATE.getDescriptor();
		case Mixin:
			return DeeImages.ENT_MIXIN.getDescriptor();
		case Enum:
			return DeeImages.ENT_ENUM.getDescriptor();
		case Alias:
			return DeeImages.ENT_NATIVE.getDescriptor();
			
		case Tuple:
			return DeeImages.ENT_TUPLE.getDescriptor();
		case TypeParameter:
			return DeeImages.ENT_TYPE_PARAMETER.getDescriptor();
		case EnumMember:
			return DeeImages.ENT_VARIABLE.getDescriptor();
		}
		
		throw assertFail();
		
	}
	
	public ImageDescriptor getJDTStyleFieldImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeeImages.IMG_FIELD_PRIVATE.getDescriptor();
		case PROTECTED:
			return DeeImages.IMG_FIELD_PROTECTED.getDescriptor();
		case PACKAGE: 
			return DeeImages.IMG_FIELD_DEFAULT.getDescriptor();
		case PUBLIC:
		case EXPORT:
			return DeeImages.IMG_FIELD_PUBLIC.getDescriptor();
		}
		throw assertUnreachable();
	}
	
	public ImageDescriptor getJDTStyleMethodImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeeImages.IMG_METHOD_PRIVATE.getDescriptor();
		case PROTECTED:
			return DeeImages.IMG_METHOD_PROTECTED.getDescriptor();
		case PACKAGE: 
			return DeeImages.IMG_METHOD_DEFAULT.getDescriptor();
		case PUBLIC:
		case EXPORT:
			return DeeImages.IMG_METHOD_PUBLIC.getDescriptor();
		}
		throw assertUnreachable();
	}
	
}