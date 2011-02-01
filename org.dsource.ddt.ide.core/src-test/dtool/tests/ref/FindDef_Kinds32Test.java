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
public class FindDef_Kinds32Test extends FindDef__SingleModuleCommonP  {
	
	static final String testfile = "refKinds32.d";
	
    
	@BeforeClass
	public static void classSetup() {
		staticTestInit(testfile);
	}

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
        		{238, -1},
                {278, -1},
                {328, -1},
                {369, -1}, {379, -1},
                {423, -1},
                {462, -1},
                {485, -1},

        });
    }
	
	public FindDef_Kinds32Test(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	
}
