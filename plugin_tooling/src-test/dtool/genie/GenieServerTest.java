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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.StringUtil.UTF8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import dtool.engine.DToolServer;
import dtool.engine.operations.FindDefinitionOperation_Test;
import dtool.genie.GenieServer.GenieCommandException;
import dtool.genie.cmdline.FindDefinitionRequest;
import dtool.genie.cmdline.FindDefinitionRequest.FindDefinitionResultParser;
import dtool.genie.cmdline.ShutdownServerRequest;
import dtool.resolver.api.FindDefinitionResult;
import dtool.util.JsonReaderExt;
import dtool.util.JsonWriterExt;
import dtool.util.StatusException;

public class GenieServerTest extends JsonWriterTestUtils {
	
	public static void shutdownSocketOutput(Socket socket) {
		try {
			socket.shutdownOutput();
		} catch (IOException e) {
			throw assertFail();
		}
	}
	
	public static void writeStreamMessage(Writer serverOutput, String clientRequest) {
		try {
			serverOutput.write(clientRequest);
			serverOutput.flush();
		} catch (IOException e) {
			assertFail();
		}
	}
	
	protected static void logClientMessage(String message) {
		System.out.println(message);
		System.out.flush();
	}
	
	/* ----------------- ----------------- */
	
	protected TestsGenieServer genieServer;
	
	protected void prepareGenieServer() {
		try {
			if(genieServer != null) {
				genieServer.serverSocket.close();
			}
			GenieServer.getSentinelFile().delete();
			genieServer = new TestsGenieServer(0);
		} catch (Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		new Thread() { 
			{
				setName("TestsGenieServer");
			}
			@Override
			public void run() {
				genieServer.runServer();
			}
		}.start();
	}
	
	public static class TestsGenieServer extends GenieServer {
		
		public static final DToolServer DTOOL_SERVER = new DToolServer();
		
		protected final ArrayList<Throwable> exceptions = new ArrayList<>();
		
		public TestsGenieServer(int portNumber) throws StatusException {
			super(DTOOL_SERVER, portNumber);
		}
		
		@Override
		public void logError(String message, Throwable throwable) {
			super.logError(message, throwable);
			exceptions.add(throwable);
		}
		
		protected void shutdownAndAwait() throws InterruptedException {
			shutdown();
			awaitTerminationOfActiveConnectionHandlers();
			terminationLatch.await();
		}
		
		protected void awaitTerminationOfActiveConnectionHandlers() {
			connectionsSemaphore.acquireUninterruptibly(MAX_CONNECTIONS);
			logClientMessage("All active connections terminated.");
			connectionsSemaphore.release(MAX_CONNECTIONS);
		}
		
	}
	
	protected Socket socket;
	protected OutputStreamWriter serverInput;
	protected BufferedReader serverOutput;

	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		prepareGenieServer();
		cleanup(); // Test cleanup methods
		
		testGenieServer$();
	}
	
	@After
	public void cleanup() throws InterruptedException {
		cleanClientConnection();
		if(genieServer != null) {
			genieServer.shutdownAndAwait();
			assertTrue(genieServer.serverSocket.isClosed());
			assertTrue(genieServer.exceptions.isEmpty());
		}
		genieServer = null;
	}
	
	protected void prepareServerConnection() {
		try {
			cleanClientConnection();
			
			int serverPortNumber = genieServer.getServerPortNumber();
			logClientMessage("---->> Preparing new client connection. " 
					+ " Active server connections: " + genieServer.getActiveConnections());
			
			socket = ShutdownServerRequest.createLocalConnection(serverPortNumber);
			serverInput = new OutputStreamWriter(socket.getOutputStream(), UTF8);
			serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF8));
		} catch (IOException e) {
			assertFail();
		}
	}
	
	protected void cleanClientConnection(){
		if(socket != null) {
			try {
				serverInput.flush();
				socket.close();
				socket = null;
			} catch (IOException e) {
				assertFail();
			}
		}
	}
	
	protected HashMap<String, Object> sendCommandAndReadResponse(Map<String, Object> jsObject) {
		sendCommand(serverInput, jsWriteObject(jsObject));
		return readResponseObject();
	}
	
	protected HashMap<String, Object> readResponseObject() {
		try {
			return readObject(serverOutput);
		} catch (IOException e) {
			throw assertFail();
		}
	}
	
	protected void sendCommand(Writer connectionOut, String jsDocument) {
		logClientMessage(">> Sending command message:\n" + jsDocument);
		
		try {
			connectionOut.write(jsDocument);
			connectionOut.flush();
		} catch (IOException e) {
			assertFail();
		}
	}
	
	protected HashMap<String, Object> response;
	
	
	public void testGenieServer$() {
		prepareGenieServer();
		prepareServerConnection();
		
		response = sendCommandAndReadResponse(jsObject(entry("about", null)));
		
		assertAreEqual(response.get("engine"), GenieServer.ENGINE_NAME);
		assertAreEqual(response.get("version"), GenieServer.ENGINE_VERSION);
		assertAreEqual(response.get("protocol_version"), "0.1");
		assertTrue(genieServer.exceptions.isEmpty());
		
		// Test invalid commands
		testError(jsObject(entry("invalid_command", null)), "unknown command");
		
		// Test invalid message json
		testMessageInvalidJson("{ }", "expected property name");
		testMessageInvalidJson("{ ] }", "expected ':'");
		
		// Test disconnect during message
		writeStreamMessage(serverInput, "{ \"about\" : ");
		shutdownSocketOutput(socket);
		awaitConnectionHandlerTermination();
		checkServerExceptions("End of input");
		prepareServerConnection();
		
		// Test shutdown server
		String responseString = new ShutdownServerRequest().performAndGetResponse();
		HashMap<String, Object> responseObject = readJsonObject(responseString);
		assertTrue(responseObject.containsKey("error") == false);
		shutdownSocketOutput(socket);
		genieServer.awaitTerminationOfActiveConnectionHandlers();
		assertTrue(genieServer.serverSocket.isClosed());
		assertTrue(!GenieServer.getSentinelFile().exists());
	}
	
	public void testError(Map<String, Object> clientRequest, String expectedContains) {
		assertTrue(socket.isClosed() == false);
		
		HashMap<String, Object> response = sendCommandAndReadResponse(clientRequest);
		String errorMsg = assertCast(response.get("error"), String.class);
		assertStringContains(errorMsg.toLowerCase(), expectedContains);
		assertTrue(genieServer.exceptions.size() == 1);
		assertStringContains(genieServer.exceptions.get(0).getMessage().toLowerCase(), expectedContains);
		genieServer.exceptions.clear();
	}
	
	public void testMessageInvalidJson(String clientRequest, String expectedErrorContains) {
		assertTrue(socket.isClosed() == false);
		
		writeStreamMessage(serverInput, clientRequest);
		
		awaitConnectionHandlerTermination();
		checkServerExceptions(expectedErrorContains);
	}
	
	protected void awaitConnectionHandlerTermination() {
		try {
			while(serverOutput.read() != -1) {
			}
			socket.close();
		} catch (IOException e) {
		}
		genieServer.awaitTerminationOfActiveConnectionHandlers();
	}
	
	protected void checkServerExceptions(String expectedErrorContains) {
		assertTrue(genieServer.exceptions.size() == 1);
		Throwable exception = genieServer.exceptions.get(0);
		assertStringContains(exception.getMessage().toLowerCase(), expectedErrorContains.toLowerCase());
		genieServer.exceptions.clear();
		prepareServerConnection();
	}
	
	@Test
	public void testSemanticOps() throws Exception { testSemanticOps$(); }
	public void testSemanticOps$() throws Exception {
		prepareGenieServer();
		
		new FindDefinitionOperation_GenieTest().testALL();
		
		prepareServerConnection();
		new FindDefinitionOperation_ReuseConnectionTest().testALL();
	}
	
	public class FindDefinitionOperation_GenieTest extends FindDefinitionOperation_Test {
		
		@Override
		protected void prepEngineServer() {
			// Don't create DToolServer class
		}
		
		@Override
		protected FindDefinitionResult doOperation(Path filePath, int offset) throws GenieCommandException {
			try {
				String response = new FindDefinitionRequest().setArguments(filePath, offset).performAndGetResponse();
				return new FindDefinitionResultParser().read(new JsonReaderExt(new StringReader(response)));
			} catch (IOException e) {
				throw assertFail();
			}
		}
		
	}
	
	public class FindDefinitionOperation_ReuseConnectionTest extends FindDefinitionOperation_GenieTest {
		
		@Override
		protected FindDefinitionResult doOperation(Path filePath, int offset) throws GenieCommandException {
			try {
				new FindDefinitionRequest().setArguments(filePath, offset).
					writeRequest(new JsonWriterExt(serverInput));
				
				return new FindDefinitionResultParser().read(new JsonReaderExt(serverOutput));
			} catch (IOException e) {
				throw assertFail();
			}
		}
		
	}
	
}