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
import mmrnmhrm.core.projectmodel.IDubModel.IDubModelListener;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;

public class DubModel extends ListenerListHelper<IDubModelListener> implements IDubModel {
	
	protected final HashMap<String, DubBundleDescription> dubBundleInfos = new HashMap<>();
	
	public DubModel() {
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