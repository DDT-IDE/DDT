/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

public enum EArcheType {
	Module,
	Package,
	
	Variable,
	Function,
	Constructor,
	
	//Native,
	Struct(true),
	Union(true),
	Class(true),
	Interface(true),
	
	Template,
	TypeParameter(true),
	Mixin,
	Tuple, //This probably should not be an archetype
	
	Enum(true),
	EnumMember, // Similar to Variable
	
	Alias,
	
	Error,
	;
	
	protected final boolean isType;
	
	private EArcheType() {
		this(false);
	}
	
	private EArcheType(boolean isType) {
		this.isType = isType;
	}
	
	/** Archetype kind is TYPE, meaning it can be used to declare variables. */
	public boolean isType() {
		return isType;
	}
	
	public boolean isError() {
		return this == Error;
	}
	
}