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

import java.util.ArrayList;
import java.util.Collection;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.ElementSemantics;
import melnorme.lang.tooling.engine.NotFoundErrorElement;
import melnorme.lang.tooling.symbols.INamedElement;

public abstract class ResolvableSemantics extends ElementSemantics<ResolvableResult> 
	implements IResolvableSemantics 
{
	
	private final IResolvable resolvable;
	
	public ResolvableSemantics(IResolvable resolvable) {
		this.resolvable = resolvable;
	}
	
	@Override
	public final ResolvableResult resolveTargetElement(ISemanticContext context) {
		return getElementResolution(context);
	}
	
	@Override
	protected ResolvableResult createResolution(ISemanticContext context) {
		INamedElement result = null;
		Collection<INamedElement> namedElems = findTargetDefElements(context, true);
		if(namedElems != null && !namedElems.isEmpty()) {
			result = namedElems.iterator().next();
		}
		
		if(result == null) {
			result = new NotFoundErrorElement(resolvable);
		}
		
		return new ResolvableResult(result);
	}
	
	@Override
	public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
		Collection<INamedElement> resolvedElements = this.findTargetDefElements(mr, false);
		
		return resolveTypeOfUnderlyingValue(mr, resolvedElements); 
	}
	
	public static Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr, 
		Collection<INamedElement> resolvedElements) {
		ArrayList<INamedElement> resolvedTypeForValueContext = new ArrayList<>();
		for (INamedElement defElement : resolvedElements) {
			INamedElement resolveTypeForValueContext = defElement.resolveTypeForValueContext(mr);
			if(resolvedTypeForValueContext != null) {
				resolvedTypeForValueContext.add(resolveTypeForValueContext);
			}
		}
		return resolvedTypeForValueContext;
	}
	
	
	protected Collection<INamedElement> resolveToInvalidValue() {
		return null; // TODO
	}
	
	public abstract static class TypeReferenceSemantics extends ResolvableSemantics {
		
		public TypeReferenceSemantics(IResolvable resolvable) {
			super(resolvable);
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
			return resolveToInvalidValue();
		}
		
	}
	
	public abstract static class ExpSemantics extends ResolvableSemantics {
		
		public ExpSemantics(IResolvable resolvable) {
			super(resolvable);
		}
		
		@Override
		public abstract Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly);
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
			return findTargetDefElements(mr, true); // TODO need to review this
		}
		
	}
	
	/* ----------------- module lookup helpers ----------------- */
	
	public static INamedElement findModuleUnchecked(ISemanticContext mr, ModuleFullName moduleName) {
		try {
			return mr.findModule(moduleName);
		} catch (ModuleSourceException pse) {
			/* TODO: add error to SemanticResolution / semantic operation. */
			return null;
		}
	}
	
	public static INamedElement findModuleUnchecked(ISemanticContext mr, String moduleFullName) {
		return ResolvableSemantics.findModuleUnchecked(mr, new ModuleFullName(moduleFullName));
	}
	
}