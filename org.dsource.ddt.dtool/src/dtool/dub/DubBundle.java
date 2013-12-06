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
import java.util.Arrays;

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
	
	public Path[] getRawSourceFolders() {
		if(srcFolders != null) {
			return srcFolders;
		} else {
			return implicitSrcFolders;
		}
	}
	
	@SuppressWarnings("serial")
	public static class DubBundleException extends Exception {
		
		public DubBundleException(String message) {
	        super(message);
	    }
		
		public DubBundleException(Exception exception) {
	        super(exception);
	    }
		
	}
	
	public static class DubBundleDescription {
		
		protected final String bundleName;
		protected final DubBundleException error;
		protected final DubBundle mainDubBundle;
		protected final DubBundle[] bundleDependencies;
		
		public DubBundleDescription(String bundleName, DubBundle[] bundles, DubBundleException error) {
			this.bundleName = bundleName;
			this.error = error;
			
			if(bundles != null && bundles.length >= 1) {
				if(!hasErrors()) {
					// If no main error, then bundles must have no errors as well
					for (DubBundle dubBundle : bundles) {
						assertTrue(!dubBundle.hasErrors());
					}
				}
				mainDubBundle = bundles[0];
				bundleDependencies = Arrays.copyOfRange(bundles, 1, bundles.length);
			} else {
				mainDubBundle = null;
				bundleDependencies = null;
			}
			
			if(!hasErrors()) {
				assertTrue(bundles.length >= 1);
			}
			
		}
		
		public boolean hasErrors() {
			return error != null;
		}
		
		public DubBundle getMainBundle() {
			return mainDubBundle;
		}
		
		public DubBundle[] getBundleDependencies() {
			return bundleDependencies;
		}
		
		public DubBundleException getError() {
			return error;
		}
		
	}
	
}