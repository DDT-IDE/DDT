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

import static melnorme.utilbox.misc.MiscUtil.createValidPath;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;

import com.google.gson.stream.JsonToken;

import dtool.dub.DubBundle.DubBundleException;

/**
 * Parse a Dub bundle in a filesystem location into an in-memory description of the bundle.
 */
public class DubBundleParser extends CommonDubParser {
	
	public static DubBundle parseDubBundleFromLocation(Path location) {
		return new DubBundleParser().parseDubBundle(location);
	}
	
	protected String bundleName = null;
	protected String version = null;
	protected String[] sourceFolders = null;
	protected Path[] autoSourceFolders = null;
	protected Object[] dependencies = null; // TODO
	protected String path = null;
	
	protected DubBundleParser() {
	}
	
	protected DubBundle parseDubBundle(Path location) {
		try {
			parseFromLocation(location);
		} catch (DubBundleException e) {
			dubError = e;
		}
		return createBundle(location);
	}
	
	protected DubBundle parseDubBundleFromDescribeSource(String source) {
		try {
			parseFromSource(source);
		} catch (DubBundleException e) {
			dubError = e;
		}
		
		return createBundle(null);
	}
	
	public DubBundle createBundle(Path location) {
		if(location == null) {
			try {
				location = MiscUtil.createPath(path);
			} catch (InvalidPathExceptionX e) {
				putError("Invalid path: " + path);
			}
		}
		if(bundleName == null) {
			bundleName = location == null ? "" : location.getFileName().toString();
			
			putError("Bundle name not defined.");
		}
		
		Path[] sourceFoldersPaths = createPaths(sourceFolders);
		
		return new DubBundle(location, bundleName, version, sourceFoldersPaths, autoSourceFolders, 
				dependencies, dubError);
	}
	
	protected void parseFromLocation(Path location) throws DubBundleException {
		File jsonLocation = location.resolve(MiscUtil.createValidPath("dub.json")).toFile();
		
		try {
			String source = readStringFromFile(jsonLocation);
			parseFromSource(source);
		} catch (IOException e) {
			throw new DubBundleException(e);
		}
		
		if(sourceFolders == null) {
			autoSourceFolders = searchImplicitSrcFolders(location);
		}
	}
	
	@Override
	protected void readData(JsonReaderExt jsonParser) throws IOException {
		readBundle(jsonParser);
	}
	
	protected DubBundleParser readBundle(JsonReaderExt jsonParser) throws IOException {
		jsonParser.consumeExpected(JsonToken.BEGIN_OBJECT);
		
		while(jsonParser.hasNext()) {
			JsonToken tokenType = jsonParser.peek();
			
			if(tokenType == JsonToken.NAME) {
				String propertyName = jsonParser.nextName();
				
				if(propertyName.equals("name")) {
					bundleName = jsonParser.consumeStringValue();
				} else if(propertyName.equals("version")) {
					version = jsonParser.consumeStringValue();
				} else if(propertyName.equals("importPaths")) {
					sourceFolders = readSourcePaths(jsonParser);
				} else if(propertyName.equals("depedencies")) {
					readDependencies(jsonParser);
				} else if(propertyName.equals("path")) {
					path = jsonParser.consumeStringValue();
				} else {
					jsonParser.skipValue();
				}
			} else {
				jsonParser.errorUnexpected(tokenType);
			}
		}
		
		jsonParser.consumeExpected(JsonToken.END_OBJECT);
		return this;
	}
	
	protected String[] readSourcePaths(JsonReaderExt jsonParser) throws IOException {
		ArrayList<String> stringArray = jsonParser.consumeStringArray(true);
		return ArrayUtil.createFrom(stringArray, String.class);
	}
	
	
	protected void readDependencies(JsonReaderExt jsonParser) throws IOException {
		jsonParser.skipValue();
		dependencies = null;
	}
	
	protected Path[] searchImplicitSrcFolders(Path location) throws DubBundleException {
		final ArrayList<Path> implicitFolders = new ArrayList<>();
		
		if(location == null) {
			return new Path[0];
		}
		File locationDir = location.toFile();
		if(!locationDir.isDirectory())
			throw new DubBundleException("location is not a directory");
		
		locationDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(dir.isDirectory() && (name.equals("src") || name.equals("source"))) {
					implicitFolders.add(createValidPath(name));
				}
				return false;
			}
		});
		
		return ArrayUtil.createFrom(implicitFolders, Path.class);
	}

}