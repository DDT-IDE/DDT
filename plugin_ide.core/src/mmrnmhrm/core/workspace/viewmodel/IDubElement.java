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
package mmrnmhrm.core.workspace.viewmodel;

public interface IDubElement extends IDeeNavigatorElement {
	
	public static IDubElement[] NO_CHILDREN = { };
	
	Object getParent();
	
	boolean hasChildren();

	/** Returns the node's children. */
	Object[] getChildren();
	
	public static enum DubElementType {
		DUB_DEP_CONTAINER,
		DUB_STD_LIB,
		DUB_RESOLVED_DEP,
		DUB_RAW_DEP,
		DUB_ERROR_ELEMENT,
		DUB_DEP_SRC_FOLDER,
	}
	
	public DubElementType getElementType();
	
	public String getElementName();
	
	public String getPathString();
	
}