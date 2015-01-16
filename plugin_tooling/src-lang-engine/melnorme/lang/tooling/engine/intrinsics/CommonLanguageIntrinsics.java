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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.resolver.VarSemantics;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

public interface CommonLanguageIntrinsics {

	public abstract class IntrinsicTypeDefUnit extends IntrinsicNamedElement implements IConcreteNamedElement {
		
		protected NamedElementsScope membersScope;
		
		public IntrinsicTypeDefUnit(String name, ElementDoc doc) {
			super(name, doc, false);
		}
		
		public NamedElementsScope getMembersScope() {
			return membersScope;
		}
		
		@Override
		public String toString() {
			return "intrinsic_type#" + getName();
		}
		
		public final void createMembers(IntrinsicNamedElement... members) {
			doCreateMembers(members);
			setCompleted();
		}
		
		public abstract void doCreateMembers(IntrinsicNamedElement... members);
		
		@Override
		public TypeSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new TypeSemantics(this, pickedElement, membersScope);
		}
		
	}
	
	public abstract class AbstractIntrinsicProperty extends IntrinsicNamedElement implements IConcreteNamedElement {
		
		public AbstractIntrinsicProperty(String name, ElementDoc doc) {
			super(name, doc, true);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		@Override
		public String toString() {
			return "intrinsic_property#" + getName();
		}
		
		protected abstract INamedElement resolveType(ISemanticContext context);
		
		@Override
		public VarSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new VarSemantics(this, pickedElement) {
				
				@Override
				public INamedElement resolveTypeForValueContext() {
					return resolveType(context);
				};
				
				@Override
				protected IReference getTypeReference() {
					throw assertFail();
				};
				
			};
		}
		
	}
	
	public class IntrinsicProperty extends AbstractIntrinsicProperty {
		
		public final INamedElement type;
		
		public IntrinsicProperty(String name, INamedElement type, ElementDoc doc) {
			super(name, doc);
			this.type = assertNotNull(type);
		}
		
		@Override
		protected void doSetCompleted() {
			 assertTrue(isCompleted(type));
		}
		
		@Override
		protected INamedElement resolveType(ISemanticContext context) {
			return type;
		}
		
	}
	
	public interface IPrimitiveDefUnit { }
	
	/* ----------------- refs ----------------- */
	
	public class IntrinsicProperty2 extends AbstractIntrinsicProperty {
		
		public final IReference typeRef;
		
		public IntrinsicProperty2(String name, IReference typeRef, ElementDoc doc) {
			super(name, doc);
			this.typeRef = assertNotNull(typeRef);
		}
		
		@Override
		protected void doSetCompleted() {
			 assertTrue(isCompleted(typeRef));
		}
		
		@Override
		protected INamedElement resolveType(ISemanticContext context) {
			return typeRef.getSemantics(context).resolveTargetElement().result;
		}
		
	}
	
}