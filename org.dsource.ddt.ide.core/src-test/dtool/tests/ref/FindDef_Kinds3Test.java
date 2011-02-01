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
public class FindDef_Kinds3Test extends FindDef__SingleModuleCommonP  {
	
	public static final String testfile = "refKinds3.d";
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {218, -1},
                {250, -1},
                {279, -1},
                {312, -1},
                {354, -1},
                {387, -1},
                
                {423, -1}, {427, -1},  
                {459, -1}, {463, -1}, 
                {495, -1}, {505, -1},
                {554, -1}, {558, -1},
                {602, -1}, {606, -1},
                {652, -1}, {656, -1}, {662, -1},
                
                {733, -1},

        });
    }
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	public FindDef_Kinds3Test(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	

}
