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
package dtool.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.core.fntypes.ICallable;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.BundleFile;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParser;
import dtool.project.DeeNamingRules;

public class SemanticManager {
	
	protected final ITaskAgent processAgent;
	protected final DToolServer dtoolServer;
	
	protected final HashMap<Path, DubBundleDescription> bundleInfos = new HashMap<>();
	
	
	public SemanticManager(ITaskAgent processAgent, DToolServer dtoolServer) {
		this.processAgent = processAgent;
		this.dtoolServer = assertNotNull(dtoolServer);
	}
	
	public static Path validatePath(Path filePath) {
		assertTrue(filePath.isAbsolute());
		assertTrue(filePath.getNameCount() > 0);
		filePath = filePath.normalize();
		return filePath;
	}
	
	public SemanticContext getSemanticContext(Path bundlePath) throws InterruptedException, ExecutionException {
		
		bundlePath = validatePath(bundlePath);
		return processAgent.submit(new GetSemanticContextOperation(bundlePath)).get();
	}
	
	public class SemanticContext {
		
		protected final DubBundleDescription bundleDesc;
		protected final HashMap<ModuleFullName, Path> bundleModules;

		public SemanticContext(DubBundleDescription bundleDesc) {
			this.bundleDesc = bundleDesc;
			DubBundle mainBundle = bundleDesc.getMainBundle();
			assertNotNull(mainBundle.getBundleName());
			
			this.bundleModules = calculateBundleModules(mainBundle);
		}
		
		public String getBundleId() {
			return bundleDesc.getMainBundle().getBundleName();
		}
		
		public HashMap<ModuleFullName, Path> getBundleModuleFiles() {
			return bundleModules;
		}
		
	}
	
	protected class GetSemanticContextOperation implements ICallable<SemanticContext, Exception> {
		
		protected Path bundlePath;

		public GetSemanticContextOperation(Path bundlePath) {
			this.bundlePath = bundlePath;
		}

		@Override
		public SemanticContext call() throws IOException, InterruptedException {
			ProcessBuilder pb = new ProcessBuilder("dub", "describe").directory(bundlePath.toFile());
			ExternalProcessHelper extPH = new ExternalProcessHelper(pb);
			ExternalProcessResult processResult;
			try {
				processResult = extPH.strictAwaitTermination();
			} catch (TimeoutException e) {
				throw assertFail();
			}
			
			DubBundleDescription bundleDesc = parseDubDescribe(bundlePath, processResult);
			return new SemanticContext(bundleDesc);
		}
	}
	
	public static DubBundleDescription parseDubDescribe(Path location, ExternalProcessResult processResult) {
		String describeOutput = processResult.stdout.toString(StringUtil.UTF8);
		
		// Trim leading characters. 
		// They shouldn't be there, but sometimes dub outputs non JSON text if downloading packages
		describeOutput = StringUtil.substringFromMatch('{', describeOutput);
		
		return DubDescribeParser.parseDescription(location, describeOutput);
	}
	
	/* ----------------- ----------------- */
	
	protected HashMap<ModuleFullName, Path> calculateBundleModules(DubBundle mainBundle) {
		HashMap<ModuleFullName, Path> hashMap = new HashMap<>();
		
		for (BundleFile bundleFiles : mainBundle.bundleFiles) {
			Path filePath = MiscUtil.createValidPath(bundleFiles.filePath);
			if(filePath == null) {
				logError("Invalid filesystem path: " + bundleFiles.filePath);
				continue; // ignore
			}
			
			Path[] importFolders = mainBundle.getEffectiveImportPathFolders();
			for (Path importFolder : importFolders) {
				if(filePath.startsWith(importFolder)) {
					Path relPath = importFolder.relativize(filePath);
					if(relPath.getNameCount() == 0) {
						logError("File has same path as import folder: " + filePath);
						continue;
					}
					
					ModuleFullName moduleFullName = DeeNamingRules.getModuleFullName(relPath);
					if(!moduleFullName.isValid()) {
						logWarning("Invalid path for a D module: " + relPath);
						continue;
					}
					hashMap.put(moduleFullName, filePath);
					
					// continue looking, the same file can be present in multiple import paths, if nested
					// it's not an elegant scenario, but it's probably ok to support.
				}
			}
		}
		return hashMap;
	}
	
	protected void logError(String message) {
		dtoolServer.logError(message, null);
	}
	
	protected void logWarning(String message) {
		dtoolServer.logMessage(message);
	}
	
}