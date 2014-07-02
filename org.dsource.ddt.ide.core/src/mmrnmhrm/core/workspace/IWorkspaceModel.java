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
package mmrnmhrm.core.workspace;

import java.util.Set;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;

/**
 * DUB model. Holds information about DUB bundles, for the projects in the workspace.
 * Designed to be managed concurrently by some other code (see {@link WorkspaceModelManager}).
 * Can notify listeners of updates. 
 */
public interface IWorkspaceModel {
	
	public void addListener(IWorkspaceModelListener listener);
	public Set<String> getDubProjects();
	public void removeListener(IWorkspaceModelListener listener);
	
	public DubBundleDescription getBundleInfo(IProject project);
	
	public interface IWorkspaceModelListener {
		
		/** 
		 * Note, several locks are held in the scope of this method (DubModel, and potentially Workspace Root).
		 * Do NOT run long running or locking code in the implementation, 
		 * just post the event to another thread/agent/dispatcher to handle.
		 */
		void notifyUpdateEvent(DubModelUpdateEvent updateEvent);
		
	}
	
	public class DubModelUpdateEvent {
		public final IProject project;
		public final DubBundleDescription dubBundleDescription;
		
		public DubModelUpdateEvent(IProject project, DubBundleDescription dubBundleDescription) {
			this.project = project;
			this.dubBundleDescription = dubBundleDescription;
		}
		
	}
	
}