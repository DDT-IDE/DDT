package mmrnmhrm.tests.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;


public class SWTTestUtils {
	
	/** Runs the event queue executing all events that were pending when this method was called. 
	 * Note BM: This works under the assumption that {@link Display#asyncExec(Runnable)} puts the given runnable 
	 * at the end of the queue */
	public static void runPendingUIEvents(Display display) {
		assertEquals(display, Display.getCurrent());
		final boolean[] result = new boolean[1];
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				result[0] = true;
			}
		});
		while (result[0] == false) {
			display.readAndDispatch();
		}
	}
	
	/** Runs the event queue until there are no events in the queue. 
	 * Note that it is possible that this method might not return, if new events keep being added to the queue. */
	public static void runEventQueueUntilEmpty(Display display) {
		assertEquals(display, Display.getCurrent());
		while (display.readAndDispatch()) {
		}
	}

	public static void clearEventQueue() {
		runEventQueueUntilEmpty(PlatformUI.getWorkbench().getDisplay());
	}

	public static void ________________clearEventQueue________________() {
		clearEventQueue();
	}
	
}
