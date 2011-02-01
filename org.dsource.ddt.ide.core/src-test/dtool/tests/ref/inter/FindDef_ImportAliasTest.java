package dtool.tests.ref.inter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_ImportAliasTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testImportAlias.d";


	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

 
                {102, 12, "pack/mod1.d"},
                {125, 12, "pack/mod2.d"},
                {150, 12, "pack/sample.d"},
                {171, 20, "pack/subpack/mod3.d"},

                {390, 131, testSrcFile}, // DefUnit in import

                {364, -1, null},
                {402, 60, "pack/sample.d"},
                {468, 86, "pack/sample.d"},
                
                {491, 55, "pack2/foopublic.d"},
                {527, -1, null},
                {491, 55, "pack2/foopublic.d"},
                {654, -1, null},
              
        });
    }
    
	
	public FindDef_ImportAliasTest(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

}
