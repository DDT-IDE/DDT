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
import static melnorme.utilbox.core.CoreUtil.array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import melnorme.utilbox.misc.StringUtil;

import com.google.gson.stream.JsonToken;

import dtool.util.JsonReaderExt;
import dtool.util.JsonWriterExt;

public class GenieServer extends AbstractSocketServer {
	
	public static final String ENGINE_NAME = "D Tool Genie";
	public static final String ENGINE_VERSION = "0.1.0";
	public static final String ENGINE_PROTOCOL_VERSION = "0.1";
	
	public GenieServer(int portNumber) throws IOException {
		super(portNumber);
	}
	
	@Override
	protected GenieConnectionHandler createConnectionHandlerRunnable(Socket clientSocket) {
		return new GenieConnectionHandler(clientSocket);
	}
	
	@Override
	protected Thread createHandlerThread(ConnectionHandlerRunnable genieConnectionHandler) {
		Thread thread = super.createHandlerThread(genieConnectionHandler);
		thread.setName("GenieConnectionHandler:" + genieConnectionHandler.clientSocket.getPort());
		return thread;
	}
	
	public class GenieConnectionHandler extends ConnectionHandlerRunnable {
		
		public GenieConnectionHandler(Socket clientSocket) {
			super(clientSocket);
		}
		
		@Override
		protected void doHandleConnectionStream() throws IOException {
			try(
				BufferedReader serverInput = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), StringUtil.UTF8)); 
				OutputStreamWriter serverResponse = 
					new OutputStreamWriter(clientSocket.getOutputStream(), StringUtil.UTF8);
				JsonReaderExt jsonParser = new JsonReaderExt(serverInput);
				JsonWriterExt jsonWriter = new JsonWriterExt(serverResponse);
			) {
				jsonParser.setLenient(true);
				jsonWriter.setLenient(true);
				
				while(true) {
					processJsonMessage(jsonParser, jsonWriter);
				}
			}
		}
		
	}
	
	protected final JsonCommandHandler[] commandHandlers = array(
		new AboutCommandHandler()
	);
	
	protected void processJsonMessage(JsonReaderExt jsonParser, JsonWriterExt jsonWriter) throws IOException {
		jsonParser.consumeExpected(JsonToken.BEGIN_OBJECT);
		try {
			
			String commandName = jsonParser.consumeExpectedName();
			
			for (JsonCommandHandler commandHandler : commandHandlers) {
				if(commandHandler.canHandle(commandName)) {
					commandHandler.processCommand(jsonParser, jsonWriter);
					return;
				}
			}
			
			new JsonCommandHandler(commandName) {
				@Override
				protected void writeResponseJsonContents() throws IOException {
					String msg = "Unknown command: " + commandName;
					jsonWriter.writeProperty("error", msg);
					logException(msg, new Exception(msg));
				};
			}.processCommand(jsonParser, jsonWriter);
			
		} finally {
			jsonParser.consumeExpected(JsonToken.END_OBJECT);
		}
		
	}
	
	public static abstract class JsonCommandHandler {
		
		protected final String commandName;
		
		public JsonCommandHandler(String commandName) {
			this.commandName = commandName;
		}
		
		public boolean canHandle(String requestName) {
			return areEqual(commandName, requestName);
		}
		
		protected JsonReaderExt jsonParser;
		protected JsonWriterExt jsonWriter;
		
		public void processCommand(JsonReaderExt jsonParser, JsonWriterExt jsonWriter) throws IOException {
			this.jsonParser = jsonParser;
			this.jsonWriter = jsonWriter;
			
			parseCommandInput();
			processCommandResponse();
		}
		
		protected void processCommandResponse() throws IOException {
			jsonWriter.beginObject();
			jsonWriter.writeProperty("command", commandName);
			writeResponseJsonContents();
			jsonWriter.endObject();
			jsonWriter.flush();
		};
		
		protected abstract void writeResponseJsonContents() throws IOException;
		
		protected void parseCommandInput() throws IOException {
			jsonParser.skipValue();
		}
		
	}
	
	public static class AboutCommandHandler extends JsonCommandHandler {
		
		public AboutCommandHandler() {
			super("about");
		}
		
		@Override
		protected void writeResponseJsonContents() throws IOException {
			jsonWriter.writeProperty("engine", ENGINE_NAME);
			jsonWriter.writeProperty("version", ENGINE_VERSION);
			jsonWriter.writeProperty("protocol_version", ENGINE_PROTOCOL_VERSION);
		}
		
	}
	
}