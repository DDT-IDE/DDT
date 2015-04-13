/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.build;

import java.nio.file.Path;

import melnorme.lang.tooling.data.SDKLocationValidator;
import melnorme.lang.tooling.data.StatusLevel;
import melnorme.lang.tooling.data.ValidationMessages;
import melnorme.lang.utils.SearchPathForExecutable;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCoreMessages;

public class DubLocationValidator extends SDKLocationValidator {
	
	public DubLocationValidator() {
		super(DeeCoreMessages.DUB_PATH_Label);
		directoryOnly = false;
		fileOnly = true;
	}
	
	@Override
	protected Location validatePath(Path path) throws ValidationException {
		if(!path.isAbsolute() && path.getNameCount() == 1) {
			String pathEnvExe = path.toString();
			
			try {
				new SearchPathForExecutable(pathEnvExe).checkIsFound();
			} catch (CommonException e) {
				throw createException(StatusLevel.WARNING, e.getMessage());
			}
			return null; // special case allowed
		}
		
		return super.validatePath(path);
	}
	
	@Override
	protected ValidationException error_NotAbsolute(Path path) throws ValidationException {
		return createException(StatusLevel.ERROR, ValidationMessages.Location_NotAbsoluteNorSingle(path));
	}
	
	@Override
	protected Location getSDKExecutableLocation(Location location) {
		return location;
	}
	
	@Override
	protected String getSDKExecutable_append() {
		return ""; 
	}
	
}