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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Map;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;

/**
 * A class responsible for doing semantic analysis.
 * Each instance is bound to a specific {@link ILanguageElement}.
 * 
 * This class uses the {@link #hashCode()} and {@link #equals()} of Object, such that each instance of 
 * this class can be seperately inserted in a {@link Map}. 
 */
public abstract class ElementSemantics<ER> {
	
	protected final ISemanticContext context;
	
	public ElementSemantics(PickedElement<?> pickedElement) {
		this.context = pickedElement.context;
	}
	
	/* ----------------- Note #equals and #hashCode contract ----------------- */
	
	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	/* ----------------- ----------------- */
	
	private ER resolution;
	
	protected final ER getElementResolution() {
		return getOrCreateElementResolution();
	}
	
	protected ER getOrCreateElementResolution() {
		if(resolution == null) {
			// TODO: optimization: put information about a partial result that can be resolved without a context
			// in the ILanguageElement itself. 
			// This way, such information can be re-used a new resolution is created in a different context.
			
			// TODO: loop detection during resolution
			
			// FIXME: BUG here, need to handle concurrent access properly. 
			// We can't just wrap this in a synchronized block, because that would cause deadlock in loop scenarios.
			resolution = assertNotNull(createResolution());
		}
		return resolution;
	}
	
	/** 
	 * Create and return the resolution object for this semantics analysis. Non-null.
	 * The resulting object must also be immutable!
	 */
	protected abstract ER createResolution();
	
	/* ----------------- Utility for classes with no semantics to resolve: ----------------- */
	
	public static class NullElementSemantics extends ElementSemantics<Void> {
		
		public NullElementSemantics(PickedElement<?> pickedElement) {
			super(pickedElement);
		}
		
		@Override
		protected Void createResolution() {
			return null;
		}
	}
	
}