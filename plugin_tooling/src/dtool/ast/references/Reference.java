/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.references;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.OverloadedNamedElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.ReferenceSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Collection2;
import dtool.ast.expressions.Resolvable;

/**
 * Common class for entity references. (might not be necessary anymore)
 */
public abstract class Reference extends Resolvable implements IReference {
	
	@Override
	public ReferenceSemantics getSemantics(ISemanticContext parentContext) {
		return (ReferenceSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected abstract ReferenceSemantics doCreateSemantics(PickedElement<?> pickedElement);
	
	public final INamedElement resolveTargetElement(ISemanticContext context) {
		return getSemantics(context).resolveTargetElement().result;
	}
	
	@Override
	public INamedElement resolveAsQualifiedRefRoot(ISemanticContext context) {
		return resolveTargetElement(context);
	}
	
	
	public static Collection2<INamedElement> resolveResultToCollection(INamedElement result) {
		if(result instanceof OverloadedNamedElement) {
			OverloadedNamedElement overloadedNamedElement = (OverloadedNamedElement) result;
			return overloadedNamedElement.getOverloadedElements();
		} else {
			return new ArrayList2<>(result);
		}
	}
	
}