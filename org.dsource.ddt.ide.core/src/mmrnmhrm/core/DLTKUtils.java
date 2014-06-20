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

import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.ISourceModule;
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
	
	protected static boolean isExternal(ISourceModule sourceModule) {
		return sourceModule.getResource() == null;
	}
	
	public static Path getFilePath(ISourceModule sourceModule) throws InvalidPathExceptionX {
		return MiscUtil.createPath(getFilePathString(sourceModule));
	}
	
	public static String getFilePathString(ISourceModule sourceModule) {
		IResource resource = sourceModule.getResource();
		if(resource == null) {
			return EnvironmentPathUtils.getLocalPath(sourceModule.getPath()).toOSString();
		} else {
			IPath location = resource.getLocation();
			if(location == null) {
				String pathString = sourceModule.getPath().toPortableString();
				pathString = pathString.replace("/ /", ""); // Fix for Windows Path issue: "/ /" is not valid!
				return "###ExternalFile/" + pathString;
			}
			return location.toOSString();
		}
	}
	
	public static Path getFilePath(IModuleSource moduleSource) throws InvalidPathExceptionX {
		ISourceModule sourceModule = tryCast(moduleSource.getModelElement(), ISourceModule.class);
		if(sourceModule != null) {
			return getFilePath(sourceModule);
		} else {
			return MiscUtil.createPath(moduleSource.getFileName());
		}
	}
	
}