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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import dtool.dub.DubBundle.DubBundleException;

/**
 * Parse a Dub bundle in a filesystem location into an in-memory description of the bundle.
 */
public class DubBundleParser {
	
	public static DubBundle parseDubBundleFromLocation(Path location) {
		return new DubBundleParser(location).parseDubBundle();
	}
	
	protected final Path location;
	protected String bundleName = null;
	protected String version = null;
	protected Path[] srcFolders = null;
	protected Path[] autoSrcFolders = null;
	protected Object[] dependencies = null;
	protected DubBundleException dubError;
	
	protected DubBundleParser(Path location) {
		this.location = location;
	}
	
	protected void semanticError(JsonParserHelper jsonParser, String message) throws DubBundleException {
		throw new DubBundleException(message);
	}
	
	protected static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return FileUtil.readStringFromFile(file, StringUtil.UTF8);
	}
	
	protected DubBundle parseDubBundle() {
		parseDubBundleData();
		if(bundleName == null) {
			bundleName = location.getFileName().toString();
			if(dubError == null) {
				dubError = new DubBundleException("Bundle name not defined");
			}
		}
		
		return new DubBundle(location, bundleName, version, srcFolders, autoSrcFolders, dependencies, dubError);
	}
	
	protected void parseDubBundleData() {
		File jsonLocation = location.resolve("package.json").toFile();
		String source;
		try {
			source = readStringFromFile(jsonLocation);
		} catch (IOException e) {
			dubError = new DubBundleException(e);
			return;
		}
		
		try(JsonParserHelper jsonParser = new JsonParserHelper(new StringReader(source))) {
			jsonParser.setLenient(true);
			
			jsonParser.consumeExpected(JsonToken.BEGIN_OBJECT);
			
			while(jsonParser.hasNext()) {
				JsonToken tokenType = jsonParser.peek();
				
				if(tokenType == JsonToken.NAME) {
					String propertyName = jsonParser.nextName();
					
					if(propertyName.equals("name")) {
						bundleName = jsonParser.consumeStringValue();
					} else if(propertyName.equals("version")) {
						version = jsonParser.consumeStringValue();
					} else if(propertyName.equals("sourcePaths")) {
						srcFolders = readSourcePaths(jsonParser);
					} else if(propertyName.equals("depedencies")) {
						readDependencies(jsonParser);
					} else {
						jsonParser.skipValue();
					}
				} else {
					errorUnexpected(tokenType);
				}
			}
			
			jsonParser.consumeExpected(JsonToken.END_OBJECT);
			jsonParser.consumeExpected(JsonToken.END_DOCUMENT);
			assertTrue(jsonParser.peek() == JsonToken.END_DOCUMENT);
			
			if(srcFolders == null) {
				autoSrcFolders = searchImplicitSrcFolders(location);
			}
			
//			if(version == null)
//			version = "~master"; // Perhaps keep null?
			
		} catch (IOException e) {
			dubError = new DubBundleException(e);
		} catch (DubBundleException e) {
			dubError = e;
		}
	}
	
	protected void errorUnexpected(JsonToken tokenType) throws MalformedJsonException {
		throw new MalformedJsonException("Unexpected token: " + tokenType);
	}
	
	protected Path[] readSourcePaths(JsonParserHelper jsonParser) throws IOException, DubBundleException {
		ArrayList<String> stringArray = consumeStringArray(jsonParser, true);
		
		ArrayList<Path> pathArray = new ArrayList<>();
		for (String string : stringArray) {
			pathArray.add(Paths.get(string));
		}
		
		return ArrayUtil.createFrom(pathArray, Path.class);
	}
	
	protected ArrayList<String> consumeStringArray(JsonParserHelper jsonParser, boolean ignoreNulls) throws IOException, 
		DubBundleException {
		jsonParser.consumeExpected(JsonToken.BEGIN_ARRAY);
		
		ArrayList<String> strings = new ArrayList<>();
		
		while(jsonParser.hasNext()) {
			JsonToken tokenType = jsonParser.peek();
			
			if(ignoreNulls && tokenType == JsonToken.NULL) {
				jsonParser.nextNull();
				continue;
			}
			
			if(tokenType != JsonToken.STRING) {
				semanticError(jsonParser, "Expected String value, instead got: " + tokenType);
			}
			
			String entry = jsonParser.nextString();
			strings.add(entry);
		}
		jsonParser.consumeExpected(JsonToken.END_ARRAY);
		return strings;
	}
	
	protected void readDependencies(JsonParserHelper jsonParser) throws IOException {
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
					implicitFolders.add(Paths.get(name));
				}
				return false;
			}
		});
		
		return ArrayUtil.createFrom(implicitFolders, Path.class);
	}
	
	public static class JsonParserHelper extends JsonReader {
		
		protected final JsonReader jsonReader = this; 
		
		public JsonParserHelper(Reader in) {
			super(in);
		}
		
		protected void consumeExpected(JsonToken expectedToken) throws IOException {
			JsonToken tokenType = checkNext(expectedToken);
			if(tokenType == JsonToken.BEGIN_OBJECT) {
				jsonReader.beginObject();
			} else if(tokenType == JsonToken.END_OBJECT) {
				jsonReader.endObject();
			} else if(tokenType == JsonToken.BEGIN_ARRAY) {
				jsonReader.beginArray();
			} else if(tokenType == JsonToken.END_ARRAY) {
				jsonReader.endArray();
			} else if(tokenType == JsonToken.END_DOCUMENT) {
			} else {
				assertFail();
			}
		}
		
		protected JsonToken checkNext(JsonToken expectedToken) throws IOException,
				MalformedJsonException {
			JsonToken tokenType = jsonReader.peek();
			if(tokenType != expectedToken) {
				throw new MalformedJsonException("Expected: " + expectedToken);
			}
			return tokenType;
		}
		
		protected String consumeStringValue() throws IOException {
			if(jsonReader.peek() != JsonToken.STRING) {
				throw new MalformedJsonException("Expected: " + JsonToken.STRING);
			}
			return jsonReader.nextString();
		}
		
	}

}