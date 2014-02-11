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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

import dtool.dub.DubBundle;


public class DubElementModelTest extends DubProjectModelTest {
	
	protected DubDependenciesContainer getDubContainer(IScriptProject dubProject) {
		return DubProjectModel.getDefault().getDubElement(dubProject.getProject());
	}
	
	@Override
	public void checkBundle(IScriptProject dubProject, String expectedError, String dubName, String[] srcFolders,
			DubBundle... deps) throws CoreException {
		
		DubDependenciesContainer dubElement = getDubContainer(dubProject);
		assertNotNull(dubElement);
		
		HashSet<CommonDubElement> children = hashSet(dubElement.getChildren());
		for (DubBundle dubBundle : deps) {
			removeChild(children, dubBundle);
		}
	}
	
	protected void removeChild(HashSet<CommonDubElement> children, DubBundle dubBundle) {
		String name = dubBundle.name;
		for (CommonDubElement dubElement : children) {
			if(dubElement instanceof DubDependencyElement) {
				DubDependencyElement dubDependencyElement = (DubDependencyElement) dubElement;
				if(dubDependencyElement.getBundleName().equals(name)) {
					children.remove(dubElement);
					return;
				}
			}
		}
	}
	
	@Override
	protected void checkErrorBundle(IScriptProject dubProject, String errorMsgStart) throws CoreException {
		DubDependenciesContainer dubElement = getDubContainer(dubProject);
		assertNotNull(dubElement);
		
		HashSet<CommonDubElement> children = hashSet(dubElement.getChildren());
		assertTrue(children.isEmpty());
	}
	
}