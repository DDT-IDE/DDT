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

import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import melnorme.utilbox.misc.MiscUtil;
import dtool.engine.DToolServer;
import dtool.genie.GenieServer.GenieCommandException;
import dtool.util.JsonReaderExt;
import dtool.util.JsonWriterExt;

public abstract class JsonCommandHandler {
	
	protected final String commandName;
	protected final GenieServer genieServer;
	
	public JsonCommandHandler(String commandName, GenieServer genieServer) {
		this.commandName = commandName;
		this.genieServer = genieServer;
	}
	
	public boolean canHandle(String requestName) {
		return areEqual(commandName, requestName);
	}
	
	protected DToolServer getDToolServer() {
		return genieServer.getDToolServer(); 
	}
	
	protected JsonReaderExt jsonParser;
	protected JsonWriterExt jsonWriter;
	
	public void processCommand(JsonReaderExt jsonParser, JsonWriterExt jsonWriter) throws IOException {
		this.jsonParser = jsonParser;
		this.jsonWriter = jsonWriter;
		
		parseCommandInput();
		try {
			processCommandResponse();
		} catch (GenieCommandException gce) {
			throw new IOException(gce);
		}
	}
	
	protected void processCommandResponse() throws IOException, GenieCommandException {
		jsonWriter.beginObject();
		jsonWriter.writeProperty("command", commandName);
		writeResponseJsonContents();
		jsonWriter.endObject();
		jsonWriter.flush();
	};
	
	protected abstract void writeResponseJsonContents() throws IOException, GenieCommandException;
	
	protected void parseCommandInput() throws IOException {
		jsonParser.skipValue();
	}
	
	/* ----------------- deserialize helpers: ----------------- */
	
	@SuppressWarnings("unchecked")
	public static <T> T validateType(Object value, String propName, Class<T> klass, boolean allowNull) 
			throws GenieCommandException {
		
		if(value == null) {
			if(allowNull) {
				return null;
			}
			throw validationError("Expected non-null value" + " for property: " + propName);
		}
		
		if(!klass.isInstance(value)) {
			throw validationError("Expected value of type " + klass.getSimpleName() + 
				" for property: " + propName + ", instead got: " + value.getClass().getSimpleName());
		}
		return (T) value;
	}
	
	protected static GenieCommandException validationError(String message) {
		return new GenieCommandException(message);
	}
	
	protected static <T> T getValue(Map<String, Object> map, String propName, Class<T> klass, boolean allowNull)
			throws GenieCommandException {
		Object value = map.get(propName);
		return validateType(value, propName, klass, allowNull);
	}
	
	protected static String getString(Map<String, Object> map, String propName) throws GenieCommandException {
		return getValue(map, propName, String.class, false);
	}
	protected static String getStringOrNull(Map<String, Object> map, String propName) throws GenieCommandException {
		return getValue(map, propName, String.class, true);
	}
	
	protected static int getInt(Map<String, Object> map, String propName) throws GenieCommandException {
		return getValue(map, propName, Integer.class, false);
	}
	
	protected static boolean getBoolean(Map<String, Object> map, String propName) throws GenieCommandException {
		return getValue(map, propName, Boolean.class, false);
	}
	
	protected static Path getPath(Map<String, Object> map, String propName) throws GenieCommandException {
		String pathString = getString(map, propName);
		Path path = MiscUtil.createPathOrNull(pathString);
		if(path == null) {
			throw validationError("Invalid path: " + pathString);
		}
		return path;
	}
	
}