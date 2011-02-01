package mmrnmhrm.core.build;

public class CommonDeeBuilderListener implements IDeeBuilderListener {
	
	@Override
	public void buildCommandsCreated(String buildCommands) {
		// Default implementation: do nothing
	}
	
	@Override
	public void handleProcessOutputLine(String line) {
		// Default implementation: do nothing
	}
	
	@Override
	public void processAboutToStart(String[] cmdLine) {
		// Default implementation: do nothing
	}
	
}