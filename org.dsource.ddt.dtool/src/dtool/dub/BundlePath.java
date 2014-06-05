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

/**
 * A valid directory path for a dub bundle.
 * Is normalized, absolute, and has at least one segment 
 */
public class BundlePath {
	
	@Deprecated
	public static BundlePath createUnchecked(Path path) {
		return new BundlePath(path);
	}
	
	public final Path path;
	
	public BundlePath(Path path) {
		this.path = BundlePath.validatePath(path);
	}
	
	public static Path validatePath(Path filePath) {
		assertNotNull(filePath);
		assertTrue(filePath.isAbsolute());
		assertTrue(filePath.getNameCount() > 0);
		filePath = filePath.normalize();
		return filePath;
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
	
	public Path getManifestPath() {
		return path.resolve("dub.json");
	}
	
	@Override
	public String toString() {
		return path.toString();
	}
	
}