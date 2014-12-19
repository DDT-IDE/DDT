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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertAreEqual;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import dtool.ast.definitions.EArcheType;

/**
 * An overloaded named element aggregates several elements with the same name in the same scope.
 * This can be a semantic error or not, depending on the composition of the elements.
 * For example it is usually valid for functions elements with the same name to exists.
 */
public abstract class OverloadedNamedElement extends AbstractNamedElement {
	
	protected final ArrayList2<INamedElement> elements;
	protected final EArcheType archeType;
	protected final INamedElement firstElement;
	
	public OverloadedNamedElement(ArrayList2<INamedElement> elements, ILanguageElement parent) {
		super(getCommonName(elements), parent);
		this.elements = elements;
		this.firstElement = elements.get(0);
		
		EArcheType archeType = null;
		for (INamedElement namedElement : elements) {
			if(archeType == null || archeType == namedElement.getArcheType()) {
				archeType = namedElement.getArcheType();
			} else {
				archeType = EArcheType.Error;
			}
			
			//assertAreEqual(namedElement.getParent(), firstElement.getParent());
			
			assertAreEqual(namedElement.getParentNamespace(), firstElement.getParentNamespace());
			assertAreEqual(namedElement.getModuleFullyQualifiedName(), firstElement.getModuleFullyQualifiedName());
			assertAreEqual(namedElement.getModuleFullName(), firstElement.getModuleFullName());
			assertAreEqual(namedElement.getFullyQualifiedName(), firstElement.getFullyQualifiedName());
			assertAreEqual(namedElement.getModulePath(), firstElement.getModulePath());
		}
		
		/* FIXME: */
//		this.archeType = archeType;
		this.archeType = EArcheType.Error;
	}
	
	protected static String getCommonName(ArrayList2<INamedElement> elements) {
		assertTrue(elements != null && elements.size() > 0);
		return elements.get(0).getName();
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
	
	public ArrayList2<INamedElement> getOverloadedElements() {
		return elements;
	}
	
}