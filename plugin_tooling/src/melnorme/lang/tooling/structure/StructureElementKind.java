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
import melnorme.lang.tooling.AbstractKindVisitor;
import melnorme.lang.tooling.LANG_SPECIFIC;


@LANG_SPECIFIC
public enum StructureElementKind {
	
	MODULEDEC,
	
	VARIABLE,
	
	FUNCTION,
	CONSTRUCTOR,
	
	CLASS,
	INTERFACE,
	STRUCT,
	UNION,
	ENUM_TYPE,
	
	TEMPLATE,
	ALIAS,
	
	MIXIN,
	;
	
	public <RET> RET switchOnKind(StructureElementKindVisitor<RET> visitor) {
		return switchOnKind(this, visitor);
	}
	
	public static <RET> RET switchOnKind(StructureElementKind kind, StructureElementKindVisitor<RET> visitor) {
		switch(kind) {
		case VARIABLE: return visitor.visitVariable();
		
		case FUNCTION: return visitor.visitFunction();
		case CONSTRUCTOR: return visitor.visitConstructor();
		
		case CLASS: return visitor.visitClass();
		case INTERFACE: return visitor.visitInterface();
		case STRUCT: return visitor.visitStruct();
		case UNION: return visitor.visitUnion();
		
		case MODULEDEC: return visitor.visitModule();
		
		case TEMPLATE: return visitor.visitTemplate();
		case ALIAS: return visitor.visitAlias();
		
		case MIXIN: return visitor.visitMixin();
		case ENUM_TYPE: return visitor.visitEnum();
		}
		throw assertUnreachable();
	}
	
	public static interface StructureElementKindVisitor<RET> extends AbstractKindVisitor<RET> {
		
		public abstract RET visitUnion();
		
		public abstract RET visitMixin();
		
		public abstract RET visitTemplate();
		
	}
	
}