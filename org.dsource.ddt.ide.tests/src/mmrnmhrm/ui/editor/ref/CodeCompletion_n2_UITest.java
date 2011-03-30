package mmrnmhrm.ui.editor.ref;

import mmrnmhrm.tests.ui.SWTTestUtils;

import org.eclipse.ui.PlatformUI;
import org.junit.After;

import dtool.tests.ref.cc.CodeCompletion_n2Test;

public class CodeCompletion_n2_UITest extends CodeCompletion_n2Test {
	
	public CodeCompletion_n2_UITest() {
		ccTester = new CodeCompletionUITestAdapter(file);
	}
	
	@After
	public void clearEventQueue() throws Throwable {
		SWTTestUtils.runEventQueueUntilEmpty(PlatformUI.getWorkbench().getDisplay());
	}
	
}
