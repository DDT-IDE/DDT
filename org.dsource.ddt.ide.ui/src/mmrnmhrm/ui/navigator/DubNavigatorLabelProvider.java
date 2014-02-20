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
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubDependencyElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubErrorElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubRawDependencyElement;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import dtool.dub.DubBundleDescription;


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
		String baseText = "Dub Dependencies";
		
		DubBundleDescription bundleInfo = element.getBundleInfo();
		if(bundleInfo.hasErrors()) {
			if(bundleInfo.isResolved()) {
				return baseText + " [DUB error]"; // TODO: query exception for more details
			} else {
				return baseText + " [DUB error]";
			}
		} else {
			if(bundleInfo.isResolved()) {
				return baseText;
			} else {
				return baseText + " <dub describing>";
			}
		}
	}

	@Override
	public String visitDepElement(DubDependencyElement element) {
		return element.getBundleName();
	}
	
	@Override
	public String visitRawDepElement(DubRawDependencyElement element) {
		return element.getBundleName();
	}
	
	@Override
	public String visitErrorElement(DubErrorElement element) {
		return element.errorDescription;
	}
	
}

class DubElementImageProvider extends DubElementSwitcher<Image>{

	@Override
	public Image visitDepContainer(DubDependenciesContainer element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_DEPENDENCIES_CONTAINER);
	}

	@Override
	public Image visitDepElement(DubDependencyElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_BUNDLE_DEP);
	}
	
	@Override
	public Image visitRawDepElement(DubRawDependencyElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_RAW_DEP);
	}
	
	@Override
	public Image visitErrorElement(DubErrorElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_ERROR_ELEMENT);
	}
	
}