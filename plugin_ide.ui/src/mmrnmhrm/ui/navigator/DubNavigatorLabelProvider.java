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
import static melnorme.lang.ide.ui.views.AbstractLangLabelProvider.fgColor;
import melnorme.lang.ide.ui.views.AbstractLangLabelProvider;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.workspace.viewmodel.DubDepSourceFolderElement;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;
import mmrnmhrm.core.workspace.viewmodel.DubDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.DubErrorElement;
import mmrnmhrm.core.workspace.viewmodel.DubRawDependencyElement;
import mmrnmhrm.core.workspace.viewmodel.StdLibContainer;
import mmrnmhrm.ui.DeeImages;
import mmrnmhrm.ui.navigator.DubNavigatorContentProvider.DubAllContentElementsSwitcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;

public class DubNavigatorLabelProvider extends AbstractLangLabelProvider implements IStyledLabelProvider {
	
	@Override
	public StyledString getStyledText(Object element) {
		return new DubElementTextProvider().switchElement(element);
	}
	
	@Override
	public Image getImage(Object element) {
		return new DubElementImageProvider().switchElement(element);
	}
	
}

class DubElementTextProvider extends DubAllContentElementsSwitcher<StyledString>{
	
	protected static final RGB DUB_LOCATION_ANNOTATION_FG = new RGB(128, 128, 128);
	protected static final RGB DUB_VERSION_ANNOTATION_FG = new RGB(120, 120, 200);
	protected static final RGB DUB_DEPCONTAINER_ANNOTATION_FG = new RGB(128, 128, 128);
	protected static final RGB DUB_DEPCONTAINER_ERROR_ANNOTATION_FG = new RGB(196, 64, 64);
	
	@Override
	public StyledString visitStdLibContainer(StdLibContainer element) {
		StyledString baseText = new StyledString("D Standard Library");
		if(element.isMissingStdLib()) {
			return baseText.append(" [Error: none found]", fgColor(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG)); 
		}
		return baseText;
	}
	
	@Override
	public StyledString visitDepContainer(DubDependenciesContainer element) {
		StyledString baseText = new StyledString("DUB Dependencies");
		
		DubBundleDescription bundleInfo = element.getBundleInfo();
		if(bundleInfo.hasErrors()) {
			// TODO: present more details about origin of error (json or dub describre)
			if(bundleInfo.isResolved()) {
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
	public StyledString visitRawDepElement(DubRawDependencyElement element) {
		return new StyledString(element.getBundleName());
	}
	
	@Override
	public StyledString visitErrorElement(DubErrorElement element) {
		return new StyledString(element.errorDescription);
	}
	
	@Override
	public StyledString visitDepElement(DubDependencyElement element) {
		StyledString baseString = new StyledString(element.getBundleName());
		baseString = appendVersionString(baseString, element.getDubBundle());
		return baseString.append(" - " + element.getDubBundle().getLocationString(), fgColor(DUB_LOCATION_ANNOTATION_FG));
	}
	
	@Override
	public StyledString visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return new StyledString(element.getSourceFolderLocalPath().toString());
	}
	
	@Override
	public StyledString visitDubManifestFile(IFile element) {
		StyledString baseString = new StyledString(element.getName());
		DubBundleDescription bundleInfo = DeeCore.getWorkspaceModel().getBundleInfo(element.getProject());
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
	
	@Override
	public StyledString visitModelElement(IModelElement element, IParent elementAsParent) {
		// TODO
		return new StyledString(element.getElementName());
	}
	
}

class DubElementImageProvider extends DubAllContentElementsSwitcher<Image>{
	
	@Override
	public Image visitDepContainer(DubDependenciesContainer element) {
		return DeeImages.DUB_DEPS_CONTAINER.getImage();
	}
	
	@Override
	public Image visitStdLibContainer(StdLibContainer element) {
		return DeeImages.DUB_STD_LIB.getImage();
	}
	
	@Override
	public Image visitRawDepElement(DubRawDependencyElement element) {
		return DeeImages.DUB_RAW_DEP.getImage();
	}
	
	@Override
	public Image visitErrorElement(DubErrorElement element) {
		return DeeImages.DUB_ERROR_ELEMENT.getImage();
	}
	
	@Override
	public Image visitDepElement(DubDependencyElement element) {
		return DeeImages.DUB_BUNDLE_DEP.getImage();
	}
	
	@Override
	public Image visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return DeeImages.SOURCE_FOLDER.getImage();
	}
	
	@Override
	public Image visitDubManifestFile(IFile element) {
		return DeeImages.DUB_MANIFEST.getImage();
	}
	
	@Override
	public Image visitDubCacheFolder(IFolder element) {
		return DeeImages.BINARY_FOLDER.getImage();
	}
	
	@Override
	public Image visitDubSourceFolder(IFolder element) {
		return DeeImages.SOURCE_FOLDER.getImage();
	}
	
	// TODO: review this usage
	protected final ScriptUILabelProvider scriptLabelProvider = new ScriptUILabelProvider();
	
	@Override
	public Image visitModelElement(IModelElement element, IParent elementAsParent) {
		return scriptLabelProvider.getImage(element);
	}
	
}