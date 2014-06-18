/*******************************************************************************
 * Copyright (c) 2010, 2014 IBM Corporation and others.
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

import java.io.File;
import java.io.IOException;

import melnorme.lang.ide.core.tests.utils.BundleResourcesUtil;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tests.TestsWorkingDir;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;

import dtool.tests.utils.MiscFileUtils;


public class DeeCoreTestResources {
	
	public static File getWorkingDirFile(String relativePath) {
		return new File(TestsWorkingDir.getWorkingDir(), relativePath);
	}
	
	public static Path getWorkingDirPath(String relativePath) {
		File workingDirFile = getWorkingDirFile(relativePath);
		assertTrue(workingDirFile.getPath().equals(workingDirFile.getAbsolutePath()));
		return new Path(workingDirFile.getAbsolutePath());
	}
	
	public static void createSrcFolderFromCoreResource(String resourcePath, IContainer destFolder) 
			throws CoreException {
		createFolderFromCoreResource(resourcePath, destFolder);
		addSourceFolder(destFolder, null);
	}
	
	public static void createFolderFromCoreResource(String resourcePath, IContainer destFolder)
			throws CoreException {
		File destFolder_File = destFolder.getLocation().toFile();
		copyTestFolderContentsFromCoreResource(resourcePath, destFolder_File);
		
		destFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(destFolder.exists());
	}
	
	protected static final String TESTDATA_BUNDLE_PATH = "testdata/";
	
	/** Copies the contents of a test bundle resource folder into given destFolder destination */
	public static void copyTestFolderContentsFromCoreResource(String resourcePath, File destFolder) 
			throws CoreException {
		String pluginId = DeeCore.TESTS_PLUGIN_ID;
		String bundleResourcePath = new Path(TESTDATA_BUNDLE_PATH).append(resourcePath).toString();
		try {
			BundleResourcesUtil.copyDirContents(pluginId, bundleResourcePath, destFolder);
		} catch(IOException e) {
			throw DeeCore.createCoreException("Error copying resource contents", e);
		}
	}
	
	public static void createSrcFolderFromDirectory(File directory, IScriptProject project, 
		String destFolderName) throws CoreException {
		IFolder destFolder = project.getProject().getFolder(destFolderName);
		MiscFileUtils.copyDirContentsIntoDirectory(directory, destFolder.getLocation().toFile());
		destFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		addSourceFolder(destFolder, null);
	}
	
	/** Setup the given folder as a source folder. */
	public static IProjectFragment addSourceFolder(IContainer folder, IProgressMonitor pm) throws CoreException {
		IScriptProject dltkProj = DLTKCore.create(folder.getProject());
		IProjectFragment fragment = dltkProj.getProjectFragment(folder);
		if(!fragment.exists()) {
			IBuildpathEntry[] bpentries = dltkProj.getRawBuildpath();
			IBuildpathEntry entry = DLTKCore.newSourceEntry(fragment.getPath());
			dltkProj.setRawBuildpath(ArrayUtil.concat(bpentries, entry), pm);
		}
		return fragment;
	}
	
}