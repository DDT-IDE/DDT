package dtool.tests.ref.inter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_DiamondShapedTest extends FindDef__ImportsCommon  {
	
	static final String testSrcFile = "testDiamondShaped.d";

	@BeforeClass
	public static void classSetup() {
		staticTestInit(testSrcFile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                {195, 55, "pack2/foopublic.d"},
                {237, 55, "pack2/foopublic.d"},
                
                {281, 30, "pack2/foopublic2.d"},
                {325, 30, "pack2/foopublic2.d"},
             
        });
    }
    
	
	public FindDef_DiamondShapedTest(int defOffset, int refOffset, String targetFile) 
		throws Exception {
		super(defOffset, refOffset, targetFile);
	}
	
	@Override
	public void test() throws Exception {
		super.test();
	}
	
}
