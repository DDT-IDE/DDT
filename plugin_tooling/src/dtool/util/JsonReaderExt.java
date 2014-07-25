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
package dtool.util;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

/** Utility extensions to JsonReader */
public class JsonReaderExt extends JsonReader {
	
	protected final JsonReaderExt jsonReader = this; 
	
	public JsonReaderExt(Reader in) {
		super(in);
	}
	
	public void consumeExpected(JsonToken expectedToken) throws IOException {
		JsonToken tokenType = validateExpectedToken(expectedToken);
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
	
	protected JsonToken validateExpectedToken(JsonToken expectedToken) throws IOException, MalformedJsonException {
		JsonToken tokenType = jsonReader.peek();
		if(tokenType != expectedToken) {
			throw new MalformedJsonException("Expected: " + expectedToken + " Got: " + tokenType);
		}
		return tokenType;
	}
	
	public boolean tryConsume(JsonToken jsonToken) throws IOException {
		if(peek() == jsonToken) {
			consumeExpected(jsonToken);
			return true;
		}
		return false;
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
		
		// Note: there is a bug in nextName, it throws IllegalStateException insteand of IOE
		// so that's why we check peek ourselves.
		return jsonReader.nextName(); 
	}
	
	public String consumeExpectedName() throws IOException {
		return consumeExpectedPropName();
	}
	
	public boolean isEOF() throws IOException {
		try {
			if(peek() == JsonToken.END_DOCUMENT) {
				return true;
			}
		} catch (EOFException eof) {
			// This exception is ok. Because of a bug, END_DOCUMENT is sometimes not reported.
			return true;
		}
		
		return false;
	}
	
}