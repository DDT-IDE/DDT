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
package mmrnmhrm.core.workspace.viewmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.arrayFrom;

import java.util.ArrayList;

import melnorme.utilbox.misc.Location;

import org.eclipse.core.resources.IProject;

import dtool.engine.StandardLibraryResolution;
import dtool.engine.compiler_installs.CompilerInstall;

public class StdLibContainer extends CommonDubElement<IProject> {
	
	protected final CompilerInstall compilerInstall;
	protected final IDubElement[] depElements;
	
	public StdLibContainer(CompilerInstall compilerInstall, IProject project) {
		super(project);
		this.compilerInstall = assertNotNull(compilerInstall);
		this.depElements = createChildren();
	}
	
	public CompilerInstall getCompilerInstall() {
		return compilerInstall;
	}
	
	public boolean isMissingStdLib() {
		return compilerInstall == StandardLibraryResolution.NULL_COMPILER_INSTALL;
	}
	
	protected DubDepSourceFolderElement[] createChildren() {
		ArrayList<DubDepSourceFolderElement> sourceContainers = new ArrayList<>();
		
		for (Location localPath : getCompilerInstall().getLibrarySourceFolders()) {
			sourceContainers.add(new DubDepSourceFolderElement(this, localPath.path));
		}
		return arrayFrom(sourceContainers, DubDepSourceFolderElement.class);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_STD_LIB;
	}
	
	public IProject getProject() {
		return getParent();
	}
	
	@Override
	public String getElementName() {
		return "{StdLib}";
	}
	
	@Override
	public String getPathString() {
		return getProject().getFullPath().toPortableString() + "/" + getElementName();
	}
	
	@Override
	public boolean hasChildren() {
		return depElements.length > 0;
	}
	
	@Override
	public IDubElement[] getChildren() {
		return depElements;
	}
	
}