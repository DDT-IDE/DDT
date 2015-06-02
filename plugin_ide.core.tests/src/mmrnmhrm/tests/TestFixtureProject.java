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
package mmrnmhrm.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.MiscUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class TestFixtureProject implements ITestResourcesConstants {
	
	protected final String projectName;
	public final IProject project;
	
	static {
		MiscUtil.loadClass(CommonDeeWorkspaceTest.class);
	}

	public TestFixtureProject(String projectName) throws CoreException {
		this.projectName = projectName;
		this.project = createProject(projectName);
		
		createContents();
	}
	
	protected IProject createProject(String projectName) throws CoreException {
		return CommonDeeWorkspaceTest.createLangProject(projectName, true);
	}
	
	protected void createContents() throws CoreException {
	}
	
	protected void writeManifestFile() throws CoreException {
		String sourceFolder = "source";
		
		CommonDeeWorkspaceTest.writeDubManifest(project, projectName, sourceFolder);
	}
	
	public java.nio.file.Path getPath() {
		return project.getLocation().toFile().toPath();
	}
	
	public IFolder createFolder(String name) throws CoreException {
		IFolder folder = getFolder(name);
		folder.create(true, true, null);
		return folder;
	}
	
	public IFolder getFolder(String name) {
		IFolder folder = project.getFolder(name);
		assertTrue(folder.exists());
		return folder;
	}
	
	public IFile getFile(String pathString) {
		IFile file = project.getFile(pathString);
		assertTrue(file.exists());
		return file;
	}
	
}