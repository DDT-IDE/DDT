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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.resolver.DefElementCommon;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.CollectionUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.IResolvable;
import dtool.resolver.ReferenceResolver;

public interface CommonLanguageIntrinsics {

	public abstract class IntrinsicTypeDefUnit extends IntrinsicDefUnit {
		
		protected InstrinsicsScope membersScope;
		
		public IntrinsicTypeDefUnit(String name, Ddoc doc) {
			super(name, doc);
		}
		
		public InstrinsicsScope getMembersScope() {
			return membersScope;
		}
		
		public abstract void createMembers(IntrinsicDefUnit... members);
		
		@Override
		public final INamedElement resolveTypeForValueContext(IModuleResolver mr) {
			assertNotNull(membersScope);
			return DefElementCommon.returnError_ElementIsNotAValue(this);
		}
		
		@Override
		public final void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			assertNotNull(membersScope);
			membersScope.resolveSearchInScope(search);
		}
		
	}
	
	public abstract class AbstractIntrinsicProperty extends IntrinsicDefUnit {
		
		public AbstractIntrinsicProperty(String name, Ddoc doc) {
			super(name, doc);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		protected abstract INamedElement resolveType(IModuleResolver mr);
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			resolveType(search.getModuleResolver()).resolveSearchInMembersScope(search);
		}
		
		@Override
		public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
			return resolveType(mr);
		}
		
	}
	
	public class IntrinsicProperty extends AbstractIntrinsicProperty {
		
		public final INamedElement type;
		
		public IntrinsicProperty(String name, INamedElement type, Ddoc ddoc) {
			super(name, ddoc);
			this.type = assertNotNull(type);
		}
		
		@Override
		protected INamedElement resolveType(IModuleResolver mr) {
			return type;
		}
		
	}
	
	public interface IPrimitiveDefUnit { }
	
	/* ----------------- refs ----------------- */
	
	public class IntrinsicProperty2 extends AbstractIntrinsicProperty {
		
		public final IResolvable typeRef;
		
		public IntrinsicProperty2(String name, IResolvable typeRef, Ddoc ddoc) {
			super(name, ddoc);
			this.typeRef = assertNotNull(typeRef);
		}
		
		@Override
		protected INamedElement resolveType(IModuleResolver mr) {
			return CollectionUtil.getFirstElementOrNull(typeRef.findTargetDefElements(mr, true));
		}
		
	}
	
	public static class FullyQualifiedReference implements IResolvable {
		
		public final String moduleFullName;
		public final String elementName;
		
		public FullyQualifiedReference(String moduleFullName, String elementName) {
			this.moduleFullName = moduleFullName;
			this.elementName = elementName;
		}
		
		public final INamedElement findTargetDefElement(IModuleResolver moduleResolver) {
			Collection<INamedElement> namedElems = findTargetDefElements(moduleResolver, true);
			return CollectionUtil.getFirstElementOrNull(namedElems);
		}
		
		@Override
		public Collection<INamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
			INamedElement module = ReferenceResolver.findModuleUnchecked(mr, moduleFullName);
			if(module == null) 
				return null;
			
			DefUnitSearch search = new DefUnitSearch(elementName, null, -1, findFirstOnly, mr);
			module.resolveSearchInMembersScope(search);
			return search.getMatchedElements();
		}
		
	}
	
}