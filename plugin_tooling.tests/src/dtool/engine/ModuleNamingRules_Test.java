package dtool.engine;

import static dtool.engine.modules.ModuleNamingRules.getDefaultModuleNameFromFileName;
import static dtool.engine.modules.ModuleNamingRules.isValidCompilationUnitName;
import static dtool.engine.modules.ModuleNamingRules.isValidPackagesPath;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.createValidPath;

import java.nio.file.Path;

import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tests.CommonTest;

import org.junit.Test;

import dtool.engine.modules.ModuleNamingRules;

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
		
		assertTrue(isValidPackagesPath(""));
		assertTrue(isValidPackagesPath("foo"));
		assertTrue(isValidPackagesPath("foo/"));
		assertTrue(isValidPackagesPath("foo/bar"));
		assertTrue(isValidPackagesPath("foo/bar/"));
		
		assertTrue(!isValidPackagesPath("foo!/bar"));
		assertTrue(!isValidPackagesPath("foo/sub-pack"));
		
		
		// Test keywords - they are considered valid, for the moment (perhaps this could change?)
		assertTrue(isValidCompilationUnitName("__FILE__.d") == false);
		assertTrue(isValidCompilationUnitName("while.d") == false);
		assertTrue(isValidCompilationUnitName("package.d") == false);
		
		assertTrue(isValidPackagesPath("foo/while") == false);
		assertTrue(isValidPackagesPath("package/bar") == false);
		
		assertEquals(getDefaultModuleNameFromFileName("mymod.d"), "mymod");
		assertEquals(getDefaultModuleNameFromFileName("mymod"), "mymod");
		assertEquals(getDefaultModuleNameFromFileName("mymod.dx"), "mymod");
		
		assertEqualArrays(StringUtil.splitString("", '.'), new String[] { "" });
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
		
		checkInvalidModuleName("foo/while/mymod.d");
		checkInvalidModuleName("foo/and bar/mymod.d");
		checkInvalidModuleName("foo/and;bar/mymod.d");
		
		
		checkInvalidModuleName("foo/bar/while.d");
		checkInvalidModuleName("foo/bar/and bar.d");
		checkInvalidModuleName("foo/bar/and;bar.d");
		checkInvalidModuleName("");
		checkInvalidModuleName(createValidPath(""));
		checkInvalidModuleName(createValidPath("/"));
		checkInvalidModuleName(createValidPath("D:/"));

		
		// Test package import rule
		checkModuleName("path/package.d", "path");
		checkModuleName("pack/foo/package.d", "pack.foo");
		checkModuleName("pack/foo/package.di", "pack.foo");
		checkInvalidModuleName("package.d");
		checkInvalidModuleName("/package.d");
		
		
		// Test separators in segments
		checkInvalidModuleName("foo/and.bar/mymod.d");
		checkInvalidModuleName("foo/../mymod.d");
		checkInvalidModuleName("..");
		
		
		// Test irregular extensions: invalid
		checkInvalidModuleName("mymod.dxx"); 
		checkInvalidModuleName("mymod.d.xx");
		checkInvalidModuleName("mymod.xx.d");
		checkInvalidModuleName("pack/mymod.d#blah");
	}
	
	protected void checkModuleName(String filePath, String moduleFullNameStr) {
		checkModuleName(moduleFullNameStr, MiscUtil.createValidPath(filePath));
	}
	
	protected void checkModuleName(String moduleFullNameStr, Path path) {
		ModuleFullName moduleFullName = ModuleNamingRules.getValidModuleNameOrNull(path);
		assertAreEqual(moduleFullName, new ModuleFullName(moduleFullNameStr));
		assertAreEqual(moduleFullName.getFullNameAsString(), moduleFullNameStr);
	}
	
	protected void checkInvalidModuleName(String filePath) {
		checkInvalidModuleName(path(filePath));
	}
	
	protected void checkInvalidModuleName(Path path) {
		assertTrue(ModuleNamingRules.getValidModuleNameOrNull(path) == null);
	}
	
}