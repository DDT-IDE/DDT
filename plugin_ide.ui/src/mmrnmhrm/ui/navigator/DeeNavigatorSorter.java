/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.navigator;

import java.text.Collator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import melnorme.lang.ide.ui.navigator.LangNavigatorSorter;
import mmrnmhrm.ui.navigator.DeeNavigatorContentProvider.DeeNavigatorAllElementsSwitcher;

public class DeeNavigatorSorter extends LangNavigatorSorter {
	
	public DeeNavigatorSorter() {
		super();
	}
	
	public DeeNavigatorSorter(Collator collator) {
		super(collator);
	}
	
	@Override
	protected LangNavigatorSorter_Switcher switcher_Sorter() {
		return new DeeSwitcher_Sorter();
	}
	
	protected static class DeeSwitcher_Sorter extends LangNavigatorSorter_Switcher
		implements DeeNavigatorAllElementsSwitcher<Integer> {
		
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
		
	}
	
}