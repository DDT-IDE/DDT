/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

/**
 * An overloaded named element aggregates several elements with the same name in the same scope.
 * This can be a semantic error or not, depending on the composition of the elements.
 * For example it is usually valid for functions elements with the same name to exists.
 */
public class OverloadedNamedElement extends AbstractNamedElement implements IConcreteNamedElement {
	
	protected final HashSet<INamedElement> elements;
	protected final EArcheType archeType;
	protected final INamedElement firstElement;
	
	public OverloadedNamedElement(INamedElement firstElement, ILanguageElement parent) {
		super(firstElement.getName(), parent);
		this.firstElement = firstElement;
		this.elements = new HashSet<>();
		elements.add(firstElement);
		this.archeType = EArcheType.Error;
	}
	
	@Override
	public Path getModulePath() {
		return parent.getModulePath();
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return firstElement.getNameInRegularNamespace();
	}
	
	@Override
	public String getFullyQualifiedName() {
		return firstElement.getFullyQualifiedName();
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return firstElement.getModuleFullyQualifiedName();
	}
	
	@Override
	public ModuleFullName getModuleFullName() {
		return firstElement.getModuleFullName();
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return firstElement.getParentNamespace();
	}
	
	@Override
	public EArcheType getArcheType() {
		return archeType;
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return null;
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return null;
	}
	
	public Collection<INamedElement> getOverloadedElements() {
		return elements;
	}
	
	public void addElement(INamedElement newElement) {
		assertTrue(newElement.getNameInRegularNamespace().equals(firstElement.getNameInRegularNamespace()));
		// FIXME: re enable this test. Will require import selections to be aliases
		//assertAreEqual(newElement.getModulePath(), firstElement.getModulePath());
		
		elements.add(newElement);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NamedElementSemantics(this, pickedElement) {
			
			protected final NotAValueErrorElement notAValueError = new NotAValueErrorElement(this.element);
			
			@Override
			protected IConcreteNamedElement doResolveConcreteElement() {
				return OverloadedNamedElement.this;
			}
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				// Do nothing.
			}
			
			@Override
			public INamedElement resolveTypeForValueContext() {
				return notAValueError;
			}
		};
	}
	
	@Override
	public String toString() {
		return "NameConflict["+getName()+"]";
	}
	
}