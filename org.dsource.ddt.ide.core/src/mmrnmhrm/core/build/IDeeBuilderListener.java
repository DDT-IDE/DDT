package mmrnmhrm.core.build;

public interface IDeeBuilderListener {
	
	void buildCommandsCreated(String buildCommands);
	
	void processAboutToStart(String[] cmdLine);
	
	void handleProcessOutputLine(String line);
	
}
