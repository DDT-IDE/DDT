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

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;

public class DubDependenciesContainer extends CommonDubElement {
	
	protected final DubBundleDescription bundleInfo;
	
	protected CommonDubElement[] depElements;
	
	public DubDependenciesContainer(DubBundleDescription bundleInfo) {
		super(null);
		this.bundleInfo = bundleInfo;
		createChildren();
	}
	
	protected CommonDubElement[] createChildren() {
		ArrayList<DubDependencyElement> newChildren = new ArrayList<>();
		
		if(bundleInfo != null) {
			for (DubBundle dubBundle : bundleInfo.getBundleDependencies()) {
				newChildren.add(new DubDependencyElement(this, dubBundle));
			}
		}
		return ArrayUtil.createFrom(newChildren, CommonDubElement.class);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_CONTAINER;
	}
	
	public DubBundleDescription getBundleInfo() {
		return bundleInfo;
	}
	
	@Override
	public boolean hasChildren() {
		return depElements.length > 0;
	}
	
	@Override
	public CommonDubElement[] getChildren() {
		return depElements;
	}
	
}