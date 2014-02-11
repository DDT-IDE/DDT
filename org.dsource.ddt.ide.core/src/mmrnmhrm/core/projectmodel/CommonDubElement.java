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
import melnorme.utilbox.tree.IElement;

public abstract class CommonDubElement implements IElement {
	
	public static enum DubElementType {
		DUB_DEP_CONTAINER,
		DUB_DEP_ELEMENT,
	}
	
	protected final CommonDubElement parent;
	
	public CommonDubElement(CommonDubElement parent) {
		this.parent = parent;
	}
	
	@Override
	public CommonDubElement getParent() {
		return parent;
	}
	
	public abstract DubElementType getElementType();
	
	@Override
	public boolean hasChildren() {
		return false; // TODO
	}
	
	@Override
	public CommonDubElement[] getChildren() {
		return null;
	}
	
	public final <RET> RET acceptSwitcher(DubElementSwitcher<RET> switcher) {
		return switcher.switchElement(this);
	}
	
	public static abstract class DubElementSwitcher<RET> {
		
		public RET switchElement(CommonDubElement element) {
			switch (element.getElementType()) {
			case DUB_DEP_CONTAINER: return visitDepContainer((DubDependenciesContainer) element);
			case DUB_DEP_ELEMENT: return visitDepElement((DubDependencyElement) element);
			}
			throw assertUnreachable();
		}
		
		public abstract RET visitDepContainer(DubDependenciesContainer element);
		
		public abstract RET visitDepElement(DubDependencyElement element);
		
	}
	
}