package mmrnmhrm.core;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

public class DLTKModelUtils {
	
	public static boolean exists(ISourceModule sourceModule) {
		return sourceModule != null && sourceModule.exists()
		// XXX: DLTK bug workaround: 
		// modUnit.exists() true on ANY source module with external project fragment
		// we should make a test case for this
			&& externalReallyExists(sourceModule)
		;
	}
	
	public static boolean externalReallyExists(ISourceModule sourceModule) {
		if(!(sourceModule instanceof IExternalSourceModule))
			return true;
		//modUnit.getUnderlyingResource() of externals is allways null
		IPath localPath = EnvironmentPathUtils.getLocalPath(sourceModule.getPath());
		return new File(localPath.toOSString()).exists();
	}
	
}
