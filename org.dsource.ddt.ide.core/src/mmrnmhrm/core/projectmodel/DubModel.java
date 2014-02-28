/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.projectmodel;

import java.util.HashMap;

import melnorme.utilbox.misc.ListenerListHelper;

import org.eclipse.core.resources.IProject;

import dtool.SimpleLogger;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;

/**
 * Dub model. Holds information about DUB bundles, usually indexed by project.
 * Designed to be managed concurrently by some other code (see {@link DubModelManager}).
 * Can notify listeners of updates. 
 */
public class DubModel extends ListenerListHelper<IDubModelListener> {
	
	protected static SimpleLogger log = new SimpleLogger(true);
	
	protected static final DubModel defaultInstance = new DubModel();
	
	public static DubModel getDefault() {
		return defaultInstance;
	}
	
	public static DubBundleDescription getBundleInfo(String projectName) {
		return getDefault().doGetBundleInfo(projectName);
	}
	
	public static DubDependenciesContainer getDubContainer(IProject project) {
		DubBundleDescription bundleInfo = getBundleInfo(project.getName());
		if(bundleInfo == null)
			return null;
		return new DubDependenciesContainer(bundleInfo, project);
	}
	
	/* ----------------------------------- */
	
	public DubModel() {
	}
	
	protected final HashMap<String, DubBundleDescription> dubBundleInfos = new HashMap<>();
	
	protected synchronized void addProjectModel(IProject project, DubBundleDescription dubBundleDescription) {
		log.println(">> Add project info: ", project);
		dubBundleInfos.put(project.getName(), dubBundleDescription);
		fireUpdateEvent(this, dubBundleDescription);
	}
	
	protected synchronized void removeProjectModel(IProject project) {
		log.println(">> Removing project: ", project);
		DubBundleDescription oldDesc = dubBundleInfos.remove(project.getName());
		fireUpdateEvent(this, oldDesc);
	}
	
	protected synchronized void addErrorToProjectModel(IProject project, DubBundleException dubError) {
		DubBundleDescription unresolvedDescription = dubBundleInfos.get(project.getName());
		DubBundle main = unresolvedDescription.getMainBundle();
		
		DubBundleDescription bundleDesc = new DubBundleDescription(main, dubError);
		addProjectModel(project, bundleDesc);
	}
	
	protected void fireUpdateEvent(DubModel source, DubBundleDescription object) {
		for (IDubModelListener listener : getListeners()) {
			listener.notifyUpdateEvent(source, object);
		}
	}
	
	protected synchronized DubBundleDescription doGetBundleInfo(String projectName) {
		return dubBundleInfos.get(projectName);
	}
	
}