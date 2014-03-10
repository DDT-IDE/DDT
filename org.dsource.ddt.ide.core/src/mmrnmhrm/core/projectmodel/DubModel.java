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
import java.util.Set;

import melnorme.utilbox.misc.ListenerListHelper;
import mmrnmhrm.core.projectmodel.DubModel.DubModelUpdateEvent;
import mmrnmhrm.core.projectmodel.DubModel.IDubModel;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;

public abstract class DubModel {
	
	protected static final DubModelImpl defaultInstance = new DubModelImpl();
	
	public static IDubModel getDefault() {
		return defaultInstance;
	}
	
	public static DubBundleDescription getBundleInfo(String projectName) {
		return defaultInstance.getBundleInfo(projectName);
	}
	
	/**
	 * DUB model. Holds information about DUB bundles, for the projects in the workspace.
	 * Designed to be managed concurrently by some other code (see {@link DubModelManager}).
	 * Can notify listeners of updates. 
	 */
	public static interface IDubModel {
		
		public void addListener(IDubModelListener listener);
		Set<String> getDubProjects();
		public void removeListener(IDubModelListener listener);
		
		public DubBundleDescription getBundleInfo(String projectName);
		
	}
	
	public static class DubModelUpdateEvent {
		public final IProject project;
		public final DubBundleDescription dubBundleDescription;
		
		public DubModelUpdateEvent(IProject project, DubBundleDescription dubBundleDescription) {
			this.project = project;
			this.dubBundleDescription = dubBundleDescription;
		}
		
	}
	
}

class DubModelImpl extends ListenerListHelper<IDubModelListener> implements IDubModel {
	
	protected final HashMap<String, DubBundleDescription> dubBundleInfos = new HashMap<>();
	
	public DubModelImpl() {
	}
	
	@Override
	public void addListener(IDubModelListener listener) {
		super.addListener(listener);
	}
	@Override
	public void removeListener(IDubModelListener listener) {
		super.removeListener(listener);
	}
	
	@Override
	public synchronized DubBundleDescription getBundleInfo(String projectName) {
		return dubBundleInfos.get(projectName);
	}
	
	@Override
	public synchronized Set<String> getDubProjects() {
		return dubBundleInfos.keySet();
	}
	
	protected synchronized void addProjectModel(IProject project, DubBundleDescription dubBundleDescription) {
		dubBundleInfos.put(project.getName(), dubBundleDescription);
		fireUpdateEvent(new DubModelUpdateEvent(project, dubBundleDescription));
	}
	
	protected synchronized void removeProjectModel(IProject project) {
		DubBundleDescription oldDesc = dubBundleInfos.remove(project.getName());
		fireUpdateEvent(new DubModelUpdateEvent(project, oldDesc));
	}
	
	protected void fireUpdateEvent(DubModelUpdateEvent updateEvent) {
		for (IDubModelListener listener : getListeners()) {
			listener.notifyUpdateEvent(updateEvent);
		}
	}
	
	protected synchronized void addErrorToProjectModel(IProject project, DubBundleException dubError) {
		DubBundleDescription unresolvedDescription = dubBundleInfos.get(project.getName());
		DubBundle main = unresolvedDescription.getMainBundle();
		
		DubBundleDescription bundleDesc = new DubBundleDescription(main, dubError);
		addProjectModel(project, bundleDesc);
	}
	
}