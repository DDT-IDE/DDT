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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import dtool.dub.DubBundle.DubBundleException;

public abstract class CommonDubParser {
	
	protected static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return FileUtil.readStringFromFile(file, StringUtil.UTF8);
	}
	
	protected DubBundleException dubError;
	
	public CommonDubParser() {
	}
	
	protected void putError(String message) {
		if(dubError == null) {
			dubError = new DubBundleException(message);
		}
	}
	
	protected void parseFromSource(String source) throws DubBundleException {
		try(JsonReaderExt jsonParser = new JsonReaderExt(new StringReader(source))) {
			jsonParser.setLenient(true);
			
			readData(jsonParser);
			
			jsonParser.consumeExpected(JsonToken.END_DOCUMENT);
			assertTrue(jsonParser.peek() == JsonToken.END_DOCUMENT);
		} catch (IOException e) {
			throw new DubBundleException(e);
		}
	}
	
	protected abstract void readData(JsonReaderExt jsonParser) throws IOException;
	
	/** Utility extensions to JsonReader */
	public static class JsonReaderExt extends JsonReader {
		
		protected final JsonReaderExt jsonReader = this; 
		
		public JsonReaderExt(Reader in) {
			super(in);
		}
		
		public void consumeExpected(JsonToken expectedToken) throws IOException {
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
				throw new MalformedJsonException("Expected: " + expectedToken + " Got: " + tokenType);
			}
			return tokenType;
		}
		
		public String consumeStringValue() throws IOException {
			if(jsonReader.peek() != JsonToken.STRING) {
				throw new MalformedJsonException("Expected: " + JsonToken.STRING);
			}
			return jsonReader.nextString();
		}
		
		public ArrayList<String> consumeStringArray(boolean ignoreNulls) throws IOException {
			jsonReader.consumeExpected(JsonToken.BEGIN_ARRAY);
			
			ArrayList<String> strings = new ArrayList<>();
			
			while(jsonReader.hasNext()) {
				JsonToken tokenType = jsonReader.peek();
				
				if(ignoreNulls && tokenType == JsonToken.NULL) {
					jsonReader.nextNull();
					continue;
				}
				
				if(tokenType != JsonToken.STRING) {
					sourceError("Expected String value, instead got: " + tokenType);
				}
				
				String entry = jsonReader.nextString();
				strings.add(entry);
			}
			jsonReader.consumeExpected(JsonToken.END_ARRAY);
			return strings;
		}
		
		public void sourceError(String message) throws MalformedJsonException {
			// TODO: add source location to message.
			throw new MalformedJsonException(message);
		}
		
		public void errorUnexpected(JsonToken tokenType) throws MalformedJsonException {
			sourceError("Unexpected token: " + tokenType);
		}
		
		public String consumeExpectedPropName() throws IOException {
			JsonToken tokenType = jsonReader.peek();
			
			if(tokenType != JsonToken.NAME) {
				jsonReader.sourceError("Expected property name, instead got: " + tokenType);
			}
			
			return jsonReader.nextName(); 
			// Note: there is a bug in nextName, it throws IllegalStateException insteand of IOE
			// so that's why we check peek ourselves.
		}
		
	}
	
}