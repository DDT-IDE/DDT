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
public class FindDef_Kinds1Test extends FindDef__SingleModuleCommonP  {
	
	static final String testfile = "refKinds.d";
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {218, 14},
                {250, 4},
                {279, 14},
                {312, 4},
                {354, 124},
                {387, 124},
                
                {423, 14}, {427, 59},  
                {459, 14}, {463, 59}, 
                {495, 124}, {505, 154},
                {554, 14}, {558, 25},
                {602, 14}, {606, 25},
                {652, 14}, {656, 59}, {662, 74},
                
                {733, 14},

        });
    }
	
	public FindDef_Kinds1Test(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	
}
