/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.dub;

import java.io.IOException;
import java.nio.file.attribute.FileTime;

import dtool.dub.DubBundle.DubBundleException;
import melnorme.lang.tooling.BundlePath;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.OperationCallable;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

/**
 * Helper code to run DUB commands.
 */
public class DubDescribeRunner {
	
	public static final String DUB_PATH_OVERRIDE = System.getProperty("DTool.DubPath");
	
	static {
		if(DUB_PATH_OVERRIDE != null) {
			System.out.println(":::: DubPathOverride: " + DUB_PATH_OVERRIDE);	
		}
	}
	
	
	/* -----------------  ----------------- */
	
	protected final BundlePath bundlePath;
	protected final String dubPath;
	protected final boolean allowDepDownload;
	
	public DubDescribeRunner(BundlePath bundlePath, String dubPath, boolean allowDepDownload) {
		this.bundlePath = bundlePath;
		this.dubPath = dubPath;
		this.allowDepDownload = allowDepDownload;
	}
	
	public DubBundleDescription runDubDescribe() throws CommonException, OperationCancellation {
		return runDescribeOperation();
	}
	
	public DubBundleDescription runDescribeOperation() throws CommonException, OperationCancellation {
		ProcessBuilder pb = allowDepDownload ? 
				new ProcessBuilder(dubPath, "describe") : 
				new ProcessBuilder(dubPath, "describe", "--nodeps");
		
		pb.directory(bundlePath.getLocation().toFile());
		
		ExternalProcessResult processResult = runProcessAndAwaitResult(pb);
		
		return parseDubDescribe(bundlePath, processResult);
	}
	
	protected ExternalProcessResult runProcessAndAwaitResult(ProcessBuilder pb) 
			throws CommonException, OperationCancellation {
		ExternalProcessResult processResult;
		try {
			processResult = new ExternalProcessHelper(pb).awaitTerminationAndResult();
		} catch(IOException e) {
			throw new CommonException("Error reading `dub describe` output:", e);
		} catch(InterruptedException e) {
			throw new OperationCancellation();
		}
		return processResult;
	}
	
	public DubBundleDescription parseDubDescribe(BundlePath bundlePath, ExternalProcessResult processResult) {
		String describeOutput = processResult.stdout.toString(StringUtil.UTF8);
		
		int exitValue = processResult.exitValue;
		if(exitValue != 0) {
			DubBundleException error = new DubDescribeFailure(processResult);
			return new DubBundleDescription(new DubBundle(bundlePath, DubBundleDescription.BUNDLE_NAME_ERROR, error));
		}
		
		// Trim leading characters. 
		// They shouldn't be there, but sometimes dub outputs non JSON text if downloading packages
		describeOutput = StringUtil.substringFromMatch('{', describeOutput);
		
		return DubDescribeParser.parseDescription(bundlePath, describeOutput);
	}
	
	@SuppressWarnings("serial")
	public static class DubDescribeFailure extends DubBundleException {
		
		protected final ExternalProcessResult processResult;
		
		public DubDescribeFailure(ExternalProcessResult processResult) {
			super(processResult.getStdErrBytes().toString());
			this.processResult = processResult;
		}
		
		public String getStdOut() {
			return processResult.getStdOutBytes().toString();
		}
		
		public String getStdErr() {
			return processResult.getStdErrBytes().toString();
		}
		
	}
	
	public static class RunDubDescribeCallable implements OperationCallable<DubBundleDescription> {
		
		protected final BundlePath bundlePath;
		protected final String dubPath;
		protected final boolean allowDepDownload;
		
		protected volatile FileTime startTimeStamp = null;
		
		public RunDubDescribeCallable(BundlePath bundlePath, String dubPath, boolean allowDepDownload) {
			this.bundlePath = bundlePath;
			this.dubPath = dubPath;
			this.allowDepDownload = allowDepDownload;
		}
		
		@Override
		public DubBundleDescription call() throws CommonException, OperationCancellation {
			startTimeStamp = FileTime.fromMillis(System.currentTimeMillis());
			return new DubDescribeRunner(bundlePath, dubPath, allowDepDownload).runDubDescribe();
		}
		
		public FileTime getStartTimeStamp() {
			return startTimeStamp;
		}
		
		public DubBundleDescription submitAndGet(ITaskAgent processAgent) throws CommonException {
			try {
				return processAgent.submitOp(this).getResult().get();
			} catch (OperationCancellation e) {
				throw new CommonException("Error running `dub describe`, operation interrupted.");
			}
		}
		
	}
	
}