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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.symbols.INamedElement;


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
	
	public <T extends ArchetypeSwitchVisitor> T accept(INamedElement element, T visitor) {
		visitor.visit(element, this);
		return visitor;
	}
	
	/* ----------------- Visitor ----------------- */
	
	public static class ArchetypeSwitchVisitor {
		
		public void visit(INamedElement element, EArcheType archetype) {
			assertTrue(element == null || element.getArcheType() == archetype);
			
			switch (archetype) {
			case Module: visitModule(element); break;
			
			case Variable: visitVariable(element); break;
			case EnumMember: visitEnumMember(element); break;
			
			case Function: visitFunction(element); break;
			case Constructor: visitConstructor(element); break;
			
			case Struct: visitStruct(element); break;
			case Union: visitUnion(element); break;
			case Class: visitClass(element); break;
			case Interface: visitInterface(element); break;
			case Template: visitTemplate(element); break;
			case Enum: visitEnumType(element); break;
			
			case Mixin: visitMixin(element); break;
			
			case Alias: visitAlias(element); break;
			
			case Error: visitError(element); break;
			case Tuple: visitTuple(element); break;
			case TypeParameter: visitType(element); break;
			case Package: visitPackage(element); break;
			}
		}
		
		@SuppressWarnings("unused")
		protected void visitModule(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitVariable(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitEnumMember(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitFunction(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitConstructor(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitStruct(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitUnion(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitClass(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitInterface(INamedElement element) {
		}
		
		
		@SuppressWarnings("unused")
		protected void visitTemplate(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitEnumType(INamedElement node) {
		}
		
		@SuppressWarnings("unused")
		protected void visitMixin(INamedElement node) {
		}
		
		@SuppressWarnings("unused")
		protected void visitAlias(INamedElement node) {
		}
		
		@SuppressWarnings("unused")
		protected void visitPackage(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitTuple(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitType(INamedElement element) {
		}
		
		@SuppressWarnings("unused")
		protected void visitError(INamedElement element) {
		}
		
	}
	
	public static class ArchetypeCastVisitor extends ArchetypeSwitchVisitor {
		
		@Override
		protected void visitModule(INamedElement element) {
			doVisit((Module) element);
		}
		@SuppressWarnings("unused") public void doVisit(Module element) { } 
		
		@Override
		protected void visitVariable(INamedElement element) {
		}
		
		@Override
		protected void visitEnumMember(INamedElement element) {
		}
		
		
		@Override
		protected void visitFunction(INamedElement element) {
			doVisit((DefinitionFunction) element);
		}
		@SuppressWarnings("unused") public void doVisit(DefinitionFunction element) { }

		
		@Override
		protected void visitConstructor(INamedElement element) {
			doVisit((DefinitionConstructor) element);
		}
		@SuppressWarnings("unused") public void doVisit(DefinitionConstructor element) { }
		
		
		@Override
		protected void visitStruct(INamedElement element) {
		}
		
		@Override
		protected void visitUnion(INamedElement element) {
		}
		
		@Override
		protected void visitClass(INamedElement element) {
		}
		
		@Override
		protected void visitInterface(INamedElement element) {
		}
		
		
		
		@Override
		protected void visitTemplate(INamedElement element) {
		}
		
		
		@Override
		protected void visitEnumType(INamedElement element) {
			doVisit((DefinitionEnum) element);
		}
		@SuppressWarnings("unused") public void doVisit(DefinitionEnum element) { }
		
		
		@Override
		protected void visitMixin(INamedElement node) {
		}
		
		
		@Override
		protected void visitAlias(INamedElement element) {
		}
		
		
		@Override
		protected void visitPackage(INamedElement element) {
		}
		
		
		@Override
		protected void visitTuple(INamedElement element) {
		}
		
		
		@Override
		protected void visitType(INamedElement element) {
		}
		
		
		@Override
		protected void visitError(INamedElement element) {
		}
		
	}
}