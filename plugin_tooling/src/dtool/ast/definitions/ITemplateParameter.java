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
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import dtool.ast.expressions.Resolvable;


public interface ITemplateParameter extends IASTNode {
	
	/** 
	 * Create template argument element for given argument 
	 * @param argument non-null.
	 * @param tplRefContext non-null.
	 * 
	 * @return the created tamplate argument, or null if the given argument is not applicable to the parameter.
	 */
	INamedElementNode createTemplateArgument(Resolvable argument, ISemanticContext tplRefContext);
	
	public static class NotInstantiatedErrorElement extends ErrorElement {
		
		public static final String NAME = ERROR_PREFIX + "NotInstantiated";
		
		public NotInstantiatedErrorElement(ILanguageElement ownerElement, ElementDoc doc) {
			super(NAME, ownerElement, doc);
		}
		
	}

}