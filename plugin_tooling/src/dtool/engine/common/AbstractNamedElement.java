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
package dtool.engine.common;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.definitions.INamedElement;

public abstract class AbstractNamedElement implements INamedElement {
	
	protected final String name;
	
	public AbstractNamedElement(String name) {
		this.name = assertNotNull(name);
	}
	
	@Override
	public final String getName() {
		return name;
	}
	
	@Override
	public String getExtendedName() {
		return name;
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return getName();
	}
	
}