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
package mmrnmhrm.core.projectmodel.elements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import melnorme.utilbox.misc.MiscUtil;

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
		return false;
	}
	@Override
	public Object[] getChildren() {
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof IDubElement)) {
			return false;
		}
		IDubElement otherDubElement = (IDubElement) other;
		
		if(this.getElementType() != otherDubElement.getElementType()) {
			return false;
		}
		if(!areEqual(this.getElementName(), otherDubElement.getElementName())) {
			return false;
		}
		
		return areEqual(this.getParent(), otherDubElement.getParent());
	}
	
	@Override
	public int hashCode() {
		return MiscUtil.combineHashCodes(getParent().hashCode(), 
			getElementName().hashCode());
	}
	
	@Override
	public String toString() {
		return getPathString() + "  #" + getClass().getSimpleName();
	}
	
	/* ----------------- ----------------- */
	
	public static abstract class DubElementSwitcher<RET> {
		
		public RET switchElement(IDubElement element) {
			switch (element.getElementType()) {
			case DUB_DEP_CONTAINER: return visitDepContainer((DubDependenciesContainer) element);
			case DUB_STD_LIB: return visitStdLibContainer((StdLibContainer) element);
			case DUB_RAW_DEP: return visitRawDepElement((DubRawDependencyElement) element);
			case DUB_ERROR_ELEMENT: return visitErrorElement((DubErrorElement) element);
			case DUB_RESOLVED_DEP: return visitDepElement((DubDependencyElement) element);
			case DUB_DEP_SRC_FOLDER: return visitDepSourceFolderElement((DubDepSourceFolderElement) element);
			}
			throw assertUnreachable();
		}
		
		public abstract RET visitDepContainer(DubDependenciesContainer element);
		
		public abstract RET visitStdLibContainer(StdLibContainer element);
		
		public abstract RET visitRawDepElement(DubRawDependencyElement element);
		
		public abstract RET visitErrorElement(DubErrorElement element);
		
		public abstract RET visitDepElement(DubDependencyElement element);
		
		public abstract RET visitDepSourceFolderElement(DubDepSourceFolderElement element);
		
	}
	
}