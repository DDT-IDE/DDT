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
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.nio.file.Path;

import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;

/**
 * A valid directory path for a dub bundle.
 * Is normalized, absolute, and has at least one segment 
 */
public class BundlePath {
	
	public static final String DUB_MANIFEST_FILENAME = "dub.json";
	
	public static BundlePath create(String pathStr) {
		try {
			Path path = MiscUtil.createPath(pathStr);
			return BundlePath.create(path);
		} catch (InvalidPathExceptionX e) {
			return null;
		}
	}
	
	public static BundlePath create(Path path) {
		if(isValidBundlePath(path)) {
			return new BundlePath(path);
		}
		return null;
	}
	
	public static boolean isValidBundlePath(Path path) {
		assertNotNull(path);
		return path.isAbsolute() && path.getNameCount() > 0;
	}
	
	/* -----------------  ----------------- */
	
	public final Path path;
	
	public BundlePath(Path path) {
		assertTrue(BundlePath.isValidBundlePath(path));
		this.path = path.normalize();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof BundlePath)) return false;
		
		BundlePath other = (BundlePath) obj;
		
		return areEqual(path, other.path);
	}
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}
	
	public Path getManifestFilePath() {
		return path.resolve(DUB_MANIFEST_FILENAME);
	}
	
	public Path resolve(Path other) {
		return path.resolve(other);
	}
	
	public Path resolve(String other) {
		return path.resolve(other);
	}
	
	@Override
	public String toString() {
		return path.toString();
	}
	
	/***
	 * Searches for a manifest file in any of the directories denoted by given path, starting in path. 
	 */
	public static BundlePath findBundleForPath(Path path) {
		if(path == null || !path.isAbsolute()) {
			return null;
		}
		BundlePath bundlePath = create(path);
		if(bundlePath != null && bundlePath.getManifestFilePath().toFile().exists()) {
			return bundlePath;
		}
		return findBundleForPath(path.getParent());
	}
	
}