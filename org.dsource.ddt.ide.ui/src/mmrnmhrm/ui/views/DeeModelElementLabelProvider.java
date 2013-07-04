package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.DeeUIPreferenceConstants;
import mmrnmhrm.ui.DeeUIPreferenceConstants.ElementIconsStyle;

import org.dsource.ddt.ide.core.model.DeeModelElementUtil;
import org.dsource.ddt.ide.core.model.ProtectionAttribute;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import dtool.ast.definitions.EArcheType;

public class DeeModelElementLabelProvider extends LabelProvider implements ILabelProvider {
	
	public static final Point SMALL_SIZE= new Point(16, 16);
	public static final Point BIG_SIZE= new Point(22, 16);
	
	public static ImageDescriptor getIDEInternalErrorImageDescriptor() {
		// BM: maybe there's some other image we could use
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	public DeeModelElementLabelProvider() {
	}
	
	@Override
	public String getText(Object object) {
		return null; // Use DLTK defaults
	}
	
	public ImageDescriptorRegistry getImageDescriptorRegistry() {
		return DeePluginImages.getImageDescriptorRegistry();
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof IMember) {
			IMember member = (IMember) object;
			
			// XXX: Due to a DLTK limitation we don't know what image size is preferred. 
			// BM: so we do this awful hack to try to figure it out, 
			// I'm particularly concerned about performance, but since it is UI elements code, it should be
			// called a limited number of times 
			
			Point imageSize = BIG_SIZE;
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < 5; i++) {
				if(stackTrace[i].getClassName().startsWith(
						"org.eclipse.dltk.internal.ui.navigator.ScriptExplorerLabelProvider")) {
					imageSize = SMALL_SIZE; // Use small size for the Script Explorer
				}
			}
			
			ImageDescriptor imageDescriptor = getImageDescriptor(member, imageSize);
			
			return getImageDescriptorRegistry().get(imageDescriptor);
		} else {
			return null;
		}
	}
	
	public ImageDescriptor getImageDescriptor(IMember member, Point imageSize) {
		
		int elementFlags = 0;
		try {
			elementFlags = member.getFlags();
		} catch (ModelException e) {
			DeeCore.logError(e);
			return getIDEInternalErrorImageDescriptor();
		}
//		// correct element flags in case it is used by some fake ModelElement
//		elementFlags = DeeModelElementUtil.getCorrectedElementFlags(member, elementFlags);
		return getImageDescriptor(elementFlags, imageSize);
	}
	
	public ImageDescriptor getImageDescriptor(int elementFlags, Point imageSize) {
		EArcheType archetype = DeeModelElementUtil.elementFlagsToArcheType(elementFlags);
		if(archetype == null) {
			// archetype can be null if elementFlags is somehow wrongly created
			// for example, can happen if elementFlags is serialized/deserialized with incompatible DDT versions  
			return getIDEInternalErrorImageDescriptor();
		}
		
		ElementIconsStyle iconStyle = getIconStylePreference();
		ImageDescriptor baseImage = getBaseImageDescriptor(archetype, elementFlags, iconStyle);
		
		ProtectionAttribute prot = null;
		if (iconStyle != ElementIconsStyle.JDTLIKE || 
				(archetype != EArcheType.Variable && archetype != EArcheType.Function)) {
			prot = DeeModelElementUtil.elementFlagsToProtection(elementFlags, null);
		}
		
		int imageFlags = getImageAdornmentFlags(elementFlags);
		return new DeeElementImageDescriptor(baseImage, imageFlags, prot, imageSize);
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
	
	protected ElementIconsStyle getIconStylePreference() {
		String iconStyleStr = DeePlugin.getPrefStore().getString(DeeUIPreferenceConstants.ELEMENT_ICONS_STYLE);
		return ElementIconsStyle.create(iconStyleStr, ElementIconsStyle.DDT);
	}
	
	protected ImageDescriptor getBaseImageDescriptor(EArcheType archeType, int flags, ElementIconsStyle iconStyle) {
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
		case EnumMember:
		case Tuple:
		case TypeParameter:
			throw assertFail(); //The above are not reported as elements
		}
		throw assertFail();
		
	}

	public ImageDescriptor getJDTStyleFieldImageDescriptor(int flags) {
		switch (DeeModelElementUtil.elementFlagsToProtection(flags, ProtectionAttribute.PUBLIC)) {
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
		switch (DeeModelElementUtil.elementFlagsToProtection(flags, ProtectionAttribute.PUBLIC)) {
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
	
}