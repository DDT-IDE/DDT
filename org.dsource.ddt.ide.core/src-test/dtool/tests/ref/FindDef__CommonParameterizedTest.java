package dtool.tests.ref;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

public abstract class FindDef__CommonParameterizedTest extends FindDef__Common {
	
	public FindDef__CommonParameterizedTest(int offset, int targetOffset, String testfile) throws IOException, CoreException {
		this.offset = offset;
		this.targetOffset = targetOffset;
		prepSameModuleTest(testdataRefsPath(testfile));
	}
	
	@Test
	public void test() throws ModelException {
		assertFindReF(sourceModule, offset, sourceModule, targetOffset);
	}
	
}