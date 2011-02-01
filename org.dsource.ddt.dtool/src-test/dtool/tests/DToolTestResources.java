package dtool.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;



public class DToolTestResources {
	
	protected static final String D_TOOL_TEST_RESOURCES_BASE_DIR = DToolBaseTest.DTOOL_PREFIX + "TestResourcesDir";
	protected static final String D_TOOL_TEST_RESOURCES_WORKING_DIR = DToolBaseTest.DTOOL_PREFIX + "TestsWorkingDir";
	
	private static final String TESTDATA = "testdata/";
	
	protected static DToolTestResources instance;
	
	private String testResourcesDir;
	private String testsWorkingDir;
	
	public DToolTestResources() {
		this(System.getProperty(D_TOOL_TEST_RESOURCES_WORKING_DIR));
	}
	
	public DToolTestResources(String workingDir) {
		testResourcesDir = System.getProperty(D_TOOL_TEST_RESOURCES_BASE_DIR);
		if(testResourcesDir == null) {
			testResourcesDir = TESTDATA; // Assume a default based on process working dir
		}

		this.testsWorkingDir = workingDir;
		System.out.println("====>> WORKING DIR: " + testsWorkingDir);
		
		if(testsWorkingDir != null) {
			File file = new File(testsWorkingDir);
			if(!file.exists()) {
				file.mkdir();
			}
		}
	}
	
	protected static void initialize(String workingDir) {
		assertTrue(instance == null);
		instance = new DToolTestResources(workingDir);
	}
	
	public static synchronized DToolTestResources getInstance() {
		if(instance == null) {
			instance = new DToolTestResources();
		}
		return instance;
	}
	
	
	public File getResourcesDir() {
		File file = new File(testResourcesDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
	public static File getTestResource(String fileRelPath) {
		return new File(DToolTestResources.getInstance().getResourcesDir(), fileRelPath);
	}
	
	public File getWorkingDir() {
		assertNotNull(testsWorkingDir); // Maybe use workingDir = "_runtime-tests" instead
		File file = new File(testsWorkingDir);
		assertTrue(file.exists() && file.isDirectory());
		return file;
	}
	
}
