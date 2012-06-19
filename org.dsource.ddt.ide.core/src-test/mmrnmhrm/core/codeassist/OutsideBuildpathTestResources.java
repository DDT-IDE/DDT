package mmrnmhrm.core.codeassist;

import mmrnmhrm.tests.ITestResourcesConstants;

public interface OutsideBuildpathTestResources {
	
	static final String TEST_SRCFILE = ITestResourcesConstants.TR_SAMPLE_SRC1 + "/testFindDefOp.d";
	static final String TEST_SRC_TARGETFILE = ITestResourcesConstants.TR_SAMPLE_SRC3 +"/pack/sample.d";
	static final String TEST_NONDEEPROJ_FILE = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL + "/testFindDefOp.d";
	static final String TEST_OUTFILE = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL + "/testFindDef_Out.d";
	static final String TEST_OUTFILE2 = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL + "/pck/testFindDef_Out.d";
	static final String TEST_OUTFILE3 = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL + "/pck2/testFindDef_Out.d";
	
}