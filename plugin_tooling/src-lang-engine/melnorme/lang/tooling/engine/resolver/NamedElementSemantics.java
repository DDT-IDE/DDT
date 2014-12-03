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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class NamedElementSemantics extends ElementSemantics<ConcreteElementResult> 
	implements INamedElementSemantics 
{
	
	protected final INamedElement element; 
	
	public NamedElementSemantics(INamedElement element, ISemanticContext context) {
		super(context);
		this.element = assertNotNull(element);
	}
	
	@Override
	public ConcreteElementResult resolveConcreteElement() {
		return getElementResolution();
	}
	
	@Override
	protected final ConcreteElementResult createResolution(ISemanticContext context) {
		return new ConcreteElementResult(doResolveConcreteElement(context));
	}
	
	protected abstract IConcreteNamedElement doResolveConcreteElement(ISemanticContext context);
	
}