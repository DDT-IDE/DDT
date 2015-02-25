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
package melnorme.lang.tooling.ast;

import java.nio.file.Path;

import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;

/**
 * Language element is a data node, part of a node tree, that encompasses data about a language/program.
 * Usually parsed from string source, but not always.
 * 
 * Some properties:
 *   The data it holds is immutable after the node has been parsed or fully constructed. 
 *   (TODO: need to formalize this better) 
 */
public interface ILanguageElement {
	
	/** @return the parent element of this element. null if it is the top element of the tree. */
	CommonLanguageElement getLexicalParent();
	
	/** @return the fully qualified name of the module this element belongs to. 
	 * Can be null if element is not contained in a module. */
	public String getModuleFullName();
	
	
	/** 
	 * @return true if this is a builtin/predefined language element, in other words,
	 * not created from source code nor source elements.  
	 * (example: primitives such as int, void, or native types like arrays, pointer types).
	 * 
	 * This is a special case for in which the elements do not have a containing module, 
	 * and {@link #getSemanticContainerKey()}  is null. 
	 */
	public boolean isBuiltinElement();
	/**
	 * @return the path of the module from where this element was created.
	 * This is used to find which semantic context to use for the semantic element.
	 * Non-null in most cases, but it can be null.
	 */
	public Path getSemanticContainerKey();
	
	
	/**
	 * @return whether the construction/setup of this element is complete, and therefore the element
	 * is ready for semantic analysis. Only when this is true should semantic operations be performed.
	 * After an element is ready, it should be immutable, at least with regards to the data affecting semantics. 
	 */
	boolean isSemanticReady();
	
	/**
	 * Evaluate the node's contributions to its parent scope.
	 * Results should be placed in given scopeRes. 
	 */
	public void evaluateForScopeLookup(ScopeNameResolution scopeRes, boolean isSecondaryScope, 
			boolean publicImportsOnly);
	
	
	/**
	 * Create the semantics object for this element. 
	 * The semantics object will be bound to the given {@link ISemanticContext} context.
	 * Subclasses should reimplement when applicable.
	 * Note that only the semantic context should be calling this class.
	 */
	public IElementSemanticData createSemantics(PickedElement<?> pickedElement);
	/**
	 * Should perform exactly this: <code>parentContext.getSemanticsEntry(this)</code>
	 * @return the semantics object. Should be the same on every call. Non-null.
	 */
	public IElementSemanticData getSemantics(ISemanticContext parentContext);
	
	/**
	 * @return the context where this element is directly contained in.
	 */
	public ISemanticContext getElementSemanticContext(ISemanticContext parentContext);
	
}