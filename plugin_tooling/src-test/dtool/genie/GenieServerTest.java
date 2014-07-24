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
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

public class GenieServerTest extends JsonWriterTestUtils {
	
	protected TestsGenieServer genieServer;
	
	protected void prepareGenieServer() {
		if(genieServer != null && !genieServer.serverSocket.isClosed()) {
			return;
		}
		try {
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
		
		public void awaitTerminationOfPendingConnections() {
			connectionsSemaphore.acquireUninterruptibly(MAX_CONNECTIONS);
			connectionsSemaphore.release(MAX_CONNECTIONS);
		}
		
	}
	
	@After
	public void cleanup() throws InterruptedException {
		genieServer.terminateAndAwait();
		assertTrue(genieServer.serverSocket.isClosed());
		assertTrue(genieServer.exceptions.isEmpty());
		genieServer = null;
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
	
	protected void prepareServerConnection() {
		int serverPortNumber = genieServer.getServerPortNumber();
		try {
			if(socket != null) {
					serverInput.flush();
					socket.close();
			}
			
			socket = new Socket("localhost", serverPortNumber);
			serverInput = new OutputStreamWriter(socket.getOutputStream(), UTF8);
			serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF8));
		} catch (IOException e) {
			assertFail();
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
	
	protected void writeConnectionMessage(String clientRequest) {
		try {
			serverInput.write(clientRequest);
			serverInput.flush();
			socket.getOutputStream().flush();
		} catch (IOException e) {
			assertFail();
		}
	}
	
	public void testGenieServer____________() {
		prepareGenieServer();
		prepareServerConnection();
		
		HashMap<String, Object> response;
		
		response = sendCommand(jsDocument(jsEntry("about", jsNull())));
		
		assertAreEqual(response.get("engine"), GenieServer.ENGINE_NAME);
		assertAreEqual(response.get("version"), GenieServer.ENGINE_VERSION);
		assertAreEqual(response.get("protocol_version"), "0.1");
		assertTrue(genieServer.exceptions.isEmpty());
		
		// Test invalid commands
		testError(jsDocument(jsEntry("invalid_command", jsNull())), "unknown command");
		
		// Test invalid message json
		testMessageInvalidJson("{ }", "expected property name");
		testMessageInvalidJson("{ ] }", "expected ':'");
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
		
		writeConnectionMessage(clientRequest);
		
		try {
			while(serverOutput.read() != -1) {
			}
			socket.close();
		} catch (IOException e) {
		}
		genieServer.awaitTerminationOfPendingConnections();
		
		assertTrue(genieServer.exceptions.size() == 1);
		Throwable exception = genieServer.exceptions.get(0);
		assertStringContains(exception.getMessage().toLowerCase(), expectedErrorContains.toLowerCase());
		genieServer.exceptions.clear();
		prepareServerConnection();
	}
	
}