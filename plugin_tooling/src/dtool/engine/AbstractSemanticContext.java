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
package dtool.engine;


import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.engine.ResolutionEntry;
import melnorme.lang.tooling.util.EntryMap;

public abstract class AbstractSemanticContext implements ISemanticContext {
	
	/* ----------------- NodeSemantics ----------------- */
	
	protected final ResolutionsMap resolutionsMap = new ResolutionsMap();
	
	public static class ResolutionsMap extends EntryMap<IElementSemantics, ResolutionEntry<?>> {
		
		@Override
		protected ResolutionEntry<?> createEntry(IElementSemantics key) {
			return new ResolutionEntry<>();
		}
		
	}
	
	@Override
	public ResolutionEntry<?> findResolutionEntryForContainedElement(IElementSemantics elementSemantics) {
		/* FIXME: ensure elementSemantics belongs to this context */
		return resolutionsMap.getEntry(elementSemantics);
	}
	
	@Override
	public ISemanticContext findSemanticContext(ISemanticElement Element) {
		return this; // TODO subclass must reimplement, if appropriate
	}
	
}