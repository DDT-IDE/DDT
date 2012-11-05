package dtool.tests.ref;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FindDef_DefUnitContainer1Test extends FindDef__CommonParameterizedTest  {
	
	static final String testfile = "refDefUnitContainers1.d";
	
	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{62, 137},
				{65, 163},
				{68, 191},
				{71, 211},
				{74, 231},
				{77, 251},
				
				{272, 137},
				{275, 163},
				{278, 191},
				{281, 211},
				{284, 231},
				{287, 251},
		});
	}
	
	
	public FindDef_DefUnitContainer1Test(int offset, int targetOffset) throws IOException, CoreException {
		super(offset, targetOffset, testfile);
	}
	
}