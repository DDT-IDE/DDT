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
package melnorme.lang.tooling.engine.resolver;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;

public class TypeSemantics extends NonValueConcreteElementSemantics {
	
	protected final IScopeElement membersScope; // Can be null
	
	public TypeSemantics(IConcreteNamedElement typeElement, PickedElement<?> pickedElement, 
			IScopeElement membersScope) {
		super(typeElement, pickedElement);
		this.membersScope = membersScope;
	}
	
	protected final IConcreteNamedElement getTypeElement() {
		return elementRes.result;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		search.evaluateScope(getMembersScope());
	}
	
	public IScopeElement getMembersScope() {
		return membersScope;
	}
	
	public static ITypeNamedElement resolveTypeOfExpression(Resolvable resolvable, ISemanticContext parentContext) {
		if(resolvable instanceof Expression) {
			Expression expression = (Expression) resolvable;
			return expression.resolveTypeOfUnderlyingValue_nonNull(parentContext).concreteType;
		} else {
			final Reference reference = (Reference) resolvable;
			TypeReferenceResult result = ExpSemantics.resolveTypeOfExpressionReference(reference, parentContext);
			if(result == null) {
				return ExpSemantics.concreteTypeResult(ErrorElement.newNotFoundError(reference)).concreteType; 
			}
			return result.concreteType;
		}
	}
	
}