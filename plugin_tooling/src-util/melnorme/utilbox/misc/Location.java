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
package melnorme.utilbox.misc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;

/**
 * A location is a normalized, absolute path, based upon {@link Path}.
 */
public class Location {
	
	/** @return a new {@link Location}. Assume path is absolute. */
	public static Location create_fromValid(Path path) {
		return new Location(path);
	}
	
	/** @return a new {@link Location} from given path. 
	 * @throws InvalidPathExceptionX if path is not absolute. */
	public static Location create(Path path) throws PathUtil.InvalidPathExceptionX {
		if(!path.isAbsolute()) {
			throw new PathUtil.InvalidPathExceptionX(new InvalidPathException(path.toString(), 
					"Invalid location: path is not absolute"));
		}
		return new Location(path);
	}
	
	/** @return a new {@link Location} from given path string. 
	 * @throws InvalidPathExceptionX if path is not absolute. */
	public static Location create(String pathString) throws PathUtil.InvalidPathExceptionX {
		return create(PathUtil.createPath(pathString));
	}
	
	/** @return a new {@link Location} from given path, or null if path is not absolute.  */
	public static Location createValidOrNull(Path path) {
		try {
			return create(path);
		} catch (InvalidPathExceptionX e) {
			return null;
		}
	}
	
	/* -----------------  ----------------- */
	
	public final Path path;
	
	protected Location(Path absolutePath) {
		assertTrue(absolutePath != null && absolutePath.isAbsolute());
		this.path = absolutePath.normalize();
	}
	
	public Path getPath() {
		return path;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Location)) return false;
		
		Location other = (Location) obj;
		
		return areEqual(path, other.path);
	}
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}
	
	@Override
	public String toString() {
		return path.toString();
	}
	
	public Path toPath() {
		return path;
	}
	
	public File toFile() {
		return path.toFile();
	}
	
	public URI toUri() {
		return path.toUri();
	}
	
	public Path resolvePath(String otherPath) {
		return path.resolve(otherPath);
	}
	
	/*FIXME: BUG here !!! location, need to review this code. */
	public Location resolve(String otherPathStr) {
		Path otherPath = PathUtil.createPathOrNull(otherPathStr);
		if(otherPath == null) {
			return null;
		}
		return resolve(otherPath);
	}
	
	public Location resolve(Path otherPath) {
		// resolving should always result in a valid path: absolute and non-null
		return Location.create_fromValid(path.resolve(otherPath));
	}
	
	public Location getParent() {
		Path parent = path.getParent();
		if(parent == null) {
			return null;
		}
		return Location.create_fromValid(parent);
	}
	
}