/*******************************************************************************
 * Copyright (c) 2013, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.intrinsics;

import java.util.ArrayList;

import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.utilbox.misc.CollectionUtil;
import dtool.resolver.ReferenceResolver;

public class InstrinsicsScope implements IScopeProvider {
	
	public final ArrayList<IntrinsicDefUnit> members;
	
	public InstrinsicsScope(IntrinsicDefUnit... members) {
		this.members = CollectionUtil.createArrayList(members); 
	}
	
	public InstrinsicsScope(ArrayList<IntrinsicDefUnit> members) {
		this.members = members; 
	}
	
	@Override
	public void resolveSearchInScope(CommonScopeLookup search) {
		ReferenceResolver.findInNamedElementList(search, members);
	}
	
}