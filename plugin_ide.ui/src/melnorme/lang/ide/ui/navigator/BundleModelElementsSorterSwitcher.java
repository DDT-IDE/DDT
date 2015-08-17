/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.navigator;

import melnorme.lang.ide.ui.navigator.LangNavigatorSorter.BundleModelElementsSorterSwitcher_Default;
import mmrnmhrm.core.workspace.viewmodel.DubDepSourceFolderElement;
import mmrnmhrm.core.workspace.viewmodel.DubDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.StdLibContainer;

public class BundleModelElementsSorterSwitcher extends BundleModelElementsSorterSwitcher_Default {
	
	@Override
	public Integer visitStdLibContainer(StdLibContainer element) {
		return -20;
	}
	
	@Override
	public Integer visitDepElement(DubDependencyElement element) {
		return 0;
	}
	
	@Override
	public Integer visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return 0;
	}
	
}