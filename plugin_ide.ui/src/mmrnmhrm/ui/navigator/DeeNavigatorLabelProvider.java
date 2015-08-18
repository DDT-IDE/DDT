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

import static melnorme.lang.ide.ui.views.StylerHelpers.fgColor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.RGB;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.ide.core.project_model.view.BundleErrorElement;
import melnorme.lang.ide.core.project_model.view.DependenciesContainer;
import melnorme.lang.ide.core.project_model.view.IBundleModelElement;
import melnorme.lang.ide.core.project_model.view.RawDependencyElement;
import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.views.LangNavigatorLabelProvider;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.workspace.viewmodel.DubDepSourceFolderElement;
import mmrnmhrm.core.workspace.viewmodel.DubDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.StdLibContainer;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.navigator.DeeNavigatorContentProvider.DeeNavigatorAllElementsSwitcher;

public class DeeNavigatorLabelProvider extends LangNavigatorLabelProvider implements IStyledLabelProvider {
	
	@Override
	protected DefaultGetStyledStringSwitcher getStyledString_switcher() {
		return new DubElementTextProvider();
	}
	
	@Override
	protected DefaultGetImageSwitcher getBaseImage_switcher() {
		return new DubElementImageProvider();
	}
	


class DubElementTextProvider extends DefaultGetStyledStringSwitcher
	implements DeeNavigatorAllElementsSwitcher<StyledString> {
	
	protected final RGB DUB_LOCATION_ANNOTATION_FG = new RGB(128, 128, 128);
	protected final RGB DUB_VERSION_ANNOTATION_FG = new RGB(120, 120, 200);
	protected final RGB DUB_DEPCONTAINER_ANNOTATION_FG = new RGB(128, 128, 128);
	protected final RGB DUB_DEPCONTAINER_ERROR_ANNOTATION_FG = new RGB(196, 64, 64);
	
	@Override
	public StyledString visitBundleElement2(IBundleModelElement bundleElement) {
		return new BundleModelGetStyledStringSwitcher() {
			
	@Override
	public StyledString visitStdLibContainer(StdLibContainer element) {
		StyledString baseText = new StyledString("D Standard Library");
		if(element.isMissingStdLib()) {
			return baseText.append(" [Error: none found]", fgColor(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG)); 
		}
		return baseText;
	}
	
	@Override
	public StyledString visitDepContainer(DependenciesContainer element) {
		StyledString baseText = new StyledString("DUB Dependencies");
		
		BundleInfo bundleInfo = element.getBundleInfo();
		
		DubBundleDescription bundleDesc = bundleInfo.getBundleDesc();
		if(bundleInfo.hasErrors()) {
			// TODO: present more details about origin of error (json or dub describre)
			if(bundleDesc.isResolved()) {
				return baseText.append(" [DUB error]", fgColor(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG)); 
			} else {
				return baseText.append(" [DUB error]", fgColor(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG));
			}
		} else {
			if(bundleInfo.isResolved()) {
				return baseText;
			} else {
				return baseText.append(" <dub describing>", fgColor(DUB_DEPCONTAINER_ANNOTATION_FG));
			}
		}
	}
	
	@Override
	public StyledString visitDepElement(DubDependencyElement element) {
		StyledString baseString = new StyledString(element.getBundleName());
		DubBundle dubBundle = element.getDubBundle();
		baseString = appendVersionString(baseString, dubBundle);
		return baseString.append(" - " + dubBundle.getLocationString(), fgColor(DUB_LOCATION_ANNOTATION_FG));
	}
	
	@Override
	public StyledString visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return new StyledString(element.getSourceFolderLocalPath().toString());
	}
		
		}.switchBundleElement(bundleElement);
	}
	
	@Override
	public StyledString visitDubManifestFile(IFile element) {
		StyledString baseString = new StyledString(element.getName());
		BundleInfo bundleInfo = DeeCore.getDeeBundleModel().getProjectInfo(element.getProject());
		if(bundleInfo == null) {
			return null;
		}
		return appendVersionString(baseString, bundleInfo.getMainBundle());
	}
	
	protected StyledString appendVersionString(StyledString baseStyled, DubBundle bundle) {
		String versionStr = bundle.version;
		if(versionStr == null) {
			versionStr = "?";
		}
		return baseStyled.append(" [" + versionStr + "]", fgColor(DUB_VERSION_ANNOTATION_FG));
	}
	
	@Override
	public StyledString visitDubCacheFolder(IFolder element) {
		return null; // Use defaults
	}
	
	@Override
	public StyledString visitDubSourceFolder(IFolder element) {
		return null; // Use defaults
	}
	
}

class DubElementImageProvider extends DefaultGetImageSwitcher 
		implements DeeNavigatorAllElementsSwitcher<ImageDescriptor> {
	
	@Override
	public ImageDescriptor visitBundleElement2(IBundleModelElement bundleElement) {
		return new BundleModelGetImageSwitcher() {
			@Override
			public ImageDescriptor visitStdLibContainer(StdLibContainer element) {
				return DeeImages.DUB_STD_LIB;
			}
			
			@Override
			public ImageDescriptor visitRawDepElement(RawDependencyElement element) {
				return DeeImages.DUB_RAW_DEP;
			}
			
			@Override
			public ImageDescriptor visitErrorElement2(BundleErrorElement element) {
				return LangImages.NAV_Error;
			}
			
			@Override
			public ImageDescriptor visitDepElement(DubDependencyElement element) {
				return DeeImages.DUB_BUNDLE_DEP;
			}
			
			@Override
			public ImageDescriptor visitDepSourceFolderElement(DubDepSourceFolderElement element) {
				return LangImages.NAV_SourceFolder;
			}
			
		}.switchBundleElement(bundleElement);
	}
	
	@Override
	public ImageDescriptor visitDubManifestFile(IFile element) {
		return DeeImages.DUB_MANIFEST;
	}
	
	@Override
	public ImageDescriptor visitDubCacheFolder(IFolder element) {
		return LangImages.NAV_OutputFolder;
	}
	
	@Override
	public ImageDescriptor visitDubSourceFolder(IFolder element) {
		return LangImages.NAV_SourceFolder;
	}
	
}

}