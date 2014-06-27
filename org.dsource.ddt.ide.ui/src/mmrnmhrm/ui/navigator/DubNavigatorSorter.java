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
package mmrnmhrm.ui.navigator;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.text.Collator;

import mmrnmhrm.core.projectmodel.elements.DubDepSourceFolderElement;
import mmrnmhrm.core.projectmodel.elements.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.elements.DubDependencyElement;
import mmrnmhrm.core.projectmodel.elements.DubErrorElement;
import mmrnmhrm.core.projectmodel.elements.DubRawDependencyElement;
import mmrnmhrm.core.projectmodel.elements.StdLibContainer;
import mmrnmhrm.ui.navigator.DubNavigatorContentProvider.DubAllContentElementsSwitcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.jface.viewers.ViewerSorter;

public class DubNavigatorSorter extends ViewerSorter {
	
	public DubNavigatorSorter() {
		super();
	}
	
	public DubNavigatorSorter(Collator collator) {
		super(collator);
	}
	
	@Override
	public int category(Object element) {
		return new DubAllContentElementsSwitcher<Integer>() {
			// Note: make sure we don't allocate Integer's here:
			// for values between -127 and 127 Java has a cache, so it's fine (see Integer.valueOf(int))
			
			@Override
			public Integer visitProject(IProject project) {
				assertFail();
				return null;
			}
			
			@Override
			public Integer visitModelElement(IModelElement element, IParent elementAsParent) {
				assertFail();
				return null;
			}
			
			@Override
			public Integer visitStdLibContainer(StdLibContainer element) {
				return -20;
			}
			
			@Override
			public Integer visitDepContainer(DubDependenciesContainer element) {
				return -10;
			}
			
			@Override
			public Integer visitRawDepElement(DubRawDependencyElement element) {
				return 0;
			}
			
			@Override
			public Integer visitErrorElement(DubErrorElement element) {
				return -10;
			}
			
			@Override
			public Integer visitDepElement(DubDependencyElement element) {
				return 0;
			}
			
			@Override
			public Integer visitDepSourceFolderElement(DubDepSourceFolderElement element) {
				return 0;
			}
			
			@Override
			public Integer visitDubSourceFolder(IFolder element) {
				return -5;
			}
			
			@Override
			public Integer visitDubCacheFolder(IFolder element) {
				return -4;
			}
			
			@Override
			public Integer visitDubManifestFile(IFile element) {
				return 0;
			}
			
			@Override
			public Integer visitOther(Object element) {
				if(element instanceof IFolder) {
					return -2;
				} 
				return 0;
			}
			
		}.switchElement(element);
	}
	
}