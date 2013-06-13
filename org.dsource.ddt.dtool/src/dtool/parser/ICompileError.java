package dtool.parser;

public interface ICompileError {

	String getUserMessage();

	int getStartPos();

	int getEndPos();

	int getLineNumber();
	
}
