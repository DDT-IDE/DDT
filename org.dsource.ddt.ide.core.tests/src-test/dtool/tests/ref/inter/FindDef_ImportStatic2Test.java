package dtool.tests.ref.inter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.util.List;

import org.eclipse.dltk.core.ModelException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * This classes reuses FindDef_TestImportStatic test data, modyfing only some
 * of test cases, and the source file.
 */
@RunWith(Parameterized.class)
public class FindDef_ImportStatic2Test extends FindDef_ImportStaticTest  {
	
	
	@BeforeClass
	public static void classSetup() throws ModelException {
		setupDefault(testSrcFile);
		defaultModule.scriptModule.getBuffer().replace(ix1, 4, "//  ");
		assertEquals(defaultModule.scriptModule.getBuffer().getText(ix1, 4), "//  ");
		defaultModule.scriptModule.getBuffer().replace(ix2, 4, "    ");
		assertEquals(defaultModule.scriptModule.getBuffer().getText(ix2, 4), "    ");
		setupDefault(testSrcFile);
	}
	
	@Parameters
	public static List<Object[]> data() {
		java.util.List<Object[]> coll = FindDef_ImportStaticTest.data();
		coll.set(0, new Object[] {180, 12, "pack/mod1.d"});
		coll.set(1, new Object[] {198, 12, "pack/mod2.d"});
		coll.set(2, new Object[] {209, 12, "pack/sample.d"});
		coll.set(3, new Object[] {230, 20, "pack/subpack/mod3.d"});
		return coll;
	}
	
	
	public FindDef_ImportStatic2Test(int defOffset, int refOffset, String targetFile) throws Exception {
		super(defOffset, refOffset, targetFile);
	}
	
}