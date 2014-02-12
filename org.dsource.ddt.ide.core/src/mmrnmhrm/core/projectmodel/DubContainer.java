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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.CoreUtil.array;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;

public class DubContainer implements IBuildpathContainer {
	
	protected final IPath containerPath;
	protected final IScriptProject project;
	protected final IBuildpathEntry[] entries;
	
	public DubContainer(IPath containerPath, IScriptProject project, IBuildpathEntry[] entries) {
		this.containerPath = containerPath;
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
	
	protected static final IBuildpathEntry[] NO_ENTRIES = new IBuildpathEntry[0];
	
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
		return DLTKCore.newLibraryEntry(localEnvPath(path), 
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
	
	public static IPath localEnvPath(IPath path) {
		return EnvironmentPathUtils.getFullPath(LocalEnvironment.getInstance(), path);
	}
	
}