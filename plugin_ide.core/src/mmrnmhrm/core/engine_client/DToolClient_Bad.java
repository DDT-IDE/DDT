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

import melnorme.lang.tooling.context.EmptySemanticResolution;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Problematic API that needs to be replaced eventually
 */
public class DToolClient_Bad {
	
	@Deprecated
	public static ISemanticContext getResolverFor(Path filePath) {
		try {
			return getResolverFor(Location.create(filePath));
		} catch (InvalidPathExceptionX e) {
			return new EmptySemanticResolution();
		}
	}
	
	@Deprecated
	public static ISemanticContext getResolverFor(Location filePath) {
		try {
			return DToolClient.getDefault().getResolvedModule(filePath).getSemanticContext();
		} catch (CommonException e) {
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