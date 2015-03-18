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

import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.nio.file.Path;

import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.ReflectionUtils;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.internal.core.ExternalSourceModule;

public class DLTKUtils {
	
	/** Convenience method to get the DLTK Model. */
	public static IScriptModel getDLTKModel() {
		return DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}
	
	public static IPath localEnvPath(IPath path) {
		return EnvironmentPathUtils.getFullPath(LocalEnvironment.getInstance(), path);
	}
	
	@Deprecated
	public static Path getFilePath(ISourceModule sourceModule) throws CommonException {
		IResource resource = sourceModule.getResource();
		if(resource != null && resource.getLocation() != null) {
			// This is the best case, it means we should have an accurate path.
			return getFilePath(resource.getLocation());
		}
		if(sourceModule instanceof ExternalSourceModule) {
			ExternalSourceModule externalSourceModule = (ExternalSourceModule) sourceModule;
			IStorage storage = externalSourceModule.getStorage();
			return getFilePath(storage.getFullPath());
		}
		
		if(sourceModule.isWorkingCopy()) {
			try {
				IBuffer buffer = sourceModule.getBuffer();
				if(buffer != null) {
					IFileStore fileStore = tryCast(ReflectionUtils.readField(buffer, "fFileStore"), IFileStore.class);
					return fileStore.toLocalFile(0, null).toPath();
				}
			} catch (NoSuchFieldException e) {
			} catch (CoreException e) {
			}
		}
		
		// Workaround:
		String pathString = sourceModule.getPath().toPortableString();
		
		DeeCore.logError("Failed to get accurate filePath from source module: " + pathString);
		pathString = pathString.replace("/ /", ""); // Fix for Windows Path issue: "/ /" is not valid!
		return MiscUtil.createPath2("###ExternalFile/" + pathString);
	}
	
	public static Path getFilePath(IPath location) throws CommonException {
		return MiscUtil.createPath2(location.toOSString());
	}
	
}