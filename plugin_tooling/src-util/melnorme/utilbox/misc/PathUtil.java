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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import melnorme.utilbox.core.CommonException;


public class PathUtil {
	
	/** @return a valid path, 
	 * or null if a valid path could not be created from given pathString. */
	public static Path createPathOrNull(String pathString) {
		try {
			return Paths.get(pathString);
		} catch (InvalidPathException ipe) {
			return null;
		}
	}

	/** @return a valid path. Given pathString must represent a valid path. */
	public static Path createValidPath(String pathString) {
		try {
			return Paths.get(pathString);
		} catch (InvalidPathException ipe) {
			throw assertFail();
		}
	}

	/** @return a valid path, 
	 * or throws a checked exception if a valid path could not be created from given pathString. */
	public static Path createPath(String pathString) throws InvalidPathExceptionX {
		try {
			return Paths.get(pathString);
		} catch (InvalidPathException ipe) {
			throw new InvalidPathExceptionX(ipe);
		}
	}

	/** Checked exception wrapper for {@link InvalidPathException} */
	public static class InvalidPathExceptionX extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		public InvalidPathExceptionX(InvalidPathException ipe) {
			super(ipe);
		}
		
	}
	
	public static Path getParentOrEmpty(Path path) throws CommonException {
		Path parent = path.getParent();
		return parent == null ? createValidPath("") : parent;
	}
	
	
	public static Location newLocation(String pathString) throws InvalidPathExceptionX {
		return newLocation(createPath(pathString));
	}
	
	public static Location newLocation(Path path) throws InvalidPathExceptionX {
		if(!path.isAbsolute()) {
			throw new InvalidPathExceptionX(new InvalidPathException(path.toString(), 
				"Invalid location: path is not absolute"));
		}
		return new Location(path);
	}
	
	public static Location newLocation_fromValid(Path path) {
		return new Location(path);
	}

	
	/**
	 * A location is a normalized, absolute path.
	 */
	public static class Location {
		
		public final Path path;
		
		protected Location(Path absolutePath) {
			assertTrue(absolutePath != null && absolutePath.isAbsolute());
			this.path = absolutePath.normalize();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(!(obj instanceof PathUtil.Location)) return false;
			
			PathUtil.Location other = (PathUtil.Location) obj;
			
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
		
	}
	
}