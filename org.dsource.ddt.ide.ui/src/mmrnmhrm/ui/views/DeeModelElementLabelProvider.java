package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.DeePluginImages;

import org.dsource.ddt.ide.core.model.DeeModelElementUtil;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
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
		if(object instanceof IMember) {
			IMember member = (IMember) object;
			
			int flags;
			try {
				flags = member.getFlags();
			} catch (ModelException e) {
				// TODO throw instead?
				DeeCore.log(e);
				flags = 0; // Ignore, use empty flags
			}
			
			EArcheType archeType = DeeModelElementUtil.elementFlagsToArcheType(member, flags);
			if(archeType == null) {
				return null;
			}
			
			switch (archeType) {
			case Module:
				return DeePluginImages.getManagedImage(DeePluginImages.NODE_MODULE_DEC);
			case Package:
				return DeePluginImages.getManagedImage(DeePluginImages.ELEM_PACKAGE);
				
			case Variable:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_VARIABLE);
			case Function:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_FUNCTION);
				
			case Class:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_CLASS);
			case Interface:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_INTERFACE);
			case Struct:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_STRUCT);
			case Union:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_UNION);
				
			case Template:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_TEMPLATE);
			case Enum:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_ENUM);
			case Alias:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_ALIAS);
			case Typedef:
				return DeePluginImages.getManagedImage(DeePluginImages.ENT_TYPEDEF);
			default:
				throw assertFail();
			}
			
		} else {
			return null;
		}
	}
	
}
