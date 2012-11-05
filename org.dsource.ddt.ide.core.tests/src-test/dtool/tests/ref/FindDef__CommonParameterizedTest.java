package dtool.tests.ref;

import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

public abstract class FindDef__CommonParameterizedTest extends FindDef__Common {
	
	public FindDef__CommonParameterizedTest(int offset, int targetOffset, String testfile) {
		this.offset = offset;
		this.targetOffset = targetOffset;
		prepSameModuleTest(testdataRefsPath(testfile));
	}
	
	@Test
	public void test() throws ModelException {
		testFindRefWithConfiguredValues();
	}
	
}