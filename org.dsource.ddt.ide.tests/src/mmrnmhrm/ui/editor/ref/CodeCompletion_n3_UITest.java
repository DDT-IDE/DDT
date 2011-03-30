package mmrnmhrm.ui.editor.ref;

import mmrnmhrm.tests.ui.SWTTestUtils;

import org.eclipse.ui.PlatformUI;
import org.junit.After;

import dtool.tests.ref.cc.CodeCompletion_n3Test;

public class CodeCompletion_n3_UITest extends CodeCompletion_n3Test {
	
	public CodeCompletion_n3_UITest() {
		ccTester = new CodeCompletionUITestAdapter(file);
	}
	
	@After
	public void uiAfters() throws Throwable {
		//ccTester.runAfters();
		SWTTestUtils.runEventQueueUntilEmpty(PlatformUI.getWorkbench().getDisplay());
	}
}
