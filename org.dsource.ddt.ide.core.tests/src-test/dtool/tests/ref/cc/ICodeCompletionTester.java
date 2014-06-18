package dtool.tests.ref.cc;

import org.eclipse.core.runtime.CoreException;

public interface ICodeCompletionTester {
	
	void testComputeProposalsWithRepLen(int repOffset, int prefixLen, int repLen,
			String... expectedProposals) throws CoreException;
	
	void runAfters();
	
}