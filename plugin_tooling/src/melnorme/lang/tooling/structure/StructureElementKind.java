/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.structure;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.LANG_SPECIFIC;


@LANG_SPECIFIC
public enum StructureElementKind {
	
	VARIABLE,
	
	FUNCTION,
	CONSTRUCTOR,
	
	CLASS,
	INTERFACE,
	STRUCT,
	UNION,
	
	MODULEDEC,
	
	TEMPLATE,
	ALIAS,
	
	MIXIN,
	
	ENUMCONTAINER,
	ENUM;
	
	public static abstract class StructureElementKindVisitor<RET> extends AbstractStructureElementKindVisitor<RET> {
		
		
		public RET switchOnKind(StructureElementKind kind) {
			switch(kind) {
			case VARIABLE: return visitVariable();
			
			case FUNCTION: return visitFunction();
			case CONSTRUCTOR: return visitConstructor();
			
			case CLASS: return visitClass();
			case INTERFACE: return visitInterface();
			case STRUCT: return visitStruct();
			case UNION: return visitUnion();
			
			case MODULEDEC: return visitModuleDeclaration();
			
			case TEMPLATE: return visitTemplate();
			case ALIAS: return visitAlias();
			
			case MIXIN: return visitMixin();
			case ENUMCONTAINER: return visitEnumContainer();
			case ENUM: return visitEnum();
			}
			throw assertUnreachable();
		}
		
		protected abstract RET visitUnion();
		
		protected abstract RET visitMixin();
		
		protected abstract RET visitEnumContainer();
		
		protected abstract RET visitEnum();
		
	}
	
}