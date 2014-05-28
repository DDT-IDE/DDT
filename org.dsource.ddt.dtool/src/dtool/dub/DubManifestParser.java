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

import static melnorme.utilbox.misc.MiscUtil.createPath;
import static melnorme.utilbox.misc.MiscUtil.createValidPath;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;

import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundle.DubDependecyRef;

/**
 * Parse a Dub bundle in a filesystem location into an in-memory description of the bundle.
 */
public class DubManifestParser extends CommonDubParser {
	
	public static final String DUB_MANIFEST_FILENAME = "dub.json";
	
	public static final String ERROR_BUNDLE_NAME_UNDEFINED = "Bundle name not defined.";
	
	public static DubBundle parseDubBundleFromLocation(Path location) {
		return new DubManifestParser().parseDubBundle(location);
	}
	
	protected String source;
	
	protected String locationStr = null;
	protected String bundleName = null;
	
	protected String version = null;
	protected String[] sourceFolders = null;
	protected List<String> sourceFiles = null; //TODO
	protected List<String> importFiles = null;
	protected DubDependecyRef[] dependencies = null;
	protected String targetName = null;
	protected String targetPath = null;
	
	protected DubManifestParser() {
	}
	
	protected DubBundle parseDubBundle(Path location) {
		try {
			parseFromLocation(location);
		} catch (DubBundleException e) {
			dubError = e;
		}
		return createBundle(location, true);
	}
	
	protected void parseFromLocation(Path location) throws DubBundleException {
		File jsonLocation = location.resolve(MiscUtil.createValidPath(DUB_MANIFEST_FILENAME)).toFile();
		
		try {
			source = readStringFromFile(jsonLocation);
		} catch (IOException e) {
			throw new DubBundleException(e);
		}
		parseFromSource(source);
	}
	
	public DubBundle createBundle(Path location, boolean searchImplicitSourceFolders) {
		if(location == null) {
			try {
				location = MiscUtil.createPath(locationStr);
			} catch (InvalidPathExceptionX e) {
				putError("Invalid path: " + locationStr);
			}
		}
		if(bundleName == null) {
			bundleName = "<undefined>";
			
			putError(ERROR_BUNDLE_NAME_UNDEFINED);
		}
		
		Path[] effectiveSourceFolders;
		if(sourceFolders != null) {
			effectiveSourceFolders = createPaths(sourceFolders);
		} else if(searchImplicitSourceFolders) {
			effectiveSourceFolders = searchImplicitSrcFolders(location);
		} else {
			effectiveSourceFolders = null;
		}
		
		return new DubBundle(location, bundleName, dubError, version, 
			sourceFolders, effectiveSourceFolders, 
			sourceFiles, importFiles,
			dependencies, targetName, targetPath);
	}
	
	protected Path[] createPaths(String[] paths) {
		if(paths == null) 
			return null;
		
		ArrayList<Path> pathArray = new ArrayList<>();
		for (String pathString : paths) {
			try {
				pathArray.add(createPath(pathString));
			} catch (InvalidPathExceptionX e) {
				putError("Invalid source/import path: " + pathString);
			}
		}
		
		return ArrayUtil.createFrom(pathArray, Path.class);
	}
	
	protected Path[] searchImplicitSrcFolders(Path location) {
		if(location == null) {
			return new Path[0];
		}
		File locationDir = location.toFile();
		if(!locationDir.isDirectory()) {
			putError("location is not a directory");
			return new Path[0];
		}
		
		final ArrayList<Path> implicitFolders = new ArrayList<>();
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
	
	@Override
	protected void readData(JsonReaderExt jsonParser) throws IOException {
		readBundle(jsonParser);
	}
	
	protected DubManifestParser readBundle(JsonReaderExt jsonParser) throws IOException {
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
				} else if(propertyName.equals("dependencies")) {
					dependencies = readDependencies(jsonParser);
				} else if(propertyName.equals("path")) {
					locationStr = jsonParser.consumeStringValue();
				} else if(propertyName.equals("targetName")) {
					targetName = jsonParser.consumeStringValue();
				} else if(propertyName.equals("targetPath")) {
					targetPath = jsonParser.consumeStringValue();
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
	
	
	protected DubDependecyRef[] readDependencies(JsonReaderExt jsonParser) throws IOException {
		return new BundleDependenciesSegmentParser(jsonParser).parse();
	}
	
	protected class BundleDependenciesSegmentParser {
		
		protected JsonReaderExt jsonReader;
		
		public BundleDependenciesSegmentParser(JsonReaderExt jsonParser) {
			this.jsonReader = jsonParser;
		}
		
		public DubDependecyRef[] parse() throws IOException {
			if(jsonReader.peek() == JsonToken.BEGIN_OBJECT) 
				return parseRawDeps();
			
			return parseResolvedDeps();
		}
		
		public DubDependecyRef[] parseRawDeps() throws IOException, MalformedJsonException {
			jsonReader.consumeExpected(JsonToken.BEGIN_OBJECT);
			
			ArrayList<DubDependecyRef> deps = new ArrayList<>();
			
			while(jsonReader.hasNext()) {
				JsonToken tokenType = jsonReader.peek();
				
				if(tokenType != JsonToken.NAME) {
					jsonReader.sourceError("Expected key name, instead got: " + tokenType);
				}
				
				String depName = jsonReader.nextName();
				jsonReader.skipValue(); // Ignore value for now, TODO
				
				deps.add(new DubDependecyRef(depName, null));
			}
			jsonReader.consumeExpected(JsonToken.END_OBJECT);
			return ArrayUtil.createFrom(deps, DubDependecyRef.class);
		}
		
		public DubDependecyRef[] parseResolvedDeps() throws IOException, MalformedJsonException {
			jsonReader.consumeExpected(JsonToken.BEGIN_ARRAY);
			
			ArrayList<DubDependecyRef> deps = new ArrayList<>();
			
			while(jsonReader.hasNext()) {
				String depName = jsonReader.nextString();
				deps.add(new DubDependecyRef(depName, null));
			}
			jsonReader.consumeExpected(JsonToken.END_ARRAY);
			return ArrayUtil.createFrom(deps, DubDependecyRef.class);
		}
	}
	
}