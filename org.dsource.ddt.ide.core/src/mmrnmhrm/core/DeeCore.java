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
package mmrnmhrm.core;

import melnorme.lang.ide.core.LangCore;
import mmrnmhrm.core.projectmodel.DubModelManager;

import org.osgi.framework.BundleContext;

public class DeeCore extends LangCore {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	public static final String TESTS_PLUGIN_ID = PLUGIN_ID + ".tests";
	
	@Override
	protected void doCustomStart(BundleContext context) {
		// Note: the core plugin does not start the DubModelManager... it is the responsiblity of
		// the Dee UI plugin (or some other "application" code) to start it, 
		// so that they can register listeners first.
		//DubModelManager.startDefault();
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
		DubModelManager.shutdownDefault();
	}
	
}