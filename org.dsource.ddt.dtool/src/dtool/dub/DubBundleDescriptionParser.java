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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;

import com.google.gson.stream.JsonToken;

import dtool.dub.DubBundle.DubBundleDescription;
import dtool.dub.DubBundle.DubBundleException;

public class DubBundleDescriptionParser extends CommonDubParser {
	
	protected String bundleName;
	protected ArrayList<DubBundle> bundles;
	
	public DubBundleDescriptionParser() {
	}
	
	public DubBundleDescription parseDescription(String source) {
		try {
			parseFromSource(source);
		} catch (DubBundleException e) {
			dubError = e;
		}
		
		if(bundleName == null) {
			putError(new DubBundleException("Expected \"mainPackage\" entry."));
		}
		
		if(dubError == null) {
			assertNotNull(bundles);
			// check for errors during bundle parsing
			for (DubBundle bundle : bundles) {
				if(bundle.hasErrors()) {
					putError(bundle.error);
					break;
				}
			}
		}
		
		return new DubBundleDescription(bundleName, ArrayUtil.createFrom(bundles, DubBundle.class), dubError);
	}

	@Override
	protected void readData(JsonReaderExt jsonParser) throws IOException {
		
		jsonParser.consumeExpected(JsonToken.BEGIN_OBJECT);
		
		while(jsonParser.hasNext()) {
			JsonToken tokenType = jsonParser.peek();
			
			if(tokenType == JsonToken.NAME) {
				String propertyName = jsonParser.nextName();
				
				if(propertyName.equals("mainPackage")) {
					bundleName = jsonParser.consumeStringValue();
				} else if(propertyName.equals("packages")) {
					bundles = readBundles(jsonParser);
				} else {
					jsonParser.skipValue();
				}
			} else {
				jsonParser.errorUnexpected(tokenType);
			}
		}
		
		jsonParser.consumeExpected(JsonToken.END_OBJECT);
	}
	
	protected static ArrayList<DubBundle> readBundles(JsonReaderExt jsonParser) throws IOException {
		jsonParser.consumeExpected(JsonToken.BEGIN_ARRAY);
		
		ArrayList<DubBundle> bundles = new ArrayList<>();
		
		while(jsonParser.hasNext()) {
			JsonToken tokenType = jsonParser.peek();
			
			if(tokenType == JsonToken.BEGIN_OBJECT) {
				DubBundle bundle = new DubBundleParser().readBundle(jsonParser).createBundle(null);
				bundles.add(bundle);
			} else {
				jsonParser.errorUnexpected(tokenType);
			}
		}
		
		jsonParser.consumeExpected(JsonToken.END_ARRAY);
		return bundles;
	}
	
}