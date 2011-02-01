package dtool.tests.ref.inter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_ImportSelectiveTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testImportSelective.d";


	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                //
                //
                {195, 12, "pack/sample.d"}, 
                //
                
                {208, 60, "pack/sample.d"}, 
                {240, 100, "pack/sample.d"}, 
                {258, 25, "pack/sample.d"}, 
                {286, 40, "pack/sample.d"}, 
                
                {888, 911, testSrcFile},

                {479, -1, null},
                
                {528, 60, "pack/sample.d"},
                {554, 221, testSrcFile},
                {586, -1, null},

                {652, 86, "pack/sample.d"},
                {672, 221, testSrcFile},
                {701, -1, null},

                
                {720, -1, null},
                {752, -1, null},
                {803, -1, null},
                {852, -1, null},
                
              
        });
    }
    
	
	public FindDef_ImportSelectiveTest(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}

}
