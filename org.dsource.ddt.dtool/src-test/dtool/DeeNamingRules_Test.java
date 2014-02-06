package dtool;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.parser.DeeTokenHelper;
import dtool.parser.DeeTokens;

public class DeeNamingRules_Test extends DeeNamingRules {
	
	@Test
	public void test_isValidDIdentifier() throws Exception { test_isValidDIdentifier$(); }
	public void test_isValidDIdentifier$() throws Exception {
		assertTrue(isValidDIdentifier("foo"));
		assertTrue(isValidDIdentifier("bar321"));
		assertTrue(isValidDIdentifier("_bar"));
		assertTrue(isValidDIdentifier("_foo_bar"));
		assertTrue(isValidDIdentifier("foo_bar"));
		assertTrue(isValidDIdentifier("Açores"));
		assertTrue(isValidDIdentifier("Солярис"));
		
		assertTrue(!isValidDIdentifier(""));
		assertTrue(!isValidDIdentifier("foo.d"));
		assertTrue(!isValidDIdentifier("123foo"));
		assertTrue(!isValidDIdentifier("bar.foo"));
		assertTrue(!isValidDIdentifier("bar foo"));
		assertTrue(!isValidDIdentifier("bar-foo"));
		
		// Test keywords
		assertTrue(!isValidDIdentifier("while"));
		assertTrue(!isValidDIdentifier("package"));
		assertTrue(!isValidDIdentifier("__FILE__"));
		
		for (DeeTokens token : DeeTokenHelper.keyWords_All) {
			assertTrue(!isValidDIdentifier(token.getSourceValue()));
		}
		
	}
	
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
	
}