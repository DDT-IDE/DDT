/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IParameter;

public class MethodParameterInfo_Copy implements IParameter {
	
	private final String name;
	private final String type;
	private final String defaultValue;
	
	public MethodParameterInfo_Copy(String name) {
		this(name, null, null);
	}
	
	public MethodParameterInfo_Copy(String name, String type, String defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof MethodParameterInfo_Copy) {
			final MethodParameterInfo_Copy other = (MethodParameterInfo_Copy) obj;
			return name.equals(other.name)
					&& CharOperation.equals(type, other.type)
					&& CharOperation.equals(defaultValue, other.defaultValue);
		}
		return false;
	}
}
