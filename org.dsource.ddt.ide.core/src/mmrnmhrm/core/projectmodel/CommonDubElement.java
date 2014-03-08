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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubDependencySourceFolderElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubDependencyElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubErrorElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubRawDependencyElement;

public abstract class CommonDubElement<PARENT> implements IDubElement {
	
	protected final PARENT parent;
	
	public CommonDubElement(PARENT parent) {
		this.parent = parent;
	}
	
	@Override
	public PARENT getParent() {
		return parent;
	}
	
	@Override
	public boolean hasChildren() {
		return false; // TODO
	}
	
	@Override
	public Object[] getChildren() {
		return null;
	}
	
	public static abstract class DubElementSwitcher<RET> {
		
		public RET switchElement(IDubElement element) {
			switch (element.getElementType()) {
			case DUB_DEP_CONTAINER: return visitDepContainer((DubDependenciesContainer) element);
			case DUB_RAW_DEP: return visitRawDepElement((DubRawDependencyElement) element);
			case DUB_ERROR_ELEMENT: return visitErrorElement((DubErrorElement) element);
			case DUB_RESOLVED_DEP: return visitDepElement((DubDependencyElement) element);
			case DUB_DEP_SRC_FOLDER: return visitDepSourceFolderElement((DubDependencySourceFolderElement) element);
			}
			throw assertUnreachable();
		}
		
		public abstract RET visitDepContainer(DubDependenciesContainer element);
		
		public abstract RET visitRawDepElement(DubRawDependencyElement element);
		
		public abstract RET visitErrorElement(DubErrorElement element);
		
		public abstract RET visitDepElement(DubDependencyElement element);
		
		public abstract RET visitDepSourceFolderElement(DubDependencySourceFolderElement element);
		
	}
	
}