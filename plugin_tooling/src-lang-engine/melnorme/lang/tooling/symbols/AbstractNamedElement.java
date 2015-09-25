/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.symbols;

import melnorme.lang.tooling.ast.NonSourceElement;
import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;

public abstract class AbstractNamedElement extends NonSourceElement implements INamedElement {
	
	protected final String name;
	protected final ElementDoc doc;
	
	public AbstractNamedElement(String name, CommonLanguageElement lexicalParent, ILanguageElement ownerElement) {
		this(name, lexicalParent, ownerElement, null);
	}
	
	public AbstractNamedElement(String name, CommonLanguageElement lexicalParent, ILanguageElement ownerElement,
			ElementDoc doc) {
		super(ownerElement, lexicalParent);
		this.name = name;
		this.doc = doc;
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return doc;
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return NodeElementUtil.getOuterNamedElement(this);
	}
	
	@Override
	public final String getName() {
		return name;
	}
	
	@Override
	public String getExtendedName() {
		return name;
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return getName();
	}
	
	@Override
	public String getFullyQualifiedName() {
		return CommonLanguageElement.getFullyQualifiedName(this);
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return null;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public INamedElementSemanticData getSemantics(ISemanticContext parentContext) {
		return (INamedElementSemanticData) super.getSemantics(parentContext);
	}
	@Override
	protected abstract INamedElementSemanticData doCreateSemantics(PickedElement<?> pickedElement);
	
	@Override
	public final IConcreteNamedElement resolveConcreteElement(ISemanticContext context) {
		return getSemantics(context).resolveConcreteElement().result;
	}
	
	@Override
	public final void resolveSearchInMembersScope(CommonScopeLookup search) {
		getSemantics(search.context).resolveSearchInMembersScope(search);
	}
	
}