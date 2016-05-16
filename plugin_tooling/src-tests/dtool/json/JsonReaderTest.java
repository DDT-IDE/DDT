/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.json;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.StringReader;
import java.util.regex.Pattern;

import org.junit.Test;

import dtool.util.JsonReaderExt;
import melnorme.utilbox.tests.CommonTest;

public class JsonReaderTest extends CommonTest {
	
	
	@Test
	public void test_lenient_parsing() throws Exception { test_lenient_parsing$(); }
	public void test_lenient_parsing$() throws Exception {
		
		try (JsonReaderExt jsonReaderExt = jsonReader(" { |name|: |dub_foo|, } ")) {
			
			jsonReaderExt.beginObject();
			jsonReaderExt.nextName();
			jsonReaderExt.nextString();
			jsonReaderExt.endObject();
			
		} catch(Exception e) {
			throw e;
		}
		
		
		try(JsonReaderExt jsonReaderExt = jsonReader(" { , } ")) {
			
			jsonReaderExt.beginObject();
			jsonReaderExt.endObject();
			assertFail();
			
		} catch(Exception e) {
			assertTrue(e.getMessage().contains("Expected name"));
			// continue;
		}
		
		try (JsonReaderExt jsonReaderExt = jsonReader(" { |name|: { |name|: |dub_foo|, } , } ")) {
			
			jsonReaderExt.beginObject();
			jsonReaderExt.nextName();

			jsonReaderExt.beginObject();
			jsonReaderExt.nextName();
			jsonReaderExt.nextString();
			jsonReaderExt.endObject();
			
			jsonReaderExt.endObject();
			
		} catch(Exception e) {
			throw e;
		}
		
	}
	
	protected JsonReaderExt jsonReader(String tplSource) {
		String source = tplSource.replaceAll(Pattern.quote("|"), "\"");
		JsonReaderExt jsonReaderExt = new JsonReaderExt(new StringReader(source));
		jsonReaderExt.setLenient(true);
		return jsonReaderExt;
	}
	
}