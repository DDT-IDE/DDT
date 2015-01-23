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
package dtool.ast.expressions;


import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.IQualifierNode;

public abstract class Expression extends Resolvable implements IQualifierNode, IInitializer {
	
	/* -----------------  ----------------- */
	
	@Override
	public ExpSemantics getSemantics(ISemanticContext parentContext) {
		return (ExpSemantics) super.getSemantics(parentContext);
	}
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				return null; // TODO
			}
		};
	}
	
	@Override
	public TypeReferenceResult resolveTypeOfUnderlyingValue_nonNull(ISemanticContext context) {
		/* FIXME: need to review this: */
		return getSemantics(context).resolveTypeOfUnderlyingValue();
	}
	
	@Override
	public INamedElement resolveAsQualifiedRefRoot(ISemanticContext context) {
		return resolveTypeOfUnderlyingValue_nonNull(context).originalType;
	}
	
}