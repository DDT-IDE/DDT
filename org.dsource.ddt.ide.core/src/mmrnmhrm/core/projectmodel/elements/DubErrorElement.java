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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

public class DubErrorElement extends CommonDubElement<IDubElement> {
	
	public final String errorDescription;
	
	public DubErrorElement(IDubElement parent, String errorDescription) {
		super(parent);
		this.errorDescription = assertNotNull(errorDescription);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_ERROR_ELEMENT;
	}
	
	@Override
	public String getElementName() {
		return "<error>";
	}
	
	@Override
	public String getPathString() {
		return getParent().getPathString() + "/" + getElementName();
	}
}