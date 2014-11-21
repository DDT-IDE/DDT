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
package mmrnmhrm.core.engine_client;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.EmptySemanticResolution;
import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Bad API that needs to be replaced eventually
 */
public class DToolClient_Bad {
	
	@Deprecated
	public static ISemanticContext getResolverFor(Path filePath) {
		try {
			return DToolClient.getDefault().getResolvedModule(filePath).getSemanticContext();
		} catch (ExecutionException e) {
			return new EmptySemanticResolution();
		}
	}
	
	@Deprecated
	public static Path getFilePath(ISourceModule input) throws CoreException {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			throw new CoreException(DeeCore.createErrorStatus("Invalid path for module source. ", e));
		}
	}
	
	@Deprecated
	public static Path getFilePath(IModuleSource input) throws CoreException {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			throw new CoreException(DeeCore.createErrorStatus("Invalid path for module source. ", e));
		}
	}
	
	@Deprecated
	public static Path getFilePathOrNull(ISourceModule input) {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			DeeCore.logError("Invalid path from DLTK: " + e);
			return null;
		}
	}
	
	@Deprecated
	public static Path getFilePathOrNull(IModuleSource input) {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			DeeCore.logError("Invalid path from DLTK: " + e);
			return null;
		}
	}
	
}
