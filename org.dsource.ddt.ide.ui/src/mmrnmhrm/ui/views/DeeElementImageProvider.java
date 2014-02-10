package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.utilbox.tree.IElement;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.core.model_elements.DefElementFlagConstants;
import mmrnmhrm.core.model_elements.DefElementFlagsUtil;
import mmrnmhrm.ui.DeePlugin;
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
		String iconStyleStr = DeePlugin.getPrefStore().getString(DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE);
		return ElementIconsStyle.fromString(iconStyleStr, ElementIconsStyle.DDT);
	}
	
	public static ImageDescriptor getNodeImageDescriptor(ASTNode node, ElementIconsStyle iconStyle) {
		if(node instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) node;
			return getDefUnitImageDescriptor(defUnit, iconStyle);
		}
		
		return DeePluginImages.getDescriptor(getNodeImageDescriptorKey(node));
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
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_NATIVE);
		}
		
		switch (archeType) {
		case Package:
			return DeePluginImages.getDescriptor(DeePluginImages.ELEM_PACKAGE);
		case Module:
			return DeePluginImages.getDescriptor(DeePluginImages.NODE_MODULE_DEC);
			
		case Variable:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleFieldImageDescriptor(flags); 
			}
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_VARIABLE);
			
		case Function:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_FUNCTION);
		case Constructor:
			if(iconStyle == ElementIconsStyle.JDTLIKE) {
				return getJDTStyleMethodImageDescriptor(flags);
			}
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_CONSTRUCTOR);
		case Struct:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_STRUCT);
		case Union:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_UNION);
		case Class:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_CLASS);
		case Interface:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_INTERFACE);
			
		case Template:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_TEMPLATE);
		case Mixin:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_MIXIN);
		case Enum:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_ENUM);
		case Alias:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_NATIVE);
			
		case Tuple:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_TUPLE);
		case TypeParameter:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_TYPE_PARAMETER);
		case EnumMember:
			return DeePluginImages.getDescriptor(DeePluginImages.ENT_VARIABLE);
		}
		
		throw assertFail();
		
	}
	
	public ImageDescriptor getJDTStyleFieldImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_FIELD_PRIVATE);
		case PROTECTED:
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_FIELD_PROTECTED);
		case PACKAGE: 
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_FIELD_DEFAULT);
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_FIELD_PUBLIC);
		}
		throw assertUnreachable();
	}
	
	public ImageDescriptor getJDTStyleMethodImageDescriptor(int flags) {
		switch (DefElementFlagsUtil.elementFlagsToProtection(flags, EProtection.PUBLIC)) {
		case PRIVATE: 
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_METHOD_PRIVATE);
		case PROTECTED:
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_METHOD_PROTECTED);
		case PACKAGE: 
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_METHOD_DEFAULT);
		case PUBLIC:
		case EXPORT:
			return DeePluginImages.getDescriptor(DeePluginImages.IMG_METHOD_PUBLIC);
		}
		throw assertUnreachable();
	}
	
}