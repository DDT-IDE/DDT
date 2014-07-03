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
package mmrnmhrm.core.workspace.viewmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;

public class DubDependenciesContainer extends CommonDubElement<IProject> {
	
	protected final DubBundleDescription bundleInfo;
	protected final IDubElement[] depElements;
	
	public DubDependenciesContainer(DubBundleDescription bundleInfo, IProject project) {
		super(project);
		this.bundleInfo = assertNotNull(bundleInfo);
		depElements = createChildren();
	}
	
	protected IDubElement[] createChildren() {
		ArrayList<IDubElement> newChildren = new ArrayList<>();
		
		if(bundleInfo.isResolved()) {
			for (DubBundle dubBundle : bundleInfo.getBundleDependencies()) {
				newChildren.add(new DubDependencyElement(this, dubBundle));
			}
		} else {
			for (DubDependecyRef dubBundleRef : bundleInfo.getMainBundle().getDependencyRefs()) {
				newChildren.add(new DubRawDependencyElement(this, dubBundleRef));
			}
		}
		if(bundleInfo.getError() != null) {
			newChildren.add(new DubErrorElement(this, bundleInfo.getError().getMessage()));
		}
		return ArrayUtil.createFrom(newChildren, IDubElement.class);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_CONTAINER;
	}
	
	public DubBundleDescription getBundleInfo() {
		return bundleInfo;
	}
	
	public IProject getProject() {
		return getParent();
	}
	
	@Override
	public String getElementName() {
		return "{Dependencies}";
	}
	
	@Override
	public String getPathString() {
		return getProject().getFullPath().toPortableString() + "/" + getElementName();
	}
	
	@Override
	public boolean hasChildren() {
		return depElements.length > 0;
	}
	
	@Override
	public IDubElement[] getChildren() {
		return depElements;
	}
	
}