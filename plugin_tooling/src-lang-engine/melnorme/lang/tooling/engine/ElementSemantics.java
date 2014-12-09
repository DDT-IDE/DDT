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
import melnorme.lang.tooling.context.ISemanticContext;


public abstract class ElementSemantics<ER> implements IElementSemantics {
	
	protected final ISemanticContext context;
	
	public ElementSemantics(PickedElement<?> pickedElement) {
		this.context = pickedElement.context;
	}
	
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
	 * Create and return the main resolution object for this semantics object. Non-null.
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