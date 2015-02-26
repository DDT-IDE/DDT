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
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.resolver.VarSemantics;
import melnorme.lang.tooling.engine.scoping.NamedElementsScope;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

public interface CommonLanguageIntrinsics {
	
	public abstract class IntrinsicNamedElement extends AbstractNamedElement {
		
		public IntrinsicNamedElement(String name, ElementDoc doc) {
			super(name, null, null, doc);
		}
		
	}

	public abstract class BuiltinTypeElement extends IntrinsicNamedElement implements IConcreteNamedElement {
		
		protected NamedElementsScope membersScope;
		
		public BuiltinTypeElement(String name, ElementDoc doc) {
			super(name, doc);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Struct;
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
			setElementReady();
		}
		
		public abstract void doCreateMembers(IntrinsicNamedElement... members);
		
		
		@Override
		public TypeSemantics getSemantics(ISemanticContext parentContext) {
			return (TypeSemantics) super.getSemantics(parentContext);
		}
		@Override
		public TypeSemantics doCreateSemantics(PickedElement<?> pickedElement) {
			return new TypeSemantics(this, pickedElement, membersScope);
		}
		
	}
	
	public abstract class AbstractIntrinsicProperty extends IntrinsicNamedElement implements IConcreteNamedElement {
		
		public AbstractIntrinsicProperty(String name, ElementDoc doc) {
			super(name, doc);
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
				public INamedElement getTypeForValueContext_do() {
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
			setElementReady();
		}
		
		@Override
		protected void doSetElementSemanticReady() {
//			 assertTrue(type.isSemanticReady()); /* FIXME: */
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
			setElementReady();
		}
		
		@Override
		protected void doSetElementSemanticReady() {
//			 assertTrue(typeRef.isSemanticReady()); /* FIXME: */
		}
		
		@Override
		protected INamedElement resolveType(ISemanticContext context) {
			return typeRef.getSemantics(context).resolveTargetElement().result;
		}
		
	}
	
}