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
package mmrnmhrm.ui.navigator;
import mmrnmhrm.core.projectmodel.CommonDubElement;
import mmrnmhrm.core.projectmodel.CommonDubElement.DubElementSwitcher;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.DubDependencyElement;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class DubNavigatorLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if(element instanceof CommonDubElement) {
			CommonDubElement dubElement = (CommonDubElement) element;
			return new DubElementTextProvider().switchElement(dubElement);
		}
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof CommonDubElement) {
			CommonDubElement dubElement = (CommonDubElement) element;
			return new DubElementImageProvider().switchElement(dubElement);
		}
		return null;
	}
	
}

class DubElementTextProvider extends DubElementSwitcher<String>{

	@Override
	public String visitDepContainer(DubDependenciesContainer element) {
		return "Dub Dependencies";
	}

	@Override
	public String visitDepElement(DubDependencyElement element) {
		return element.getBundleName();
	}
	
}

class DubElementImageProvider extends DubElementSwitcher<Image>{

	@Override
	public Image visitDepContainer(DubDependenciesContainer element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_DEPENDENCIES_CONTAINER);
	}

	@Override
	public Image visitDepElement(DubDependencyElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_DEP_ELEMENT);
	}
	
}