package mmrnmhrm.core.launch.debug;

public interface IDebuggerHandler {
	
	void commandStartSession();
	
	void commandSuspend();
	
	void commandResume();

	void dispose();
	
}
