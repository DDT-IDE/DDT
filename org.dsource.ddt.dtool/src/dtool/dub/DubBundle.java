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
import static melnorme.utilbox.misc.ArrayUtil.nullToEmpty;

import java.nio.file.Path;
import java.nio.file.Paths;

import melnorme.utilbox.misc.MiscUtil;

public class DubBundle {
	
	public static final String DEFAULT_VERSION = "~master";
	
	public final Path location;// location in the filesystem where bundle is installed. Can be null if not installed.
	public final String name; 
	public final DubBundleException error;
	
	public final String version;
	public final String[] srcFolders;
	public final Path[] effectiveSrcFolders;
	public final DubDependecyRef[] dependencies;
	public final String targetName;
	public final String targetPath;
	
	public DubBundle(Path location, String name, DubBundleException error, String version, String[] srcFolders,
			Path[] effectiveSrcFolders, DubDependecyRef[] dependencies, String targetName, String targetPath) {
		this.location = location;
		this.name = assertNotNull(name);
		this.error = error;
		
		this.version = version == null ? DEFAULT_VERSION : version;
		this.srcFolders = srcFolders;
		this.effectiveSrcFolders = nullToEmpty(effectiveSrcFolders, Path.class);
		this.dependencies = nullToEmpty(dependencies, DubDependecyRef.class);
		this.targetName = targetName;
		this.targetPath = targetPath;
		
		if(!hasErrors()) {
			assertTrue(location != null);
		}
	}
	
	public DubBundle(Path location, String name, DubBundleException error) {
		this(location, name, error, null, null, null, null, null, null);
	}
	
	public boolean hasErrors() {
		return error != null;
	}
	
	public String[] getDefinedSourceFolders() {
		return srcFolders;
	}
	
	public Path[] getEffectiveSourceFolders() {
		return assertNotNull(effectiveSrcFolders);
	}
	
	public DubDependecyRef[] getDependencyRefs() {
		return dependencies;
	}
	
	public String getTargetName() {
		return targetName;
	}
	
	public String getTargetPath() {
		return targetPath;
	}
	
	public String getEffectiveTargetName() {
		String baseName = targetName != null ? targetName : name;
		return baseName + getExecutableSuffix();
	}
	
	protected static String getExecutableSuffix() {
		return MiscUtil.OS_IS_WINDOWS ? ".exe" : "";
	}
	
	public Path getEffectiveTargetFullPath() {
		Path path = MiscUtil.createValidPath(getTargetPath() == null ? "" : getTargetPath());
		if(path == null) {
			path = Paths.get("");
		}
		return path.resolve(getEffectiveTargetName());
	}
	
	public static class DubDependecyRef {
		
		public final String bundleName;
		public final String version; // tODO;
		
		public DubDependecyRef(String bundleName, String version) {
			this.bundleName = bundleName;
			this.version = version;
		}
		
		public String getBundleNameRef() {
			return bundleName;
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
		
		@Override
		public String getMessage() {
			return super.getMessage();
		}
		
	}
	
}