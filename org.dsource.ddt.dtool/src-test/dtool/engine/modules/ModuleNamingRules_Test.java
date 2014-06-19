package dtool.engine.modules;

import static dtool.engine.modules.ModuleNamingRules.getDefaultModuleNameFromFileName;
import static dtool.engine.modules.ModuleNamingRules.getModuleFullName;
import static dtool.engine.modules.ModuleNamingRules.isValidCompilationUnitName;
import static dtool.engine.modules.ModuleNamingRules.isValidPackagePathName;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tests.CommonTest;

import org.junit.Test;

import dtool.engine.modules.ModuleFullName;

public class ModuleNamingRules_Test extends CommonTest {
	
	@Test
	public void test_isValidCompilationUnitName() throws Exception { test_isValidCompilationUnitName$(); }
	public void test_isValidCompilationUnitName$() throws Exception {
		assertTrue(isValidCompilationUnitName("foo.d"));
		assertTrue(isValidCompilationUnitName("bar321.d"));
		assertTrue(isValidCompilationUnitName("_bar.d"));
		assertTrue(isValidCompilationUnitName("_foo_bar.d"));
		assertTrue(isValidCompilationUnitName("foo_bar.d"));
		assertTrue(isValidCompilationUnitName("Açores.d"));
		assertTrue(isValidCompilationUnitName("Солярис.di"));
		
		assertTrue(!isValidCompilationUnitName(""));
		assertTrue(!isValidCompilationUnitName("foo"));
		assertTrue(!isValidCompilationUnitName("123foo.d"));
		assertTrue(!isValidCompilationUnitName("bar.foo.d"));
		assertTrue(!isValidCompilationUnitName("bar foo.d"));
		assertTrue(!isValidCompilationUnitName("bar-foo.d"));
		assertTrue(!isValidCompilationUnitName("Açores.txt"));
		
		assertTrue(isValidPackagePathName(""));
		assertTrue(isValidPackagePathName("foo"));
		assertTrue(isValidPackagePathName("foo/"));
		assertTrue(isValidPackagePathName("foo/bar"));
		assertTrue(isValidPackagePathName("foo/bar/"));
		
		assertTrue(!isValidPackagePathName("foo!/bar"));
		assertTrue(!isValidPackagePathName("foo/sub-pack"));
		
		
		// Test keywords - they are considered valid, for the moment (perhaps this could change?)
		assertTrue(!isValidCompilationUnitName("__FILE__.d"));
		assertTrue(!isValidCompilationUnitName("while.d"));
		assertTrue(!isValidCompilationUnitName("package.d"));
		
		assertTrue(!isValidPackagePathName("foo/while"));
		assertTrue(!isValidPackagePathName("package/bar"));
	}
	
	/* ----------------- module names ----------------- */
	
	@Test
	public void testGetModuleName() throws Exception { testGetModuleName$(); }
	public void testGetModuleName$() throws Exception {
		checkModuleName("mymod.d", "mymod");
		checkModuleName("mymod.di", "mymod");
		checkModuleName("/mymod.d", "mymod");
		
		checkModuleName("path/mymod.d", "path.mymod");
		checkModuleName("/path/mymod.d", "path.mymod");
		checkModuleName("foo/bar/mymod.d", "foo.bar.mymod");
		
		checkModuleName("foo/while/mymod.d", "foo.while.mymod", false);
		checkModuleName("foo/and bar/mymod.d", "foo.and bar.mymod", false);
		checkModuleName("foo/and;bar/mymod.d", "foo.and;bar.mymod", false);
		
		
		checkModuleName("foo/bar/while.d", "foo.bar.while", false);
		checkModuleName("foo/bar/and bar.d", "foo.bar.and bar", false);
		checkModuleName("foo/bar/and;bar.d", "foo.bar.and;bar", false);
		checkModuleName("", "", false);
		
		// Test separators in segments
		checkModuleName("foo/and.bar/mymod.d", true, "foo.and.bar.mymod", false);
		checkModuleName("foo/../mymod.d", true, "foo....mymod", false);
		checkModuleName("..", true, "", false);
		
		
		// Test irregular extensions: allow
		checkModuleName("mymod.dxx", "mymod", true); 
		checkModuleName("mymod.d.xx", "mymod", true);
		checkModuleName("mymod.xx.d", "mymod", true);
		checkModuleName("pack/mymod.d#blah", "pack.mymod", true);
		assertEquals(getDefaultModuleNameFromFileName("mymod.d"), "mymod");
		assertEquals(getDefaultModuleNameFromFileName("mymod"), "mymod");
		assertEquals(getDefaultModuleNameFromFileName("mymod.dx"), "mymod");
	}
	
	protected void checkModuleName(String filePath, String moduleFullNameStr) {
		checkModuleName(filePath, moduleFullNameStr, true);
	}
	
	protected void checkModuleName(String filePath, String moduleFullNameStr, boolean isValid) {
		boolean isLossyPath = false;
		checkModuleName(filePath, isLossyPath, moduleFullNameStr, isValid);
	}
	
	protected void checkModuleName(String filePath, boolean isLossyPath, String moduleFullNameStr, boolean isValid) {
		ModuleFullName moduleFullName = getModuleFullName(path(filePath));
		if(isLossyPath) {
			assertAreEqual(moduleFullName.getNameAsString(), moduleFullNameStr);
		} else {
			assertAreEqual(moduleFullName, new ModuleFullName(moduleFullNameStr));
		}
		assertAreEqual(moduleFullName.isValid(), isValid);
	}
	
}