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
package dtool.engine.operations;


import java.nio.file.Path;

import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;

public abstract class Resolver2Tests extends CommonDToolTest {
	
	public static final String RESOLVER2 = "resolver2";
	
	protected static Path getTestResource(String resourcePath) {
		return DToolTestResources.getInstance().getResourcesDir().toPath().
				resolve(RESOLVER2).resolve(resourcePath);
	}
	
}