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
package dtool.engine.modules;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;

public abstract class CommonModuleResolver implements IModuleResolver {
	
	@Override
	public String[] findModules(String fqNamePrefix) throws Exception {
		assertNotNull(fqNamePrefix);
		return findModules_do(fqNamePrefix);
	}
	
	protected abstract String[] findModules_do(String fqNamePrefix) throws Exception;
	
	@Override
	public Module findModule(String[] packages, String module) throws Exception {
		assertNotNull(packages);
		assertTrue(ArrayUtil.contains(packages, null) == false);
		assertTrue(ArrayUtil.contains(packages, "") == false);
		assertNotNull(module);
		assertTrue(!module.isEmpty());
		return findModule_do(packages, module);
	}
	
	protected abstract Module findModule_do(String[] packages, String module) throws Exception;
	
}