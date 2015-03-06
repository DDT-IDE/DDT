/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.DefaultBufferListener;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import melnorme.utilbox.misc.SimpleLogger;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;


public abstract class AbstractSemanticDaemonClient {
	
	public static SimpleLogger log = new SimpleLogger(Platform.inDebugMode());
	
	protected final IFileBufferListener fileBufferListener;
	
	public AbstractSemanticDaemonClient() {
		fileBufferListener = new FileBuffersListener();
		FileBuffers.getTextFileBufferManager().addFileBufferListener(fileBufferListener);
	}
	
	public void shutdown() {
		FileBuffers.getTextFileBufferManager().removeFileBufferListener(fileBufferListener);
	}
	
	/* -----------------  ----------------- */
	
	protected class FileBuffersListener extends DefaultBufferListener {
		@Override
		public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
			log.println("dirtyStateChanged: " + buffer.getLocation() + " "+ buffer.isDirty());
			if(!isDirty) {
				discardWorkingCopy(buffer);
			}
		}
		
		@Override
		public void bufferDisposed(IFileBuffer buffer) {
			log.println("bufferDisposed: " + buffer.getLocation());
			discardWorkingCopy(buffer);
		}
	}
	
	protected void discardWorkingCopy(IFileBuffer buffer) {
		IFileStore fileStore = buffer.getFileStore();
		if(fileStore == null) {
			LangCore.logError("Error in discardWorkingCopy: listener fileStore == null");
		}
		Path filePath;
		try {
			filePath = Paths.get(fileStore.toURI());
		} catch (RuntimeException e) {
			LangCore.logError("Error converting URI to path.");
			return;
		}
		
		discardWorkingCopy(filePath);
	}
	
	protected abstract void updateServerWorkingCopy(Path filePath, String source);
	
	// XXX: perhaps in the future these two methods will be unified
	protected abstract void discardWorkingCopy(Path filePath);
	protected abstract void discardServerWorkingCopy(Path filePath);
	
	public void updateServerWorkingCopyIfDirty(Path filePath, String source) {
		IFileBuffer fileBuffer = getFileBuffer(filePath);
		
		if(fileBuffer != null && fileBuffer.isDirty()) {
			updateServerWorkingCopy(filePath, source);
		}
	}
	
	public void discardWorkingCopyIfNotDirty(Path filePath) {
		IFileBuffer fileBuffer = getFileBuffer(filePath);
		
		if(fileBuffer == null || !fileBuffer.isDirty()) {
			// If somehow the buffer is not dirty, or wasn't dirty in the first place, discard working copy:
			discardServerWorkingCopy(filePath);
		}
	}
	
	protected IFileBuffer getFileBuffer(Path filePath) {
		
		ITextFileBufferManager fbm = FileBuffers.getTextFileBufferManager();
		IFileBuffer fileBuffer = fbm.getFileBuffer(EclipseUtils.epath(filePath), LocationKind.LOCATION);
		fbm.getTextFileBuffer(EclipseUtils.epath(filePath), LocationKind.LOCATION);
		if(fileBuffer != null) {
			return fileBuffer;
		}
		
		// Could be an external file, try alternative API:
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(EclipseUtils.epath(filePath));
		fileBuffer = fbm.getFileStoreFileBuffer(fileStore);
		
		return fileBuffer;
	}
	
	/* -----------------  ----------------- */
	
	public abstract class WorkingCopyOperation<RET> {
		
		protected final Path filePath;
		protected final String source;
		
		public WorkingCopyOperation(Path filePath, String source) {
			this.filePath = assertNotNull(filePath);
			this.source = assertNotNull(source);
		}
		
		public RET runSemanticServerOperation() throws CoreException {
			try {
				updateServerWorkingCopyIfDirty(filePath, source);
				
				return runOperationWithWorkingCopy();
				
			} finally {
				discardWorkingCopyIfNotDirty(filePath);
			}
		}
		
		protected abstract RET runOperationWithWorkingCopy() throws CoreException;
	}
	
	public abstract class SemanticEngineOperation<RET> extends WorkingCopyOperation<RET>{
		
		protected final int offset;
		protected final int timeoutMillis;
		protected final String opName;
		
		public SemanticEngineOperation(Path filePath, String source, int offset, int timeoutMillis, String opName) {
			super(filePath, source);
			this.offset = offset;
			this.timeoutMillis = timeoutMillis;
			this.opName = assertNotNull(opName);
		}
		
		@Override
		protected final RET runOperationWithWorkingCopy() throws CoreException {
			if(timeoutMillis <= 0 ) {
				// Run directly
				return doRunOperationWithWorkingCopy();
			}
			
			ExecutorTaskAgent completionExecutor = new ExecutorTaskAgent(opName + " - Executor");
			
			Future<RET> future = completionExecutor.submit(new Callable<RET>() {
				@Override
				public RET call() throws CoreException {
					return doRunOperationWithWorkingCopy();
				}
			});
			
			try {
				return EclipseUtils.getFutureResult(future, timeoutMillis, TimeUnit.MILLISECONDS, opName);
			} finally {
				completionExecutor.shutdown();
			}
		}
		
		protected abstract RET doRunOperationWithWorkingCopy() throws CoreException;
		
	}
	
}