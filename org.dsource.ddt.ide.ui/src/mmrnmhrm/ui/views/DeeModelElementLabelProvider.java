package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.ui.DeePluginImages;

import org.dsource.ddt.ide.core.model.DeeModelElementUtil;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import dtool.ast.definitions.EArcheType;

// Compare with ScriptUILabelProvider, the later has more capabilites (like flags) and different image sizes 
// TODO: need to talk with DTLK guys because of the above
public class DeeModelElementLabelProvider extends LabelProvider implements ILabelProvider {
	
	public DeeModelElementLabelProvider() {
	}
	
	@Override
	public String getText(Object object) {
		return null;
	}
	
	@Override
	public Image getImage(Object object) {
		if(object instanceof IModelElement) {
			IModelElement element = (IModelElement) object;
			
			EArcheType archeType = DeeModelElementUtil.elementFlagsToArcheType(element);
			if(archeType == null) {
				return null;
			}
			
			switch (archeType) {
			case Module:
				return DeePluginImages.getImage(DeePluginImages.NODE_MODULE_DEC);
			case Package:
				return DeePluginImages.getImage(DeePluginImages.ELEM_PACKAGE);
				
			case Variable:
				return DeePluginImages.getImage(DeePluginImages.ENT_VARIABLE);
			case Function:
				return DeePluginImages.getImage(DeePluginImages.ENT_FUNCTION);
				
			case Class:
				return DeePluginImages.getImage(DeePluginImages.ENT_CLASS);
			case Interface:
				return DeePluginImages.getImage(DeePluginImages.ENT_INTERFACE);
			case Struct:
				return DeePluginImages.getImage(DeePluginImages.ENT_STRUCT);
			case Union:
				return DeePluginImages.getImage(DeePluginImages.ENT_UNION);
				
			case Template:
				return DeePluginImages.getImage(DeePluginImages.ENT_TEMPLATE);
			case Enum:
				return DeePluginImages.getImage(DeePluginImages.ENT_ENUM);
			case Alias:
				return DeePluginImages.getImage(DeePluginImages.ENT_ALIAS);
			case Typedef:
				return DeePluginImages.getImage(DeePluginImages.ENT_TYPEDEF);
			default:
				throw assertFail();
			}
			
		} else {
			return null;
		}
	}
	
}
