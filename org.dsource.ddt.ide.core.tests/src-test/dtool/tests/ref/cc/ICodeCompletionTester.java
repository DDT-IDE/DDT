package dtool.tests.ref.cc;

import org.eclipse.dltk.core.ModelException;

public interface ICodeCompletionTester {
	
	void testComputeProposalsWithRepLen(int repOffset, int prefixLen, int repLen,
			boolean removeObjectIntrinsics, String... expectedProposals) throws ModelException;
	
	void runAfters();
	
}