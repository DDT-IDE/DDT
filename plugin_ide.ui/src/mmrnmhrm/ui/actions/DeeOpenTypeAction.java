package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUILanguageToolkit;
import mmrnmhrm.ui.OpenTypeSelectionDialog2_Ext;

import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.actions.OpenTypeAction;
import org.eclipse.dltk.ui.dialogs.TypeSelectionExtension;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

/*FIXME: repurpose this operation */
public class DeeOpenTypeAction extends OpenTypeAction {
	
	@Override
	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}
	
	@Override
	protected String getOpenTypeDialogTitle() {
		return "Open D Type";
	}
	
	@Override
	protected String getOpenTypeDialogMessage() {
		return "&Select a type element/symbol to open (? = any character, * = any String, TZ = TimeZone):";
	}
	
	@Override
	protected String getOpenTypeErrorTitle() {
		return getOpenTypeDialogTitle();
	}
	
	@Override
	protected String getOpenTypeErrorMessage() {
		return "An exception occurred while opening the element/symbol.";
	}
	
	@Override
	protected SelectionDialog createDialog() {
		OpenTypeSelectionDialog2_Ext dialog = new OpenTypeSelectionDialog2_Ext(DLTKUIPlugin.getActiveWorkbenchShell(),
				true, PlatformUI.getWorkbench().getProgressService(), null, 
				IDLTKSearchConstants.TYPE, new DeeTypeSelectionExtension(), this.getUILanguageToolkit());
		dialog.setTitle(getOpenTypeDialogTitle());
		dialog.setMessage(getOpenTypeDialogMessage());
		return dialog;
	}
	
	protected final class DeeTypeSelectionExtension extends TypeSelectionExtension {
//		final DeeModelElementLabelProvider deeModelElementLabelProvider = new DeeModelElementLabelProvider();
		
//		@Override
//		public ITypeInfoImageProvider getImageProvider() {
//			return new ITypeInfoImageProvider() {
//				@Override
//				public ImageDescriptor getImageDescriptor(ITypeInfoRequestor typeInfoRequestor) {
//					return deeModelElementLabelProvider.getImageDescriptor(
//						new DefElementDescriptor(typeInfoRequestor.getModifiers()),
//						LangElementImageDescriptor.DEFAULT_SIZE);
//				}
//			};
//		}
	}
	
}