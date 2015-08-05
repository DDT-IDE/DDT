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

import static java.util.Collections.unmodifiableList;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.ArrayUtil.nullToEmpty;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.HashcodeUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.PathUtil;
import melnorme.utilbox.misc.StringUtil;

public class DubBundle {
	
	public static final String DEFAULT_VERSION = "~master";
	
	public final String name; // not null
	
	// bundlePath is the bundle's location in the filesystem. Can be null if path is invalid or not specified.
	protected final BundlePath bundlePath;
	
	public final DubBundleException error;
	
	public final String version;
	public final String[] srcFolders;
	public final Path[] effectiveSourceFolders;
	public final List<BundleFile> bundleFiles;
	
	public final DubDependecyRef[] dependencies; // not null
	public final String targetName;
	public final String targetPath;
	
	public final Indexable<DubConfiguration> configurations;
	
	public DubBundle(
			BundlePath bundlePath, 
			String name, 
			DubBundleException error, 
			String version, 
			String[] srcFolders,
			Path[] effectiveSrcFolders, 
			List<BundleFile> bundleFiles,
			DubDependecyRef[] dependencies, 
			String targetName, 
			String targetPath,
			ArrayList2<DubConfiguration> configurations) {
		this.bundlePath = bundlePath;
		this.name = assertNotNull(name);
		this.error = error;
		
		this.version = version == null ? DEFAULT_VERSION : version;
		this.srcFolders = srcFolders;
		this.effectiveSourceFolders = nullToEmpty(effectiveSrcFolders, Path.class);
		this.dependencies = nullToEmpty(dependencies, DubDependecyRef.class);
		this.bundleFiles = unmodifiableList(CollectionUtil.nullToEmpty(bundleFiles));
		this.targetName = targetName;
		this.targetPath = targetPath;
		
		this.configurations = CollectionUtil.nullToEmpty(configurations);
		
		if(!hasErrors()) {
			assertTrue(bundlePath != null);
		}
	}
	
	public DubBundle(BundlePath bundlePath, String name, DubBundleException error) {
		this(bundlePath, name, error, null, null, null, null, null, null, null, null);
	}
	
	@Override
	public String toString() {
		return name + " ("+version+") @" + (bundlePath == null ? "<null>" : bundlePath);
	}
	
	/** @return the bundle name, not null. */
	public String getBundleName() {
		return name;
	}
	
	/** @return the simple name of this child bundle, if it is a child bundle. null otherwise. */
	public String getSubpackageSuffix() {
		return StringUtil.segmentAfterMatch(getBundleName(), ":");
	}
	
	/** @return the bundlePath. Can be null. */
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public Location getLocation() {
		return bundlePath == null ? null : bundlePath.location;
	}
	
	public String getLocationString() {
		return getLocation() == null ? "[null]" : getLocation().toString();
	}
	
	public boolean hasErrors() {
		return error != null;
	}
	
	public String[] getDefinedSourceFolders() {
		return srcFolders;
	}
	
	public Path[] getEffectiveSourceFolders() {
		return assertNotNull(effectiveSourceFolders);
	}
	
	public Path[] getEffectiveImportFolders() {
		return assertNotNull(effectiveSourceFolders);
	}
	
	public static class BundleFile {
		
		public final String filePath;
		public final boolean importOnly;
		
		public BundleFile(String filePath, boolean importOnly) {
			this.filePath = assertNotNull(filePath);
			this.importOnly = importOnly;
		}
		
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
	
	protected String getEffectiveTargetName() {
		return targetName != null ? targetName : name;
	}
	
	public Path getValidTargetName() throws CommonException {
		String rawTargetName = getEffectiveTargetName();
		if(rawTargetName == null) {
			throw new CommonException("Target Name not specified");
		}
		return PathUtil.createPath(rawTargetName);
	}
	
	public Path getValidTargetPath() throws CommonException {
		if(targetPath == null) {
			return PathUtil.createPath("");
		}
		return PathUtil.createPath(targetPath);
	}
	
	public static class DubDependecyRef {
		
		public final String bundleName;
		public final String version; // not implemented yet, not really important.
		
		public DubDependecyRef(String bundleName, String version) {
			this.bundleName = assertNotNull(bundleName);
			this.version = version;
		}
		
		public String getBundleName() {
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
		
		public String getExtendedMessage() {
			return getMessage() + (getCause() == null ? "" : getCause().getMessage());
		}
		
	}
	
	/* ----------------- utilities ----------------- */
	
	public ArrayList<Location> getEffectiveImportFolders_AbsolutePath() {
		assertTrue(bundlePath != null);
		
		ArrayList<Location> importFolders = new ArrayList<>(effectiveSourceFolders.length);
		for (Path srcFolder_relative : effectiveSourceFolders) {
			importFolders.add(bundlePath.resolve(srcFolder_relative));
		}
		return importFolders;
	}
	
	public Path relativizePathToImportFolder(Path path) {
		ArrayList<Location> importFolders = getEffectiveImportFolders_AbsolutePath();
		for(Location importFolder : importFolders) {
			if(path.startsWith(importFolder.path)) {
				return importFolder.path.relativize(path);
			}
		}
		return null;
	}
	
	/* ----------------- Build Config ----------------- */
	
	public Indexable<DubConfiguration> getConfigurations() {
		return configurations;
	}
	
	public static class DubConfiguration {
		
		public final String name;
		
		public final String targetType;
		public final String targetName;
		public final String targetPath;
		
		public DubConfiguration(String name, String targetType, String targetName, String targetPath) {
			this.name = assertNotNull(name);
			this.targetType = targetType;
			this.targetName = targetName;
			this.targetPath = targetPath;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(!(obj instanceof DubConfiguration)) return false;
			
			DubConfiguration other = (DubConfiguration) obj;
			
			return 
					areEqual(name, other.name) &&
					areEqual(targetType, other.targetType) &&
					areEqual(targetName, other.targetName) &&
					areEqual(targetPath, other.targetPath);
		}
		
		@Override
		public int hashCode() {
			return HashcodeUtil.combinedHashCode(name, targetType, targetPath);
		}
		
		/* -----------------  ----------------- */
		
		public Path getEffectiveTargetPath(DubBundle dubBundle) throws CommonException {
			if(targetPath != null) {
				return PathUtil.createPath(targetPath);
			}
			return dubBundle.getValidTargetPath();
		}
		
		
		public Path getEffectiveTargetName(DubBundle dubBundle) throws CommonException {
			if(targetName != null) {
				return PathUtil.createPath(targetName);
			}
			return dubBundle.getValidTargetName();
		}
		
		public Path getEffectiveTargetFullPath(DubBundle dubBundle) throws CommonException {
			return getEffectiveTargetPath(dubBundle).resolve(getEffectiveTargetName(dubBundle));
		}
		
	}
	
}