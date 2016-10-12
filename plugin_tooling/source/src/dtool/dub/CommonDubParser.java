/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import dtool.dub.DubBundle.DubBundleException;
import dtool.util.JsonReaderExt;

public abstract class CommonDubParser {
	
	public static final String MSG_JSON_PARSE_ERROR = "JSON parse error: ";
	
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
		} catch (MalformedJsonException e) {
			// XXX: this could be abstract in a better way:
			throw new DubBundleException(MSG_JSON_PARSE_ERROR + e.getMessage());
		} catch (IOException e) {
			throw new DubBundleException(e);
		}
		
	}
	
	protected abstract void readData(JsonReaderExt jsonParser) throws IOException, DubBundleException;
	
}