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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.downCast;
import melnorme.utilbox.misc.MiscUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.dub.BundlePath;

public class TestFixtureProject implements ITestResourcesConstants {
	
	protected final String projectName;
	public final IProject project;
	public final IScriptProject scriptProject;
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
	}

	public TestFixtureProject(String projectName) throws CoreException {
		this.projectName = projectName;
		scriptProject = BaseDeeTest.createLangProject(projectName, true);
		project = scriptProject.getProject();
		
		createContents();
	}
	
	protected void createContents() throws CoreException {
	}
	
	protected void writeManifestFile() throws CoreException {
		String sourceFolder = "source";
		
		BaseDeeTest.writeStringToFile(project, BundlePath.DUB_MANIFEST_FILENAME, MiscJsonUtils.jsDocument(
			MiscJsonUtils.jsStringEntry("name", projectName),
			MiscJsonUtils.jsEntryValue("sourcePaths", "[ \"" + sourceFolder + "\" ]"),
			MiscJsonUtils.jsEntryValue("importPaths", "[ \"" + sourceFolder + "\" ]")
		));
	}
	
	public java.nio.file.Path getPath() {
		return project.getLocation().toFile().toPath();
	}
	
	public ISourceModule getSourceModule(String pathString) {
		try {
			ISourceModule sourceModule = downCast(scriptProject.findElement(new Path(pathString)));
			assertTrue(sourceModule.exists());
			return sourceModule;
		} catch (ModelException e) {
			throw assertFail();
		}
	}
	
}