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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import dtool.engine.CommonSemanticManagerTest.Tests_DToolServer;
import dtool.tests.CommonDToolTest;
import dtool.tests.DToolTestResources;

public abstract class CommonDToolOperation_Test extends CommonDToolTest {
	
	public static final String RESOLVER2 = "resolver2";
	
	public static final Path BUNDLE_FOO__SRC_FOLDER = getTestResource("bundleFoo/source");
	
	protected static Path getTestResource(String resourcePath) {
		return DToolTestResources.getInstance().getResourcesDir().toPath().
				resolve(RESOLVER2).resolve(resourcePath);
	}
	
	protected static Tests_DToolServer dtoolEngine = new Tests_DToolServer();
	
	protected int indexOf(String source, String markerString) {
		int offset = source.indexOf(markerString);
		assertTrue(offset != -1);
		return offset;
	}
	
}