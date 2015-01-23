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
package dtool.engine.analysis;

import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.ErrorElement.SyntaxErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.IReference;
import melnorme.lang.tooling.engine.resolver.VarSemantics;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.expressions.IInitializer;

public class CommonDefVarSemantics extends VarSemantics {
	
	protected final IVarDefinitionLike varDef;
	
	public CommonDefVarSemantics(IVarDefinitionLike varDef, PickedElement<?> pickedElement) {
		super(varDef, pickedElement);
		this.varDef = varDef;
	}
	
	@Override
	public INamedElement getTypeForValueContext_do() {
		if(getTypeReference() != null) {
			return super.getTypeForValueContext_do();
		}
		
		IInitializer initializer = varDef.getDeclaredInitializer();
		if(initializer != null) {
			return initializer.resolveTypeOfUnderlyingValue_nonNull(context).originalType;
		}
		
		return new SyntaxErrorElement(varDef, ErrorElement.quoteDoc("Missing initializer or type."));
	}
	
	@Override
	protected IReference getTypeReference() {
		return varDef.getDeclaredType();
	}
	
}