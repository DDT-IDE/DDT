package mmrnmhrm.org.eclipse.dltk.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;


/**
 * Wraps a <code>ModelElementSearchActions</code> to find its results
 * in the specified working set.
 * <p>
 * The action is applicable to selections and Search view entries
 * representing a Script element.
 * 
 * <p>
 * Note: This class is for internal use only. Clients should not use this class.
 * </p>
 * 
	 *
 */
public class WorkingSetFindAction extends FindAction {

	private FindAction fAction;	
	IDLTKLanguageToolkit toolkit;

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 */
	public WorkingSetFindAction(ScriptEditor editor, FindAction action, String workingSetName) {
		super(editor);
		init(action, workingSetName);
		toolkit = action.getLanguageToolkit();
	}

	Class[] getValidTypes() {
		return null; // ignore, we override canOperateOn
	}
	
	@Override
	void init() {
		// ignore: do our own init in 'init(FindAction, String)'
	}
	
	private void init(FindAction action, String workingSetName) {
		Assert.isNotNull(action);
		fAction= action;
		setText(workingSetName);
		setImageDescriptor(action.getImageDescriptor());
		setToolTipText(action.getToolTipText());
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.WORKING_SET_FIND_ACTION);		
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add help support here...");
		}
	}
	
	@Override
	public void run() {
		fAction.run();
	}
	
	/*boolean canOperateOn(IModelElement element) {
		return fAction.canOperateOn(element);
	}*/

	@Override
	protected int getLimitTo() {
		return -1;
	}

	@Override
	String getOperationUnavailableMessage() {
		return fAction.getOperationUnavailableMessage();
	}

	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return toolkit;
	}	
}
