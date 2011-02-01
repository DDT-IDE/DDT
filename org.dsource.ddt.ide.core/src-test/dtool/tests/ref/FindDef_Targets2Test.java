package dtool.tests.ref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

// TODO test special defunits (not a priority)


@RunWith(Parameterized.class)
public class FindDef_Targets2Test extends FindDef__SingleModuleCommonP  {
	
	static final String testfile = "refTargets2.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                
                // Special symbols
                //{1293, 1260},
                //{1315, 1280},
                //{1360, 1363},
                //{1378, 1379},
                //{1421, 1404},
        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	        
	public FindDef_Targets2Test(int offset, int targetOffset) throws IOException, CoreException  {
		super(offset, targetOffset, testfile);
	}
	  

}
