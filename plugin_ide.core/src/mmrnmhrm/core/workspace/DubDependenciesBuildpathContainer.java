/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import static melnorme.utilbox.core.CoreUtil.array;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;

public class DubDependenciesBuildpathContainer implements IBuildpathContainer {
	
	public static String CONTAINER_PATH_ID = DeeCore.PLUGIN_ID + ".DubContainer";
	public static Path CONTAINER_PATH = new Path(CONTAINER_PATH_ID);
	
	protected final IPath containerPath;
	protected final IScriptProject project;
	protected final IBuildpathEntry[] entries;
	
	public DubDependenciesBuildpathContainer(IScriptProject project, IBuildpathEntry[] entries) {
		this.containerPath = CONTAINER_PATH;
		this.project = project;
		this.entries = entries;
	}
	
	@Override
	public IPath getPath() {
		return containerPath;
	}
	
	@Override
	public int getKind() {
		return IBuildpathContainer.K_APPLICATION;
	}
	
	@Override
	public String getDescription() {
		return "Dub Dependencies";
	}
	
	protected static final IBuildpathEntry[] NO_ENTRIES = { };
	
	@Override
	public IBuildpathEntry[] getBuildpathEntries() {
		return entries == null ? NO_ENTRIES : entries;
	}
	
	protected final static IAccessRule[] NO_ACCESS_RULES = {};
	
	protected static class DubBuildpathAttribute implements IBuildpathAttribute {
		@Override
		public String getName() {
			return "DubDependency";
		}
		
		@Override
		public String getValue() {
			return ""; // Don't return null, DLTK might not like it
		}
	}
	
	public static IBuildpathEntry createDubBuildpathEntry(IPath path) {
		return DLTKCore.newLibraryEntry(DLTKUtils.localEnvPath(path), 
				NO_ACCESS_RULES, array(new DubBuildpathAttribute()), true, true);
	}
	
	public static boolean isDubBuildpathEntry(IBuildpathEntry bpEntry) {
		for (IBuildpathAttribute bpAttrib : bpEntry.getExtraAttributes()) {
			if(bpAttrib instanceof DubBuildpathAttribute) {
				return true;
			}
		}
		return false;
	}
	
	public static class DubBPContainerInitializer extends BuildpathContainerInitializer {
		
		@Override
		public String getDescription(IPath containerPath, IScriptProject project) {
			return "Dub Container";
		}
		
		@Override
		public void initialize(IPath containerPath, IScriptProject project) throws CoreException {
			if(!containerPath.equals(CONTAINER_PATH)) {
				DeeCore.logError("containerPath doesn' match expected");
				return;
			}
//			IBuildpathContainer container = new DubBuildpathContainer(project, null);
//			DLTKCore.setBuildpathContainer(containerPath, array(project), array(container), null);
		}
		
	}
	
}