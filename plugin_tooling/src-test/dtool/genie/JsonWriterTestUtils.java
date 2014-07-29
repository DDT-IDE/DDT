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
package dtool.genie;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.tests.CommonTest;

import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import dtool.util.JsonReaderExt;

public class JsonWriterTestUtils extends CommonTest {
	
	public static String jsObject(CharSequence... entries) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		boolean first = true;
		for(Object item : entries){
			if(!first)
				sb.append(",");
			first = false;
			sb.append(item);
		}
		sb.append("}");
		
		return sb.toString();
	}
	
	public static CharSequence jsEntry(String name, Object value) {
		StringWriter sw = new StringWriter();
		
		try {
			JsonWriter.stringValue(name, sw);
			sw.append(" : ");
			sw.append(jsValue(value));
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		
		return sw.toString();
	}
	
	public static CharSequence jsValue(Object value) {
		if(value == null) {
			return jsNull();
		}
		if(value instanceof String) {
			return jsString((String) value);
		}
		if(value instanceof Number) {
			Number number = (Number) value;
			return jsString(number.toString());
		}
		if(value instanceof List) {
			assertFail(); // TODO
		}
		if(value instanceof Map) {
			assertFail(); // TODO
		}
		return jsString(value.toString());
	}
	
	public static CharSequence jsNull() {
		return "null";
	}
	
	public static CharSequence jsString(String value) {
		StringWriter sw = new StringWriter();
		try {
			JsonWriter.stringValue(value, sw);
		} catch (IOException e) {
			throw assertFail();
		}
		return sw.toString();
	}
	
	/* ----------------- deserialize ----------------- */
	
	public static HashMap<String,Object> readObject(Reader source) throws IOException {
		JsonReaderExt jsonParser = new JsonReaderExt(source);
		return readObject(jsonParser);
	}
	
	public static HashMap<String, Object> readObject(JsonReaderExt jsonParser) throws IOException {
		jsonParser.consumeExpected(JsonToken.BEGIN_OBJECT);
		
		HashMap<String, Object> jsonObject = new HashMap<>();
		
		while(jsonParser.tryConsume(JsonToken.END_OBJECT) == false) {
			String propName = jsonParser.consumeExpectedPropName();
			Object propvalue = readValue(jsonParser);
			jsonObject.put(propName, propvalue);
		}
		
		return jsonObject;
	}
	
	public static Object readValue(JsonReaderExt jsonParser) throws IOException {
		switch (jsonParser.peek()) {
		case NULL:
			jsonParser.nextNull(); return null;
		case BOOLEAN:
			return jsonParser.nextBoolean();
		case NUMBER:
			return jsonParser.nextLong();
		case STRING:
			return jsonParser.nextString();
		case BEGIN_ARRAY:
			jsonParser.skipValue(); // TODO
			return null;
		case BEGIN_OBJECT:
			return readObject(jsonParser);
		case END_ARRAY:
		case END_OBJECT:
		case END_DOCUMENT:
		case NAME:
			jsonParser.sourceError("Invalid JSON token");
			return null;
		}
		throw assertUnreachable();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T validateType(Object value, Class<T> klass, String propName) {
		if(!klass.isInstance(value)) {
			throw new GenieCommandException("Expected value of type " + klass.getSimpleName() + 
				" for property: " + propName);
		}
		return (T) value;
	}
	
	@SuppressWarnings("serial")
	public static class GenieCommandException extends RuntimeException {
		
		public GenieCommandException(String message) {
			super(message);
		}
		
	}
	
	protected static String getString(Map<String, Object> map, String propName) {
		Object value = map.get(propName);
		return validateType(value, String.class, propName);
	}
	
	protected static String getStringOrNull(Map<String, Object> map, String propName) {
		Object value = map.get(propName);
		if(value == null) {
			return null;
		}
		return validateType(value, String.class, propName);
	}
	
	protected static int getInt(Map<String, Object> map, String propName) {
		Object object = map.get(propName);
		Integer number = validateType(object, Integer.class, propName);
		return number.intValue();
	}
	
	protected static boolean getFlag(Map<String, Object> map, String propName) {
		Object value = map.get(propName);
		if(value == null) 
			return false;
		return validateType(value, Boolean.class, propName);
	}
	
	protected static Path getPath(Map<String, Object> map, String propName) {
		String pathString = getString(map, propName);
		Path path = MiscUtil.createPathOrNull(pathString);
		if(path == null) {
			throw new GenieCommandException("Invalid path: " + pathString);
		}
		return path;
	}
	
}