package dtool.tests.ref.inter;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import dtool.tests.ref.FindDef__Common;

public abstract class FindDef__ImportsCommon extends FindDef__Common {
	
	public static final String TEST_SRCFOLDER = ITestResourcesConstants.TR_SAMPLE_SRC3;
	
	protected static ParseSource getTestModule(String path) throws CoreException {
		return parseTestModule(SampleMainProject.getSourceModule(TEST_SRCFOLDER +"/"+ path));
	}
	
	protected static ParseSource defaultModule;
	
	protected static void setupDefault(String testSrcFile) {
		try {
			defaultModule = getTestModule(testSrcFile);
		} catch (CoreException ce) {
			ExceptionAdapter.unchecked(ce);
		}
	}
	
	public FindDef__ImportsCommon(int offset, int targetOffset, String targetFile) throws CoreException {
		this((ParseSource) null, offset, targetOffset, targetFile);
	}
	
	public FindDef__ImportsCommon(String srcFile, int defOffset, int refOffset, String targetFile) 
			throws Exception {
		this(getTestModule(srcFile), defOffset, refOffset, targetFile);
	}
	
	public FindDef__ImportsCommon(ParseSource newModule, int defOffset, int refOffset, String targetFile) 
			throws CoreException {
		this.offset = defOffset;
		this.targetOffset = refOffset;
		sourceModule = newModule == null ? defaultModule : newModule;
		targetModule = targetFile == null ? null : getTestModule(targetFile).module;
	}
	
	@Test
	public void test() throws Exception {
		testFindRefWithConfiguredValues();
	}
	
}