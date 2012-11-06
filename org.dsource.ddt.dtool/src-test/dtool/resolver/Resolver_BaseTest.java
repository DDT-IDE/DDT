package dtool.resolver;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;

import dtool.ast.definitions.Module;
import dtool.parser.Parser__CommonTest;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.tests.DToolBaseTest;
import dtool.util.NewUtils;

public class Resolver_BaseTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "resolver/";
	
	public static String readResolverTestFile(String filePath) throws IOException {
		return readTestResourceFile(TESTFILESDIR + filePath);
	}
	
	public static Module parseTestFile(String filename) throws IOException {
		return Parser__CommonTest.testDtoolParse(readTestResourceFile(TESTFILESDIR + filename));
	}
	
	public static final class NullModuleResolver implements IModuleResolver {
		@Override
		public String[] findModules(String fqNamePrefix) throws Exception {
			return NewUtils.EMPTY_STRING_ARRAY;
		}
		
		@Override
		public Module findModule(String[] packages, String module) throws Exception {
			return null;
		}
	}
	
	protected static int getMarkerOffset(String source, String targetMarkerName) {
		int targetOffset = -1;
		
		int offset = 0;
		while(true) {
			offset = source.indexOf("/+", offset);
			if(offset == -1)
				break;
			int commentStartOffset = offset;
			offset += 2;
			
			offset = source.indexOf("+/", offset);
			if(offset == -1)
				break;
			offset += 2;
			
			String markerName = source.substring(commentStartOffset+2, offset-2);
			if(markerName.length() < 2) 
				continue;
			
			int markerOffset;
			if(markerName.charAt(0) == '@') {
				markerOffset = commentStartOffset;
				markerName = markerName.substring(1);
			} else if(markerName.charAt(markerName.length()-1) == '@') {
				markerOffset = offset;
				markerName = markerName.substring(0, markerName.length()-1);
			} else {
				continue;
			}
			
			if(targetMarkerName.equals(markerName)) {
				assertTrue(targetOffset == -1); // can only be found once.
				targetOffset = markerOffset;
			}
		}
		assertTrue(targetOffset != -1);
		return targetOffset;
	}
	
}