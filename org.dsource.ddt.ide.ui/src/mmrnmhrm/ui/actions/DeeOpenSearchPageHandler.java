package mmrnmhrm.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeeOpenSearchPageHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DeeOpenSearchPageAction action = new DeeOpenSearchPageAction();
		action.init(HandlerUtil.getActiveWorkbenchWindow(event));
		action.run(null);
		return null;
	}
	
}
