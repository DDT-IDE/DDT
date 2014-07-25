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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import melnorme.utilbox.concurrency.SafeRunnable;
import dtool.genie.GenieServer.GenieConnectionHandler;

/** 
 * Helper for Genie Server, manages a basic TCP socket server.
 */
public abstract class AbstractSocketServer {
	
	protected static final int MAX_CONNECTIONS = 256;
	
	protected final ServerSocket serverSocket;
	protected final int portNumber;
	protected final CountDownLatch terminationLatch = new CountDownLatch(1);
	protected final Semaphore connectionsSemaphore = new Semaphore(MAX_CONNECTIONS);
	
	
	public AbstractSocketServer(int portNumber) throws IOException {
		this.serverSocket = new ServerSocket(portNumber);
		this.portNumber = serverSocket.getLocalPort();
		
		logMessage("Started server on port: " + this.portNumber);
	}
	
	public int getServerPortNumber() {
		return portNumber;
	}
	
	public void logMessage(String message) {
		System.out.println(message);
		System.out.flush();
	}
	
	public void logException(String message, Throwable exception) {
		logMessage(">> " + message);
		exception.printStackTrace(System.out);
		System.out.flush();
	}
	
	public void runServer() {
		try {
			while(true) {
				Socket clientSocket = serverSocket.accept();
				logMessage("New connection from: " + clientSocket.getRemoteSocketAddress().toString());
				handleNewClientConnection(clientSocket);
			}
		} catch (SocketException se) {
			assertTrue(serverSocket.isClosed());
		} catch (IOException ioe) {
			logException("Unexpected exception during socket accept: " , ioe);
		} finally {
			closeServerSocket();
			logMessage("Server socket closed.");
			terminationLatch.countDown();
		}
	}
	
	protected void closeServerSocket() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logException("Error closing socket: ", e);
		}
	}
	
	public int getActiveConnections() {
		return MAX_CONNECTIONS - connectionsSemaphore.availablePermits();
	}
	
	protected void handleNewClientConnection(Socket clientSocket) {
		connectionsSemaphore.acquireUninterruptibly();
		ConnectionHandlerRunnable genieConnectionHandler = createConnectionHandlerRunnable(clientSocket);
		createHandlerThread(genieConnectionHandler).start();
	}
	
	protected Thread createHandlerThread(ConnectionHandlerRunnable genieConnectionHandler) {
		return new Thread(genieConnectionHandler);
	}
	
	protected abstract GenieConnectionHandler createConnectionHandlerRunnable(Socket clientSocket);
	
	public abstract class ConnectionHandlerRunnable extends SafeRunnable {
		
		protected final Socket clientSocket;
		
		public ConnectionHandlerRunnable(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		@Override
		protected void handleUncaughtException(Throwable e) {
			logException("Internal error (runtime exception) in connection handler: ", e);
			assertFail();
		}
		
		@Override
		protected void safeRun() {
			handleConnection();
		}
		
		public void handleConnection() {
			try {
				doHandleConnectionStream();
			} catch (IOException e) {
				logException("IO error in connection handler: ", e);
			} finally {
				try {
					logMessage("Closing client connection : " + clientSocket.getRemoteSocketAddress().toString());
					clientSocket.close();
				} catch (IOException e) {
					logException("Error closing client socket: ", e);
				}
				connectionsSemaphore.release();
			}
		}
		
		protected abstract void doHandleConnectionStream() throws IOException;
		
	}
	
}