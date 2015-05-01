package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.EnumSet;

import melnorme.lang.ide.ui.utils.PluginImagesHelper.ImageHandle;
import melnorme.lang.tooling.EAttributeFlag;
import melnorme.lang.tooling.EProtection;
import melnorme.lang.tooling.ElementAttributes;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.util.swt.jface.resources.LangElementImageDescriptor;
import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.core.model_elements.DefElementFlagConstants;
import mmrnmhrm.core.model_elements.DefElementFlagsUtil;
import mmrnmhrm.ui.DeeImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;

public class DeeElementImageProvider {
	
	public static final Point SMALL_SIZE= new Point(16, 16);
	
	public static Image getElementImage(IASTNode element) {
		if (element instanceof ASTNode) {
			return getNodeImage((ASTNode) element);
		}
		return null;
	}
	
	public static Image getNodeImage(ASTNode node) {
		ImageDescriptor imageDescriptor = getNodeImageDescriptor(node);
		return DeeImages.getImageDescriptorRegistry().get(imageDescriptor);
	}
	
	
	public static ImageDescriptor getNodeImageDescriptor(ASTNode node) {
		if(node instanceof DefUnit) {
			DefUnit defUnit = (DefUnit) node;
			return getDefUnitImageDescriptor(defUnit);
		}
		
		return getNodeImageDescriptorKey(node).getDescriptor();
	}
	
	public static ImageDescriptor getDefUnitImageDescriptor(DefUnit defUnit) {
		DefElementDescriptor defDescriptor = new DefElementDescriptor(defUnit);
		return getDefUnitImageDescriptor(defDescriptor);
	}
	
	public static ImageDescriptor getDefUnitImageDescriptor(DefElementDescriptor defDescriptor) {
		Point imageSize = DeeElementImageProvider.SMALL_SIZE;
		return new DeeElementImageProvider().getImageDescriptor(defDescriptor, imageSize);
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
	
	public ImageDescriptor getImageDescriptor(DefElementDescriptor elementDesc, Point imageSize) {
		EArcheType archeType = elementDesc.getArcheType();
		int elementFlags = elementDesc.elementFlags;
		if(archeType == null) {
			// archetype can be null if elementFlags is somehow wrongly created
			// for example, can happen if elementFlags is serialized/deserialized with incompatible DDT versions  
			return DeeImages.getIDEInternalErrorImageDescriptor();
		}
		
		ImageDescriptor baseImage = getBaseImageDescriptor(elementDesc);
		
		EProtection prot = DefElementFlagsUtil.elementFlagsToProtection(elementFlags, null);
			
		if(elementDesc.getArcheType() == EArcheType.Constructor) {
			// This is to prevent drawing the constructor 'C' adornment
			elementDesc.setArcheType(DefElementFlagConstants.FLAG_KIND_FUNCTION);
		}
		
		return getDecoratedImageDescriptor(baseImage, elementDesc, prot, imageSize);
	}
	
	protected ImageDescriptor getBaseImageDescriptor(DefElementDescriptor elementDesc) {
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
			return DeeImages.ENT_VARIABLE.getDescriptor();
			
		case Function:
			return DeeImages.ENT_FUNCTION.getDescriptor();
		case Constructor:
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
		case Error:
			return DeeImages.ENT_ERROR.getDescriptor();
		}
		
		throw assertFail();
		
	}
	
	/* ----------------- header ----------------- */
	
	protected static LangElementImageDescriptor getDecoratedImageDescriptor(ImageDescriptor baseImage, 
			DefElementDescriptor elementDesc, EProtection prot, Point size) {
		
		EnumSet<EAttributeFlag> flagsSet = ElementAttributes.newFlagsSet();
		
		if(elementDesc.isOverride()) {
			// Don't add because icon is ugly combined with function icon
		}
		
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_STATIC)) {
			flagsSet.add(EAttributeFlag.STATIC);
		}
		
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_FINAL)) {
			flagsSet.add(EAttributeFlag.FINAL);
		} else if(elementDesc.isFlag(DefElementFlagConstants.FLAG_ABSTRACT)) {
			flagsSet.add(EAttributeFlag.ABSTRACT);
		}
		
		if(elementDesc.isImmutable()) {
			flagsSet.add(EAttributeFlag.IMMUTABLE);
		} else if(elementDesc.isConst()) {
			flagsSet.add(EAttributeFlag.CONST);
		}
		
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_TEMPLATED)) {
			flagsSet.add(EAttributeFlag.TEMPLATED);
		}
		
		if(elementDesc.getArcheType() == EArcheType.Alias) {
			flagsSet.add(EAttributeFlag.ALIAS);
		}
		
		ElementAttributes elementData = new ElementAttributes(prot, flagsSet);
		
		return new DeeDecoratedImageDescriptor(size, baseImage, elementData);
	}
	
}