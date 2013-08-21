package dtool.tests.ref.cc;

import org.eclipse.dltk.core.ModelException;

public interface ICodeCompletionTester {
	
	void testComputeProposalsWithRepLen(int repOffset, int prefixLen, int repLen,
			String... expectedProposals) throws ModelException;
	
	void runAfters();
	
}