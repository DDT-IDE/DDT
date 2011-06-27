package mmrnmhrm.core.parser;

import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;

final class DLTKDescentProblemWrapper {
	
	public static DefaultProblem createProblemWrapper(descent.core.compiler.IProblem problem) {
		return new DefaultProblem(new String(problem.getOriginatingFileName()),
				problem.getMessage(), DefaultProblemIdentifier.decode(IProblem.Syntax),
				problem.getArguments(), ProblemSeverities.Error,
				problem.getSourceStart(), problem.getSourceEnd(), problem.getSourceLineNumber(), 0);
	}
	
}