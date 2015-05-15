package mmrnmhrm.core.engine;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.IProblemRequestor;

public class NullProblemRequestor implements IProblemRequestor {
	@Override
	public boolean isActive() {
		return false;
	}
	
	@Override
	public void endReporting() {
	}
	
	@Override
	public void beginReporting() {
	}
	
	@Override
	public void acceptProblem(IProblem problem) {
	}
}