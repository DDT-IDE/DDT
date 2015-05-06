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
package melnorme.lang.tooling;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;

@LANG_SPECIFIC
public enum CompletionProposalKind {
	
	UNKNOWN,
	KEYWORD,
	ERROR,
	
	MODULEDEC,
	PACKAGE,
	
	VARIABLE,
	
	FUNCTION,
	CONSTRUCTOR,
	
	CLASS,
	INTERFACE,
	STRUCT,
	UNION,
	ENUM,
	TEMPLATE,
	NATIVE,
	
	ALIAS,
	
	MIXIN,
	TUPLE,
	TYPE,
	
	/* ----------------- Language-specific: ----------------- */
	;
	
	
	public <RET> RET switchOnKind(ProposalKindVisitor<RET> visitor) {
		switch(this) {
		case UNKNOWN: return visitor.visitUnknown();
		case KEYWORD: return visitor.visitKeyword();
		case ERROR: return visitor.visitError();
		
		case MODULEDEC: return visitor.visitModule();
		case PACKAGE: return visitor.visitPackage();
		
		case VARIABLE: return visitor.visitVariable();
		
		case FUNCTION: return visitor.visitFunction();
		case CONSTRUCTOR: return visitor.visitConstructor();
		
		case STRUCT: return visitor.visitStruct();
		case CLASS: return visitor.visitClass();
		case INTERFACE: return visitor.visitInterface();
		case UNION: return visitor.visitUnion();
		case ENUM: return visitor.visitEnum();
		case TEMPLATE: return visitor.visitTemplate();
		case NATIVE: return visitor.visitNative();
		
		case ALIAS: return visitor.visitAlias();
		
		case TUPLE: return visitor.visitTuple();
		case TYPE: return visitor.visitType();
		case MIXIN: return visitor.visitMixin();
		
		/* ----------------- Language-specific: ----------------- */
		
		}
		throw assertUnreachable();
	}
	
	public static interface ProposalKindVisitor<RET> extends AbstractKindVisitor<RET> {
		
		RET visitPackage();
		
		RET visitUnion();
		RET visitTemplate();
		
		RET visitError();
		
		RET visitTuple();
		RET visitType();
		RET visitMixin();
		
	}
	
}
