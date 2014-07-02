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
package mmrnmhrm.core.workspace.viewmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

import melnorme.lang.ide.core.utils.EclipseUtils;
import mmrnmhrm.core.DLTKUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;

import dtool.dub.BundlePath;

public class DubDepSourceFolderElement extends CommonDubElement<DubDependencyElement> {
	
	protected final Path srcFolderPath;
	protected final IScriptProject scriptProject;
	
	public DubDepSourceFolderElement(DubDependencyElement parent, Path srcFolderPath, 
			IScriptProject scriptProject) {
		super(parent);
		this.srcFolderPath = assertNotNull(srcFolderPath);
		this.scriptProject = assertNotNull(scriptProject);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_SRC_FOLDER;
	}
	
	public Path getSourceFolderLocalPath() {
		return srcFolderPath;
	}
	
	@Override
	public String getElementName() {
		return srcFolderPath.toString();
	}
	
	@Override
	public String getPathString() {
		return getParent().getPathString() + "/"+getElementName()+"::";
	}
	
	public IProjectFragment getUnderlyingProjectFragment() {
		BundlePath bundlePath = getParent().getDubBundle().getBundlePath();
		if(bundlePath == null) {
			return null;
		}
		Path path = bundlePath.resolve(srcFolderPath);
		IPath bpPath = DLTKUtils.localEnvPath(EclipseUtils.path(path));
		try {
			return scriptProject.findProjectFragment(bpPath);
		} catch (ModelException e) {
			return null;
		}
	}
	
	@Override
	public boolean hasChildren() {
		IProjectFragment projectFragment = getUnderlyingProjectFragment();
		try {
			return projectFragment != null && projectFragment.hasChildren();
		} catch (ModelException e) {
			return false;
		}
	}
	
	@Override
	public Object[] getChildren() {
		IProjectFragment projectFragment = getUnderlyingProjectFragment();
		try {
			return projectFragment == null ? NO_CHILDREN : projectFragment.getChildren();
		} catch (ModelException e) {
			return NO_CHILDREN;
		}
	}
}