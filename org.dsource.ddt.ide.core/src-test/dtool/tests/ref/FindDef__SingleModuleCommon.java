package dtool.tests.ref;

import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;

public class FindDef__SingleModuleCommon extends FindDef__Common {

	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC_REFS;

	protected void prepTestModule(String testfile) throws CoreException {
		sourceModule = parseNeoModuleNode(TEST_SRCFOLDER +"/"+ testfile); 
		targetModule = null;
	}

}