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

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.NullNamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

public class NotAValueErrorElement extends WrappedNamedElement implements INamedElement {
	
	public static final String ERROR_IS_NOT_A_VALUE = " (is not a value)";
	
	public NotAValueErrorElement(INamedElement wrappedElement, ISemanticElement parent) {
		super(wrappedElement, parent);
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return wrappedElement.isLanguageIntrinsic();
	}
	
	@Override
	public String getExtendedName() {
		return wrappedElement.getExtendedName() + ERROR_IS_NOT_A_VALUE;
	}
	
	@Override
	public IConcreteNamedElement resolveConcreteElement(ISemanticContext sr) {
		return wrappedElement.resolveConcreteElement(sr);
	}
	
	
	protected final INamedElementSemantics nodeSemantics = new NullNamedElementSemantics();
	
	@Override
	public INamedElementSemantics getSemantics() {
		return nodeSemantics;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		// Do nothing.
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		// Do nothing.
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return wrappedElement.getArcheType();
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return wrappedElement.resolveUnderlyingNode();
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		return wrappedElement.resolveDDoc();
	}
	
}