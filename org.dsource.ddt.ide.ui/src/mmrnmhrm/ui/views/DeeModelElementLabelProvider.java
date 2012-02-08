package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.DeePluginImages;

import org.dsource.ddt.ide.core.model.DeeModelElementUtil;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
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
			
			// XXX: Due to a DLTK limitation we don't know if what image size is preferred. 
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
		int imageFlags = 0;
		try {
			elementFlags = member.getFlags();
			imageFlags = getImageFlags(member, elementFlags);
		} catch (ModelException e) {
			// Ignore, use default flags
			// TODO throw instead?
			DeeCore.log(e);
		}
		
		ImageDescriptor baseImage = getBaseImageDescriptor(member, elementFlags);
		return new DeeElementImageDescriptor(baseImage, imageFlags, imageSize);
	}
	
	protected ImageDescriptor getBaseImageDescriptor(IMember member, int flags) {
		EArcheType archeType = DeeModelElementUtil.elementFlagsToArcheType(member, flags);
		if(archeType == null) {
			return null;
		}
		
		switch (archeType) {
		case Package:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ELEM_PACKAGE);
		case Module:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.NODE_MODULE_DEC);
			
		case Variable:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_VARIABLE);
		case Function:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_FUNCTION);
			
		case Class:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_CLASS);
		case Interface:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_INTERFACE);
		case Struct:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_STRUCT);
		case Union:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_UNION);
			
		case Template:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_TEMPLATE);
		case Enum:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_ENUM);
		case Alias:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_ALIAS);
		case Typedef:
			return DeePluginImages.getManagedDescriptor(DeePluginImages.ENT_TYPEDEF);
		default:
			throw assertFail();
		}
	}
	
	protected int getImageFlags(IMember member, int modifiers) throws ModelException {
		int flags = 0;
		
		if (member.getElementType() == IModelElement.METHOD && ((IMethod) member).isConstructor()) {
			flags |= ScriptElementImageDescriptor.CONSTRUCTOR; // TODO: this should be it's own base image
		}

		IType declaringType = member.getDeclaringType();
		boolean isInterface = declaringType != null && Flags.isInterface(declaringType.getFlags());

		if (Flags.isAbstract(modifiers) && !isInterface)
			flags |= ScriptElementImageDescriptor.ABSTRACT;
		if (Flags.isFinal(modifiers))
			flags |= ScriptElementImageDescriptor.FINAL;
		if (Flags.isStatic(modifiers))
			flags |= ScriptElementImageDescriptor.STATIC;
		
		// TODO: add decorators for protection attributes
		
		return flags;
	}
	
}
