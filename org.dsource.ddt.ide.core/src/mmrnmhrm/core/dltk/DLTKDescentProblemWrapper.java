package mmrnmhrm.core.dltk;

import descent.core.compiler.IProblem;

final class DLTKDescentProblemWrapper implements
org.eclipse.dltk.compiler.problem.IProblem {
	
	private IProblem problem;
	
	public DLTKDescentProblemWrapper(IProblem problem) {
		this.problem = problem;
	}
	
	@Override
	public String[] getArguments() {
		return problem.getArguments();
	}
	
	@Override
	public int getID() {
		return problem.getID();
	}
	
	@Override
	public String getMessage() {
		return problem.getMessage();
	}
	
	@Override
	public String getOriginatingFileName() {
		return new String(problem.getOriginatingFileName());
	}
	
	@Override
	public int getSourceEnd() {
		return problem.getSourceEnd();
	}
	
	@Override
	public int getSourceLineNumber() {
		return problem.getSourceLineNumber();
	}
	
	@Override
	public int getSourceStart() {
		return problem.getSourceStart();
	}
	
	@Override
	public boolean isError() {
		return problem.isError();
	}
	
	@Override
	public boolean isWarning() {
		return problem.isWarning();
	}
	
	@Override
	public void setSourceEnd(int sourceEnd) {
		problem.setSourceEnd(sourceEnd);
	}
	
	@Override
	public void setSourceLineNumber(int lineNumber) {
		problem.setSourceLineNumber(lineNumber);
	}
	
	@Override
	public void setSourceStart(int sourceStart) {
		problem.setSourceStart(sourceStart);
	}
}