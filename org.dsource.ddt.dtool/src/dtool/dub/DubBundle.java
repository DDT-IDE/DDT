/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

public class DubBundle {
	
	public final String name; 
	public final String version;
	public final Path location; // location in the filesystem where bundle is installed. Can be null if not installed.
	public final Path[] srcFolders;
	public final Path[] implicitSrcFolders;
	public final Object[] dependencies;
	
	public DubBundle(String name, String version, Path location, Path[] srcFolders, Path[] implicitSrcFolders,
			Object[] dependencies) {
		this.name = assertNotNull(name);
		this.version = version;
		this.location = location;
		this.srcFolders = srcFolders;
		this.implicitSrcFolders = implicitSrcFolders;
		this.dependencies = dependencies;
		assertTrue(srcFolders != null || implicitSrcFolders != null);
	}
	
	public Path[] getEffectiveSourceFolders() {
		if(srcFolders != null) {
			return srcFolders;
		} else {
			return implicitSrcFolders;
		}
	}
	
}