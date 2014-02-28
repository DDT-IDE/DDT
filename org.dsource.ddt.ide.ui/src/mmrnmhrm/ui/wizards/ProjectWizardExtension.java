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
package mmrnmhrm.ui.wizards;


import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.ui.wizards.ILocationGroup;
import org.eclipse.dltk.ui.wizards.IProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectCreator;
import org.eclipse.dltk.ui.wizards.ProjectWizard;
import org.eclipse.dltk.utils.ResourceUtil;

public abstract class ProjectWizardExtension extends ProjectWizard {
	
	public ProjectWizardExtension() {
		super();
	}
	
	@Override
	protected abstract ILocationGroup getFirstPage();
	
	@Override
	protected ProjectCreatorExt createProjectCreator() {
		return new ProjectCreatorExt(this, getFirstPage());
	}
	
	@Override
	public ProjectCreatorExt getProjectCreator() {
		return (ProjectCreatorExt) super.getProjectCreator();
	}
	
	protected final class ProjectCreatorExt extends ProjectCreator {
		private ProjectCreatorExt(IProjectWizard owner, ILocationGroup locationGroup) {
			super(owner, locationGroup);
		}
		
		@Override
		public void changeToNewProject() {
			super.changeToNewProject();
		}
		
		@Override
		protected void createProject(IProject project, URI locationURI, IProgressMonitor monitor) 
				throws CoreException {
			super.createProject(project, locationURI, monitor);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			configureScriptProject(new SubProgressMonitor(monitor, 0));
		}
		
		@Override
		protected IBuildpathEntry[] initBuildpath(IProgressMonitor monitor) throws CoreException {
			return super.initBuildpath(monitor);
		}
		
	}
	
	public void configureScriptProject(IProgressMonitor monitor) throws CoreException{
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			final IProject project = getCreatedElement().getProject();
			ResourceUtil.addNature(project, monitor, getScriptNature());
			
			configureProjectBuildpath(project, monitor);
			
		} finally {
			monitor.worked(1);
		}
	}
	
	@SuppressWarnings("unused")
	protected void configureProjectBuildpath(final IProject project, IProgressMonitor monitor) throws CoreException {
		IBuildpathEntry[] bpEntries = getProjectCreator().initBuildpath(new SubProgressMonitor(monitor, 0));
		getCreatedElement().setRawBuildpath(bpEntries, new SubProgressMonitor(monitor, 0));
	}
	
}