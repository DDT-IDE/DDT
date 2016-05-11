/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.project_model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.HashSet;

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.core.utils.CoreExecutors;
import melnorme.lang.tooling.bundle.BundleInfo;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.LatchRunnable;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.ownership.Disposable;

public abstract class BundleModelManager<BUNDLE_MODEL extends LangBundleModel> 
	extends ProjectBasedModelManager implements IBundleModelManager {
	
	/* ----------------------------------- */
	
	protected final BUNDLE_MODEL model;
	protected final SimpleLogger log;
	
	protected final ITaskAgent modelAgent = CoreExecutors.newExecutorTaskAgent(getClass());
	protected final LatchRunnable startLatch = new LatchRunnable();
	
	public BundleModelManager(BUNDLE_MODEL model) {
		this.model = assertNotNull(model);
		this.log = model.getLog();
		
		initializeModelManagerWithModelAgent();
	}
	
	public ITaskAgent getModelAgent() {
		return modelAgent;
	}
	
	protected void initializeModelManagerWithModelAgent() {
		// Put a latch runnable to prevent model from actually starting
		// This is because typically we want model to start only after UI code is fully loaded 
		modelAgent.submitR(startLatch);
		
		// Run heavyweight initialization in executor thread.
		// This is necessary so that we avoid running the initialization during plugin initialization.
		// Otherwise there could be problems because initialization is heavyweight code:
		// it requests workspace locks (which may not be available) and issues workspace deltas
		modelAgent.submitR(new Runnable() {
			@Override
			public void run() {
				initializeModelManager();
			}
		});
	}
	
	@Override
	public void startManager() {
		log.println("==> Starting: " + getClass().getSimpleName());
		startLatch.releaseAll();
	}
	
	@Override
	protected void dispose_pre() {
		modelAgent.shutdownNowAndCancelAll();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public BUNDLE_MODEL getModel() {
		return model;
	}
	
	@Override
	public BundleInfo getProjectInfo(IProject project) {
		return model.getProjectInfo(project);
	}
	
	@Override
	protected void bundleProjectRemoved(IProject project) {
		model.removeProjectInfo(project);
	}
	
	@Override
	protected void bundleManifestFileChanged(IProject project) {
		bundleProjectAdded(project);
	}
	
	@Override
	protected final void bundleProjectAdded(IProject project) {
		if(ignoredProjects.contains(project)) {
			return;
		}
		handleBundleProjectAdded(project);
	}
	
	protected void handleBundleProjectAdded(IProject project) {
		model.setProjectInfo(project, createNewInfo(project));
	}
	
	protected abstract BundleInfo createNewInfo(IProject project);
	
	/* -----------------  ----------------- */
	
	protected final HashSet<IProject> ignoredProjects = new HashSet<>();
	
	/** Ignore all manifest updates for given project. 
	 * This is intended to be used only for debugging or testing code.  */
	public Disposable enableIgnoreProject(IProject project) {
		ignoredProjects.add(project);
		
		return new Disposable() {
			@Override
			public void dispose() {
				ignoredProjects.remove(project);
			}
		};
	}
	
}