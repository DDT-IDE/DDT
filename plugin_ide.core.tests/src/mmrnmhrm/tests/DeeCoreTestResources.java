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

import melnorme.lang.ide.core.tests.LangCoreTestResources;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;


public class DeeCoreTestResources extends LangCoreTestResources {
	
	public static void createSrcFolderFromCoreResource(String resourcePath, IContainer destFolder) 
			throws CoreException {
		createFolderFromCoreTestsResource(resourcePath, destFolder);
		addSourceFolder(destFolder);
	}
	
	/** Setup the given folder as a source folder. */
	public static IProjectFragment addSourceFolder(IContainer folder) throws CoreException {
		IScriptProject dltkProj = DLTKCore.create(folder.getProject());
		IProjectFragment fragment = dltkProj.getProjectFragment(folder);
		if(!fragment.exists()) {
			IBuildpathEntry[] bpentries = dltkProj.getRawBuildpath();
			IBuildpathEntry entry = DLTKCore.newSourceEntry(fragment.getPath());
			dltkProj.setRawBuildpath(ArrayUtil.concat(bpentries, entry), null);
		}
		return fragment;
	}
	
}