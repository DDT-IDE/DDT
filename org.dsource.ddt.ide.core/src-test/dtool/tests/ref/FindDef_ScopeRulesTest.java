package dtool.tests.ref;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ModelException;
import org.junit.Before;
import org.junit.Test;

public class FindDef_ScopeRulesTest extends FindDef__Common  {
	
	static final String testfile = "refScopes.d";
	
	@Before
	public void prepTest() throws CoreException {
		prepSameModuleTest(testdataRefsPath(testfile));
	}
	
	protected void doTestFindref(String refMarker, String targetMarker) throws ModelException {
		testFindRef(sourceModule, getMarkerEndOffset(refMarker), sourceModule, getMarkerEndOffset(targetMarker));
	}
	
	@Test 
	public void test1() throws CoreException {
		doTestFindref("/+R1@+/", "/+T1@+/");
	}
	
	@Test 
	public void test2() throws CoreException {
		doTestFindref("/+R2@+/", "/+T1@+/");
	}
	
	@Test 
	public void test3() throws CoreException {
		doTestFindref("/+R3@+/", "/+T1@+/");
	}
	
	@Test 
	public void test4() throws CoreException {
		doTestFindref("/+R4@+/", "/+T3@+/");
	}
	
	@Test 
	public void test5() throws CoreException {
		doTestFindref("/+R5@+/", "/+T5@+/");
	}
	
	@Test 
	public void test6() throws CoreException {
		doTestFindref("/+R6@+/", "/+T3@+/");
	}
	
	@Test 
	public void test7() throws CoreException {
		doTestFindref("/+R7@+/", "/+T7@+/");
	}
	
	@Test 
	public void test8() throws CoreException {
		doTestFindref("/+R8@+/", "/+T8@+/");
	}
	
	@Test 
	public void test8b() throws CoreException {
		doTestFindref("/+R8b@+/", "/+T8b@+/");
	}
	
	@Test 
	public void test9() throws CoreException {
		doTestFindref("/+R9@+/", "/+T7@+/");
	}
	
	
	@Test 
	public void test10() throws CoreException {
		doTestFindref("/+R10@+/", "/+T10@+/");
	}
	
	@Test 
	public void testE() throws CoreException {
		doTestFindref("/+REa@+/", "/+TEa@+/");
		doTestFindref("/+REb@+/", "/+TEb@+/");
	}
	
}