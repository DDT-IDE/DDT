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
package mmrnmhrm.ui.launch;

import melnorme.lang.ide.ui.launch.LangLaunchShortcut;
import mmrnmhrm.core.launch.DeeLaunchConstants;

public class DeeLaunchShortcut extends LangLaunchShortcut {
	
	@Override
	protected String getLaunchTypeId() {
		return DeeLaunchConstants.ID_DEE_LAUNCH_TYPE;
	}
	
}