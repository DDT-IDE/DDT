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
import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.symbols.INamedElement;

/* FIXME: BUG here refactor this*/
public abstract class NamedElementSemantics<ER extends ElementResolution<?>> extends ElementSemantics<ER> 
	implements INamedElementSemantics 
{
	
	protected final INamedElement element; 
	
	public NamedElementSemantics(INamedElement element, ISemanticContext context) {
		super(element, context);
		this.element = assertNotNull(element);
	}
	
}