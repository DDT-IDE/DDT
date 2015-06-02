/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.tests.CommonDeeWorkspaceTest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.indexing.IndexManager;

import dtool.dub.BundlePath;
import dtool.tests.utils.MiscJsonUtils;

/***
 * Future replacement for {@link CommonDeeWorkspaceTest}
 */
public abstract class CommonDeeWorkspaceTestNew extends CommonCoreTest {
	
	static {
		disableWorkspaceAutoBuild();
		disableDLTKIndexer();
	}
	
	private static void disableWorkspaceAutoBuild() {
		IWorkspaceDescription desc = ResourcesPlugin.getWorkspace().getDescription();
		desc.setAutoBuilding(false);
		try {
			ResourcesPlugin.getWorkspace().setDescription(desc);
		} catch (CoreException e) {
			throw ExceptionAdapter.unchecked(e);
		}
		assertTrue(ResourcesPlugin.getWorkspace().isAutoBuilding() == false);
	}
	
	@SuppressWarnings("restriction")
	protected static void disableDLTKIndexer() {
		IndexManager indexManager = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		indexManager.disable();
	}
	
	@SuppressWarnings("restriction")
	protected static void enableDLTKIndexer(boolean waitUntilReady) {
		IndexManager indexManager = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		indexManager.enable();
		if(waitUntilReady) {
			indexManager.waitUntilReady();
		}
	}
	
	/* ----------------- ----------------- */
	
	public static IProject project(String name) {
		return EclipseUtils.getProject(name);
	}
	
	public static IProject createLangProject(String name, boolean overwrite) throws CoreException {
		IProject project = createAndOpenProject(name, overwrite);
		setupLangProject(project, false);
		
		assertTrue(project.exists());
		return project;
	}
	
	public static void writeDubManifest(IProject project, String bundleName, String... sourceFolders) 
			throws CoreException {
		String sourceFoldersStr = '"' + StringUtil.collToString(sourceFolders, "\", \"") + '"';
		
		CommonDeeWorkspaceTest.writeStringToFile(project, BundlePath.DUB_MANIFEST_FILENAME, MiscJsonUtils.jsDocument(
			MiscJsonUtils.jsStringEntry("name", bundleName),
			MiscJsonUtils.jsEntryValue("sourcePaths", "[ " + sourceFoldersStr + " ]"),
			MiscJsonUtils.jsEntryValue("importPaths", "[ " + sourceFoldersStr + " ]")
		));
	}
	
}