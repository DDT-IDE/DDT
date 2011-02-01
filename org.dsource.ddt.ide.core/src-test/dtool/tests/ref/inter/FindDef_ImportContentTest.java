package dtool.tests.ref.inter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_ImportContentTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testImportContent.d";


	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {90, 12, "pack/mod1.d"},
                {109, 12, "pack/mod2.d"},
                {120, 12, "pack/sample.d"},
                {141, 20, "pack/subpack/mod3.d"},

                {151, 60, "pack/sample.d"},
                {205, 86, "pack/sample.d"},
                
                {216, 55, "pack2/foopublic.d"},
                {240, -1, null},
                {292, 55, "pack2/foopublic.d"},
                {333, -1, null},
              
        });
    }
    
	
	public FindDef_ImportContentTest(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

}
