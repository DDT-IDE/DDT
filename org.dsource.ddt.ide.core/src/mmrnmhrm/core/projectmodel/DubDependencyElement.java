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

import dtool.dub.DubBundle;

public class DubDependencyElement extends CommonDubElement {
	
	protected DubBundle dubBundle;
	
	public DubDependencyElement(CommonDubElement parent, DubBundle dubBundle) {
		super(parent);
		this.dubBundle = dubBundle;
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_ELEMENT;
	}
	
	public String getBundleName() {
		return dubBundle.name;
	}
	
}