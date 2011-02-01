package dtool.tests.ref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_DefUnitContainer3Test extends FindDef__SingleModuleCommonP  {
	
	static final String testfile = "refDefUnitContainers3.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		{62, 146},
        		{65, 184},
        		{68, 214}, 
        		{72, 234}, 
        		{76, 288},
        		{79, 292},
        		// {82, 320}, TODO: mixin container not supported yet 

        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	public FindDef_DefUnitContainer3Test(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	
}
