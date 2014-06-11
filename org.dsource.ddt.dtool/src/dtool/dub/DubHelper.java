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
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

/**
 * Helper code to run DUB commands.
 */
public class DubHelper {
	
	public static DubBundleDescription runDubDescribe(BundlePath bundlePath) throws IOException, InterruptedException {
		return runDubDescribe(bundlePath, false);
	}
	
	public static DubBundleDescription runDubDescribe(BundlePath bundlePath, boolean allowDepDownload) 
			throws IOException, InterruptedException {
		ProcessBuilder pb;
		if(allowDepDownload) {
			pb = new ProcessBuilder("dub", "describe");
		} else {
			pb = new ProcessBuilder("dub", "describe", "--nodeps");
		}
		
		pb.directory(bundlePath.path.toFile());
		ExternalProcessHelper extPH = new ExternalProcessHelper(pb);
		ExternalProcessResult processResult;
		try {
			processResult = extPH.strictAwaitTermination();
		} catch (TimeoutException e) {
			throw assertFail(); // Cannot happen because there is no cancel monitor
		}
		
		return parseDubDescribe(bundlePath, processResult);
	}
	
	public static DubBundleDescription parseDubDescribe(BundlePath bundlePath, ExternalProcessResult processResult) {
		String describeOutput = processResult.stdout.toString(StringUtil.UTF8);
		
		// Trim leading characters. 
		// They shouldn't be there, but sometimes dub outputs non JSON text if downloading packages
		describeOutput = StringUtil.substringFromMatch('{', describeOutput);
		
		return DubDescribeParser.parseDescription(bundlePath, describeOutput);
	}
	
	
	public static class RunDubDescribeCallable implements ICallable<DubBundleDescription, Exception> {
		
		protected final BundlePath bundlePath;
		protected final boolean allowDepDownload;
		
		protected volatile long startTimeStamp = -1;
		
		public RunDubDescribeCallable(BundlePath bundlePath, boolean allowDepDownload) {
			this.bundlePath = bundlePath;
			this.allowDepDownload = allowDepDownload;
		}
		
		@Override
		public DubBundleDescription call() throws IOException, InterruptedException {
			startTimeStamp = System.nanoTime();
			return DubHelper.runDubDescribe(bundlePath, allowDepDownload);
		}
		
		public long getStartTimeStamp() {
			return startTimeStamp;
		}
		
		public DubBundleDescription submitAndGet(ITaskAgent processAgent) throws ExecutionException {
			try {
				return processAgent.submit(this).get();
			} catch (InterruptedException e) {
				throw new ExecutionException(e);
			}
		}
		
	}
	
}