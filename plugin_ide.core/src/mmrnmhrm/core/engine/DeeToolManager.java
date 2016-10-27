/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import melnorme.lang.ide.core.CoreSettings;
import melnorme.lang.ide.core.operations.ToolManager;

/**
 * Manages launching D tools.
 * Has an executor agent to run external DUB commands.
 */
public class DeeToolManager extends ToolManager {
	
	public DeeToolManager(CoreSettings settings) {
		super(settings);
	}
	
}