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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

public class DubDepSourceFolderElement extends CommonDubElement<IDubElement> {
	
	protected final Path srcFolderPath;
	
	public DubDepSourceFolderElement(IDubElement parent, Path srcFolderPath) {
		super(parent);
		this.srcFolderPath = assertNotNull(srcFolderPath);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_SRC_FOLDER;
	}
	
	public Path getSourceFolderLocalPath() {
		return srcFolderPath;
	}
	
	@Override
	public String getElementName() {
		return srcFolderPath.toString();
	}
	
	@Override
	public String getPathString() {
		return getParent().getPathString() + "/"+getElementName()+"::";
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public Object[] getChildren() {
		return NO_CHILDREN;
	}
	
}