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

import melnorme.lang.tooling.bundles.ISemanticContext;


public abstract class ElementSemantics<ER extends ElementResolution<?>> implements IElementSemantics {
	
	public ElementSemantics() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	protected ResolutionEntry<ER> findSemanticContainer(ISemanticContext context) {
		return (ResolutionEntry<ER>) context.findResolutionEntryForContainedElement(this);
	}
	
	protected final ER getElementResolution(ISemanticContext context) {
		return getOrCreateElementResolution(context);
	}
	
	protected ER getOrCreateElementResolution(ISemanticContext context) {
		ResolutionEntry<ER> resolutionContainer = findSemanticContainer(context);
		
		ER resolution = resolutionContainer.getResult();
		
		if(resolution == null) {
			// TODO: put temporary result
			resolution = createResolution(context);
			resolutionContainer.putResult(resolution);
		}
		return resolution;
	}
	
	protected abstract ER createResolution(ISemanticContext context);
	
}