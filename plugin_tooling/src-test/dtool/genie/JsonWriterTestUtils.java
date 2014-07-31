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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tests.CommonTest;
import dtool.util.JsonReaderExt;
import dtool.util.JsonWriterExt;

public class JsonWriterTestUtils extends CommonTest {
	
	@SafeVarargs
	public static HashMap<String, Object> jsObject(SimpleEntry<String, Object>... entries) {
		HashMap<String, Object> hashMap = new HashMap<>();
		
		for (SimpleEntry<String,Object> entry : entries) {
			hashMap.put(entry.getKey(), entry.getValue());
		}
		
		return hashMap;
	}
	
	public static SimpleEntry<String, Object> entry(String name, Object value) {
		return new SimpleEntry<>(name, value);
	}
	
	/* -----------------  ----------------- */
	
	public static String jsWriteObject(Map<String, Object> map) {
		StringWriter sb = new StringWriter();
		try {
			jsWriteObject(map, sb);
		} catch (IOException e) {
			throw assertUnreachable();
		}
		return sb.toString();
	}
	
	public static void jsWriteObject(Map<String, Object> map, StringWriter sb) throws IOException {
		JsonWriterExt jsonWriter = new JsonWriterExt(sb);
		jsWriteObject(map, jsonWriter);
	}
	
	public static void jsWriteObject(Map<String, Object> map, JsonWriterExt jsonWriter) throws IOException {
		jsonWriter.beginObject();
		
		for(Entry<String, Object> entry : map.entrySet()){
			jsonWriter.name(entry.getKey());
			jsWriteValue(entry.getValue(), jsonWriter);
		}
		jsonWriter.endObject();
	}
	
	public static void jsWriteValue(Object value, JsonWriterExt jsonWriter) throws IOException {
		if(value == null) {
			jsonWriter.nullValue();
		} else if(value instanceof String) {
			jsonWriter.value((String) value);
		} else if(value instanceof Number) {
			jsonWriter.value((Number) value);
		} else if(value instanceof Map) {
			jsWriteObject(CoreUtil.<Map<String, Object>>blindCast(value), jsonWriter);
		} else if(value instanceof List) {
			assertFail(); // TODO
		} else {
			throw assertFail();
		}
	}
	
	/* ----------------- reading helpers ----------------- */
	
	public static HashMap<String,Object> readObject(Reader source) throws IOException {
		JsonReaderExt jsonParser = new JsonReaderExt(source);
		return JsonReaderExt.readJsonObject(jsonParser);
	}
	
	public static HashMap<String, Object> readJsonObject(String source) {
		try {
			return new JsonReaderExt(new StringReader(source)).readJsonObject();
		} catch (IOException e) {
			throw assertUnreachable();
		}
	}
	
}