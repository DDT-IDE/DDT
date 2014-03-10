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
package mmrnmhrm.core.projectmodel.elements;

import dtool.dub.DubBundle.DubDependecyRef;

public class DubRawDependencyElement extends CommonDubElement<DubDependenciesContainer> {
	
	protected DubDependecyRef dubBundleRef;
	
	public DubRawDependencyElement(DubDependenciesContainer parent, DubDependecyRef dubBundleRef) {
		super(parent);
		this.dubBundleRef = dubBundleRef;
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_RAW_DEP;
	}
	
	public String getBundleName() {
		return dubBundleRef.bundleName;
	}
	
	@Override
	public String getElementName() {
		return getBundleName();
	}
	
	@Override
	public String getPathString() {
		return getParent().getPathString() + "/@" + getElementName();
	}
}