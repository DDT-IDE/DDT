package dtool.tests.ref;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FindDef_ScopeRulesTest extends FindDef__SingleModuleCommon  {
	
	static final String testfile = "refScopes.d";
	
	@BeforeClass
	public static void commonSetup() {
		staticTestInit(testfile);
	}
	
	@Before
	public void prepTest() throws CoreException {
		prepTestModule(testfile);
	}
	
	@Test public void test1() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R1@+/"), 
				sourceModule, getMarkerEndOffset("/+T1@+/"));
	}
	
	@Test public void test2() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R2@+/"), 
				sourceModule, getMarkerEndOffset("/+T1@+/"));
	}
	
	@Test public void test3() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R3@+/"), 
				sourceModule, getMarkerEndOffset("/+T1@+/"));
	}

	@Test public void test4() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R4@+/"),
				sourceModule, getMarkerEndOffset("/+T3@+/"));
	}
	
	@Test public void test5() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R5@+/"), 
				sourceModule, getMarkerEndOffset("/+T5@+/"));
	}
	
	@Test public void test6() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R6@+/"), 
				sourceModule, getMarkerEndOffset("/+T3@+/"));
	}
	
	@Test public void test7() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R7@+/"), 
				sourceModule, getMarkerEndOffset("/+T7@+/"));
	}
	
	@Test public void test8() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R8@+/"), 
				sourceModule, getMarkerEndOffset("/+T8@+/"));
	}
	
	@Test public void test8b() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R8b@+/"), 
				sourceModule, getMarkerEndOffset("/+T8b@+/"));
	}
	
	@Test public void test9() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R9@+/"), 
				sourceModule, getMarkerEndOffset("/+T7@+/"));
	}
	
	
	@Test public void test10() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+R10@+/"), 
				sourceModule, getMarkerEndOffset("/+T10@+/"));
	}
	
	@Test public void testE() throws CoreException {
		assertFindReF(sourceModule, getMarkerEndOffset("/+REa@+/"), 
				sourceModule, getMarkerEndOffset("/+TEa@+/"));
		assertFindReF(sourceModule, getMarkerEndOffset("/+REb@+/"), 
				sourceModule, getMarkerEndOffset("/+TEb@+/"));
	}
}
