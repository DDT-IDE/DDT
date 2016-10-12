/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import dtool.ast.references.RefTemplateInstance;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.NodeVector;

/**
 * Interface for elements that can have template parameters.
 */
public interface ITemplatableElement extends ILanguageElement, IASTNode {
	
	boolean isTemplated();
	
	/** @return non-null if {@link #isTemplated()} == true. */
	NodeVector<ITemplateParameter> getTemplateParameters();
	
	// XXX: the implementations of this method could probably be improved, coded in a more elegant way.
	DefUnit cloneTemplateElement(RefTemplateInstance templateRef);
	
}