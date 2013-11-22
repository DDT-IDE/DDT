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

/**
 * Parse a Dub bundle in a filesystem location into an in-memory description of the bundle.
 */
public class DeeBundleParser {
	
	@SuppressWarnings("serial")
	public static class DubBundleException extends Exception {
		
		public DubBundleException(String message) {
	        super(message);
	    }
		
	}
	
	protected static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return new String(FileUtil.readBytesFromFile(file), StringUtil.UTF8);
	}
	
	protected void semanticError(JsonParserHelper jsonParser, String message) throws DubBundleException {
		throw new DubBundleException(message);
	}
	
	public DubBundle parseDubBundle(Path location) throws IOException, MalformedJsonException, DubBundleException {
		File jsonLocation = location.resolve("package.json").toFile();
		
		String source = readStringFromFile(jsonLocation);
		
//		System.out.print("----\n" + source);
//		System.out.println("----");
		
		String bundleName = null;
		String version = null;
		Path[] srcFolders = null;
		Path[] autoSrcFolders = null;
		Object[] dependencies = null;
		
		
		try(JsonParserHelper jsonParser = new JsonParserHelper(new StringReader(source))) {
			
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
						dependencies = readDependencies(jsonParser);
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
		}
		
		if(bundleName == null)
			throw new DubBundleException("Bundle name not defined");
		
//		if(version == null)
//			version = "~master"; // Perhaps keep null?
		
		if(srcFolders == null) {
			autoSrcFolders = searchImplicitSrcFolders(location);
		}
		
		return new DubBundle(bundleName, version, location, srcFolders, autoSrcFolders, dependencies);
	}
	
	protected void errorUnexpected(JsonToken tokenType) throws MalformedJsonException {
		throw new MalformedJsonException("Unexpected token: " + tokenType);
	}
	
	protected Path[] readSourcePaths(JsonParserHelper jsonParser) throws IOException, DubBundleException {
		ArrayList<String> stringArray = consumeStringArray(jsonParser);
		
		ArrayList<Path> pathArray = new ArrayList<>();
		for (String string : stringArray) {
			pathArray.add(Paths.get(string));
		}
		
		return ArrayUtil.createFrom(pathArray, Path.class);
	}
	
	protected ArrayList<String> consumeStringArray(JsonParserHelper jsonParser) throws IOException, 
		DubBundleException {
		jsonParser.consumeExpected(JsonToken.BEGIN_ARRAY);
		
		ArrayList<String> strings = new ArrayList<>();
		
		while(jsonParser.hasNext()) {
			JsonToken tokenType = jsonParser.peek();
			
			if(tokenType != JsonToken.STRING) {
				semanticError(jsonParser, "Expected String value, instead got: " + tokenType);
			} 
			
			String entry = jsonParser.nextString();
			strings.add(entry);
		}
		jsonParser.consumeExpected(JsonToken.END_ARRAY);
		return strings;
	}
	
	protected Object[] readDependencies(JsonParserHelper jsonParser) throws IOException {
		jsonParser.skipValue();
		return null;
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