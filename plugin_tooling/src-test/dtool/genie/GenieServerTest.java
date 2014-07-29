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
import static melnorme.utilbox.core.CoreUtil.blindCast;
import static melnorme.utilbox.misc.StringUtil.UTF8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import dtool.ast.SourceRange;
import dtool.engine.operations.FindDefinitionOperation_Test;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;

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
			genieServer = new TestsGenieServer(0);
		} catch (IOException e) {
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
		
		protected final ArrayList<Throwable> exceptions = new ArrayList<>();
		
		public TestsGenieServer(int portNumber) throws IOException {
			super(portNumber);
		}
		
		@Override
		public void logException(String message, Throwable e) {
			super.logException(message, e);
			exceptions.add(e);
		}
		
		protected void shutdownAndAwait() throws InterruptedException {
			closeServerSocket();
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
		
		testGenieServer____________();
	}
	
	@After
	public void cleanup() throws InterruptedException {
		cleanClientConnection();
		genieServer.shutdownAndAwait();
		assertTrue(genieServer.serverSocket.isClosed());
		assertTrue(genieServer.exceptions.isEmpty());
		genieServer = null;
	}
	
	protected void prepareServerConnection() {
		try {
			cleanClientConnection();
			
			int serverPortNumber = genieServer.getServerPortNumber();
			logClientMessage("---->> Preparing new client connection. " 
					+ " Active server connections: " + genieServer.getActiveConnections());
			
			socket = new Socket("localhost", serverPortNumber);
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
	
	protected HashMap<String, Object> sendCommand(String jsDocument) {
		try {
			serverInput.write(jsDocument);
			serverInput.flush();
			return readObject(serverOutput);
		} catch (IOException e) {
			throw assertFail();
		}
	}
	
	public void testGenieServer____________() {
		prepareGenieServer();
		prepareServerConnection();
		
		HashMap<String, Object> response;
		
		response = sendCommand(jsObject(jsEntry("about", null)));
		
		assertAreEqual(response.get("engine"), GenieServer.ENGINE_NAME);
		assertAreEqual(response.get("version"), GenieServer.ENGINE_VERSION);
		assertAreEqual(response.get("protocol_version"), "0.1");
		assertTrue(genieServer.exceptions.isEmpty());
		
		// Test invalid commands
		testError(jsObject(jsEntry("invalid_command", null)), "unknown command");
		
		// Test invalid message json
		testMessageInvalidJson("{ }", "expected property name");
		testMessageInvalidJson("{ ] }", "expected ':'");
		
		// Test disconnect during message
		writeStreamMessage(serverInput, "{ \"about\" : ");
		shutdownSocketOutput(socket);
		awaitConnectionHandlerTermination();
		checkServerExceptions("End of input");
		prepareServerConnection();
	}
	
	public void testError(String clientRequest, String expectedContains) {
		assertTrue(socket.isClosed() == false);
		
		HashMap<String, Object> response = sendCommand(clientRequest);
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
	
	protected HashMap<String, Object> response;
	
	@Test
	public void testSemanticOps() throws Exception { testSemanticOps$(); }
	public void testSemanticOps$() throws Exception {
		prepareGenieServer();
		prepareServerConnection();
		
		new FindDefinitionOperation_GenieTest().testALL();
	}
	
	public class FindDefinitionOperation_GenieTest extends FindDefinitionOperation_Test {
		
		@Override
		protected void prepEngineServer() {
			// Don't create server class
		}
		
		@Override
		protected FindDefinitionResult doOperation(Path filePath, int offset) {
			response = sendCommand(jsObject(jsEntry("find_definition", jsObject(
				jsEntry("filepath", filePath.toString()),
				jsEntry("offset", offset)
				)))
			);
			
			String errorMessage = getStringOrNull(response, "error");
			if(errorMessage != null) {
				return new FindDefinitionResult(errorMessage);
			}
			
			List<?> jsonResults = blindCast(response.get("results"));
			ArrayList<FindDefinitionResultEntry> results = new ArrayList<>(); 
			for (Object jsonResultEntry : jsonResults) {
				results.add(findDefResult(jsonResultEntry));
			}
			
			return new FindDefinitionResult(results);
		}
		
	}
	
	protected FindDefinitionResultEntry findDefResult(Object object) {
		Map<String, Object> resultEntry = blindCast(object);
		
		SourceRange sr = new SourceRange(getInt(resultEntry, "offset"), getInt(resultEntry, "length"));
		return new FindDefinitionResultEntry(
			getPath(resultEntry, "modulePath"), 
			sr, 
			getString(resultEntry, "extentedName"), 
			getFlag(resultEntry, "isLanguageIntrinsic"));
	}
	
}