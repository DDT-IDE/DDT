/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core;

import java.nio.file.Path;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;

public class DLTKUtils {
	
	/** Convenience method to get the DLTK Model. */
	public static IScriptModel getDLTKModel() {
		return DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}
	
	public static IPath localEnvPath(IPath path) {
		return EnvironmentPathUtils.getFullPath(LocalEnvironment.getInstance(), path);
	}
	
	public static Path filePathFromSourceModule(ISourceModule sourceModule) throws ModelException {
		if(sourceModule.exists() == false) {
			DeeCore.logWarning("#getParsedDeeModule with module that does not exist: " + 
					sourceModule.getElementName());
		}
		
		Path filePath;
		
		IResource resource = sourceModule.getResource();
		if(resource == null) {
			filePath = EnvironmentPathUtils.getLocalPath(sourceModule.getPath()).toFile().toPath();
		} else {
			IPath location = resource.getLocation();
			if(location == null) {
				/*BUG here*/
				throw new ModelException(DeeCore.createCoreException("no location for source module", null));
			}
			filePath = location.toFile().toPath();
		}
		return filePath;
	}
	
	protected static boolean isExternal(ISourceModule sourceModule) {
		return sourceModule.getResource() == null;
	}
	
}