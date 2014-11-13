/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.bundles;

import java.util.HashSet;

import dtool.ast.definitions.Module;

public class NullModuleResolver extends CommonModuleResolver {
	
	@Override
	protected HashSet<String> findModules_do(String fqNamePrefix) {
		return new HashSet<>();
	}
	
	@Override
	protected Module findModule_do(String[] packages, String module) {
		return null;
	}
	
}