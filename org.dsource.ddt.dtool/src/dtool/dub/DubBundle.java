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
	
	public final Path location; // location in the filesystem where bundle is installed. Can be null if not installed.
	public final String name; 
	public final String version;
	public final Path[] srcFolders;
	public final Path[] implicitSrcFolders;
	public final Object[] dependencies;
	public final DubBundleException error;
	
	public DubBundle(Path location, String name, String version, Path[] srcFolders, Path[] implicitSrcFolders,
			Object[] dependencies, DubBundleException exception) {
		this.location = location;
		this.name = assertNotNull(name);
		this.version = version;
		this.srcFolders = srcFolders;
		this.implicitSrcFolders = implicitSrcFolders;
		this.dependencies = dependencies;
		this.error = exception;
		if(!hasErrors()) {
			assertTrue(location != null);
		}
	}
	
	public boolean hasErrors() {
		return error != null;
	}
	
	public Path[] getSourceFolders() {
		if(srcFolders != null) {
			return srcFolders;
		} else {
			return implicitSrcFolders;
		}
	}
	
	@SuppressWarnings("serial")
	public static class DubBundleException extends Exception {
		
	    public DubBundleException(String message, Throwable cause) {
	        super(message, cause);
	    }
		
		public DubBundleException(String message) {
	        super(message);
	    }
		
		public DubBundleException(Throwable exception) {
	        super(exception);
	    }
		
	}
	
}