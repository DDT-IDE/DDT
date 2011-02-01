package dtool.tests.ref;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

public abstract class FindDef__SingleModuleCommonP extends FindDef__SingleModuleCommon {
	
	public FindDef__SingleModuleCommonP(int offset, int targetOffset, String testfile) throws IOException, CoreException {
		this.offset = offset;
		this.targetOffset = targetOffset;
		prepTestModule(testfile);	
	}

	@Test
	public void test() throws ModelException {
		assertFindReF(sourceModule, offset, sourceModule, targetOffset);
	}

}