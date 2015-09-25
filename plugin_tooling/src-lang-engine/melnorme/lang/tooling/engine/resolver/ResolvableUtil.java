/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.NotATypeErrorElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.ITypeNamedElement;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;

public class ResolvableUtil {

	public static INamedElement resolveReference(IReference reference, ISemanticContext context) {
		if(reference == null) {
			return null;
		}
		return reference.getSemantics(context).resolveTargetElement().result;
	}
	
	public static IConcreteNamedElement resolveConcreteElement(IReference reference, ISemanticContext parentContext) {
		if(reference == null) {
			return null;
		}
		INamedElement refTarget = reference.getSemantics(parentContext).resolveTargetElement_();
		
		return refTarget.getSemantics(parentContext).resolveConcreteElement().result;
	}
	
	/**
	 * @param reference non-null.
	 * @return non-null.
	 */
	public static ITypeNamedElement resolveTargetType(IReference reference, ISemanticContext parentContext) {
		assertNotNull(reference);
		
		INamedElement refTarget = reference.getSemantics(parentContext).resolveTargetElement_();
		return resolveTargeType(reference, refTarget, parentContext);
	}
	
	/**
	 * @param reference non-null.
	 * @param originalType non-null.
	 * @return non-null.
	 */
	public static ITypeNamedElement resolveTargeType(IReference reference, INamedElement originalType,
			ISemanticContext parentContext) {
		assertNotNull(reference);
		assertNotNull(originalType);
		
		IConcreteNamedElement concreteResult = originalType.resolveConcreteElement(parentContext);
		
		if(concreteResult instanceof ITypeNamedElement) {
			return (ITypeNamedElement) concreteResult;
		} else {
			return new NotATypeErrorElement(reference, concreteResult);
		}
	}
	
	protected static TypeReferenceResult concreteTypeResult(IReference reference, INamedElement originalType,
			ISemanticContext parentContext) {
		if(originalType == null) {
			return new TypeReferenceResult(ErrorElement.newNotFoundError(reference));
		}
		
		ITypeNamedElement concreteType = resolveTargeType(reference, originalType, parentContext);
		return new TypeReferenceResult(originalType, concreteType);
	}
	
	/**
	 * Resolve a reference that should point to a type element.
	 */
	public static TypeReferenceResult resolveTypeReference2(IReference reference, ISemanticContext context) {
		if(reference == null) {
			return null;
		}
		INamedElement originalType = reference.getSemantics(context).resolveTargetElement_();
		return concreteTypeResult(reference, originalType, context);
	}
	
	/* -----------------  ----------------- */

	public static ITypeNamedElement resolveTypeOfExpression(Resolvable resolvable, ISemanticContext parentContext) {
		if(resolvable instanceof Expression) {
			Expression expression = (Expression) resolvable;
			return expression.resolveTypeOfUnderlyingValue_nonNull(parentContext).concreteType;
		} else {
			final IReference reference = (IReference) resolvable;
			TypeReferenceResult result = resolveTypeOfExpressionReference(reference, parentContext);
			if(result == null) {
				return ErrorElement.newNotFoundError(reference); 
			}
			return result.concreteType;
		}
	}

	/**
	 * Resolve a reference that should point to a var element 
	 * (or a similar element that has an expression value).
	 */
	public static TypeReferenceResult resolveTypeOfExpressionReference(IReference reference, 
			ISemanticContext parentContext) {
		if(reference == null) {
			return null;
		}
		INamedElement expElement = reference.getSemantics(parentContext).resolveTargetElement_();
		INamedElement type = expElement.getSemantics(parentContext).getTypeForValueContext();
		return concreteTypeResult(reference, type, parentContext);
	}
	
}