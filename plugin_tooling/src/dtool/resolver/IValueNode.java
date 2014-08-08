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
package dtool.resolver;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;

public interface IValueNode {
	
	Collection<INamedElement> resolveTypeOfUnderlyingValue(IModuleResolver mr);
	
}
