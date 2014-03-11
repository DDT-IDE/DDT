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
import mmrnmhrm.core.projectmodel.DubModel;
import mmrnmhrm.core.projectmodel.elements.DubDepSourceFolderElement;
import mmrnmhrm.core.projectmodel.elements.DubDependenciesContainer;
import mmrnmhrm.core.projectmodel.elements.DubDependencyElement;
import mmrnmhrm.core.projectmodel.elements.DubErrorElement;
import mmrnmhrm.core.projectmodel.elements.DubRawDependencyElement;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.navigator.DubNavigatorContentProvider.DubAllContentElementsSwitcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;

class ForegroundColorStyler extends Styler {
	protected final RGB fgColor;
	
	public ForegroundColorStyler(RGB fgColor) {
		this.fgColor = fgColor;
	}
	
	@Override
	public void applyStyles(TextStyle textStyle) {
		if(fgColor != null) {
			textStyle.foreground = new Color(Display.getCurrent(), fgColor);
		}
	}
}

public class DubNavigatorLabelProvider extends LabelProvider implements IStyledLabelProvider {
	
	@Override
	public String getText(Object element) {
		StyledString styledText = getStyledText(element);
		if(styledText != null) {
			return styledText.getString();
		}
		return null;
	}
	
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
	
	protected ForegroundColorStyler styler(RGB rgb) {
		return new ForegroundColorStyler(rgb);
	}
	
	@Override
	public StyledString visitDepContainer(DubDependenciesContainer element) {
		StyledString baseText = new StyledString("DUB Dependencies");
		
		DubBundleDescription bundleInfo = element.getBundleInfo();
		if(bundleInfo.hasErrors()) {
			// TODO: present more details about origin of error (json or dub describre)
			if(bundleInfo.isResolved()) {
				return baseText.append(" [DUB error]", styler(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG)); 
			} else {
				return baseText.append(" [DUB error]", styler(DUB_DEPCONTAINER_ERROR_ANNOTATION_FG));
			}
		} else {
			if(bundleInfo.isResolved()) {
				return baseText;
			} else {
				return baseText.append(" <dub describing>", styler(DUB_DEPCONTAINER_ANNOTATION_FG));
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
		return baseString.append(" - " + element.getDubBundle().getLocationString(), styler(DUB_LOCATION_ANNOTATION_FG));
	}
	
	@Override
	public StyledString visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return new StyledString(element.getSourceFolderLocalPath().toString());
	}
	
	@Override
	public StyledString visitDubManifestFile(IFile element) {
		StyledString baseString = new StyledString(element.getName());
		DubBundleDescription bundleInfo = DubModel.getBundleInfo(element.getProject().getName());
		return appendVersionString(baseString, bundleInfo.getMainBundle());
	}
	
	protected StyledString appendVersionString(StyledString baseStyled, DubBundle bundle) {
		String versionStr = bundle.version;
		if(versionStr == null) {
			versionStr = "?";
		}
		return baseStyled.append(" [" + versionStr + "]", styler(DUB_VERSION_ANNOTATION_FG));
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
		return DeePluginImages.getImage(DeePluginImages.DUB_DEPENDENCIES_CONTAINER);
	}
	
	@Override
	public Image visitRawDepElement(DubRawDependencyElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_RAW_DEP);
	}
	
	@Override
	public Image visitErrorElement(DubErrorElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_ERROR_ELEMENT);
	}
	
	@Override
	public Image visitDepElement(DubDependencyElement element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_BUNDLE_DEP);
	}
	
	@Override
	public Image visitDepSourceFolderElement(DubDepSourceFolderElement element) {
		return DeePluginImages.getImage(DeePluginImages.SOURCE_FOLDER);
	}
	
	@Override
	public Image visitDubManifestFile(IFile element) {
		return DeePluginImages.getImage(DeePluginImages.DUB_MANIFEST);
	}
	
	@Override
	public Image visitDubCacheFolder(IFolder element) {
		return DeePluginImages.getImage(DeePluginImages.BINARY_FOLDER);
	}
	
	@Override
	public Image visitDubSourceFolder(IFolder element) {
		return DeePluginImages.getImage(DeePluginImages.SOURCE_FOLDER);
	}
	
	// TODO: review this usage
	protected final ScriptUILabelProvider scriptLabelProvider = new ScriptUILabelProvider();
	
	@Override
	public Image visitModelElement(IModelElement element, IParent elementAsParent) {
		return scriptLabelProvider.getImage(element);
	}
	
}