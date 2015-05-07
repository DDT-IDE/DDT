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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup.ScopeNameResolution;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import dtool.ast.definitions.EArcheType;


public abstract class CommonLanguageElement implements ILanguageElement {
	
	/** Custom field to store various kinds of data */
	private NodeData data = NodeData.CREATED_STATUS; 
	/** AST node parent, null if the node is the tree root. */
	protected CommonLanguageElement parent = null;
	
	public CommonLanguageElement() {
	}
	
	/* ------------------------  Node data/status ------------------------  */
	
	@Override
	public final boolean isSemanticReady() {
		return getData().isSemanticReadyStatus();
	}
	
	public final NodeData getData() {
		return assertNotNull(data);
	}
	
	/** Set the data of this node. Cannot be null. Cannot set data twice without explicitly resetting */
	public final void setData(NodeData data) {
		assertNotNull(data);
		assertTrue(!isSemanticReady()); // can only change data if node has not been made ready
		this.data = data;
	}
	
	/** Removes the data of this node. Can only remove data if node is in parsed status. 
	 * @return the previous data. */
	public NodeData resetData() {
		NodeData oldData = getData();
		setData(NodeData.CREATED_STATUS);
		return oldData;
	}
	
	public void setElementReady() {
		assertTrue(isSemanticReady() == false);
		doSetElementSemanticReady();
		getData().setSemanticReady(this);
	}
	
	protected abstract void doSetElementSemanticReady();
	
	/* ----------------- lexical parent ----------------- */
	
	@Override
	public CommonLanguageElement getLexicalParent() {
		return parent;
	}	
	
	/** Set the parent of this element. Cannot set parent twice without explicitly detaching. */
	@Override
	public final void setParent(CommonLanguageElement newParent) {
		assertTrue(this.parent == null);
		if(isSemanticReady()) {
			assertTrue(newParent.isSemanticReady());
		}
		this.parent = newParent;
		checkNewParent();
	}
	
	protected void checkNewParent() {
		// Default implementation: do nothing
		// subclasses can implement to check a contract relating to their parent 
		// (usually, to ensure the parent is of a certain class)
		getParent_Concrete();
	}
	
	/** Same as {@link #getLexicalParent()}, but allows classes to cast to a more specific parent. */
	// Is this extra method really needed instead of just defining getParent as non-final?
	// Would the casts make a different in performance?
	protected ILanguageElement getParent_Concrete() {
		return getLexicalParent();
	}
	
	public void detachFromParent_disposeParent() {
		assertNotNull(parent);
		parent.data = null; // Dispose parent, parent becomes an invalid node.
		parent = null;
	}
	
	/* ----------------- owner element ----------------- */
	
	public abstract ILanguageElement getOwnerElement();
	
	@Override
	public boolean isBuiltinElement() {
		return getOwnerElement() == null ? true : getOwnerElement().isBuiltinElement();
	}
	
	/* ----------------- isSemanticReady utils ----------------- */
	
	protected static <T extends ILanguageElement> T checkIsSemanticReady(T element) {
		assertTrue(element == null || element.isSemanticReady());
		return element;
	}
	
	public static <T extends Iterable<? extends ILanguageElement>> T checkAreSemanticReady(T elements, 
			boolean readyPackageNamespace) {
		
		for(ILanguageElement element : elements) {
			if(readyPackageNamespace && element instanceof PackageNamespace) {
				// PackageNamespace cannot be setCompleted early, so set it completed now
				((PackageNamespace) element).setElementReady();
			}
			checkIsSemanticReady(element);
		}
		return elements;
	}
	
	protected static <T extends Iterable<? extends ILanguageElement>> T checkAllSemanticReady(T elements) {
		return checkAreSemanticReady(elements, false);
	}
	
	/* ----------------- INamedElement utils ----------------- */
	
	@Override
	public String getModuleFullName() {
		INamedElement module = getContainingModuleNamespace();
		return module == null ? null : module.getFullyQualifiedName();
	}
	
	public INamedElement getContainingModuleNamespace() {
		INamedElement namedElement = NodeElementUtil.getNearestNamedElement(this);
		while(namedElement != null) {
			if(namedElement.getArcheType() == EArcheType.Module) {
				return namedElement;
			}
			namedElement = NodeElementUtil.getOuterNamedElement(namedElement);
		}
		return null;
	}
	
	public static String getFullyQualifiedName(INamedElement namedElement) {
		INamedElement parentNamespace = namedElement.getParentNamespace();
		if(parentNamespace == null) {
			return namedElement.getName();
		} else {
			return parentNamespace.getFullyQualifiedName() + "." + namedElement.getName();
		}
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public IElementSemanticData getSemantics(ISemanticContext parentContext) {
		assertTrue(isSemanticReady());
		return doGetSemantics(parentContext);
	}
	
	public IElementSemanticData doGetSemantics(ISemanticContext parentContext) {
		ISemanticContext context = getElementSemanticContext(parentContext);
		return context.getSemanticsEntry(this);
	}
	
	@Override
	public ISemanticContext getElementSemanticContext(ISemanticContext parentContext) {
		assertNotNull(parentContext);
		return parentContext.getContainingSemanticContext(this);
	}
	
	@Override
	public Path getSemanticContainerKey() {
		return getOwnerElement() == null ? null : getOwnerElement().getSemanticContainerKey();
	}
	
	@Override
	public final IElementSemanticData createSemantics(PickedElement<?> pickedElement) {
		assertTrue(pickedElement.element == this); // Note this precondition!
		return doCreateSemantics(pickedElement);
	}
	
	@SuppressWarnings("unused")
	protected IElementSemanticData doCreateSemantics(PickedElement<?> pickedElement) {
		throw assertFail(); // Not valid unless re-implemented.
	}
	
	/* ----------------- name lookup ----------------- */
	
	/**
	 * Perform a name lookup starting in this node.
	 * The exact mechanism in which the name lookup will be performed will depend on the node, 
	 * but the most common (and default) scenario is to perform a lexical lookup.
	 */
	public final void performNameLookup(CommonScopeLookup lookup) {
		assertTrue(isSemanticReady());
		
		doPerformNameLookup(lookup);
	}
	
	protected void doPerformNameLookup(CommonScopeLookup lookup) {
		assertTrue(lookup.isSequentialLookup());
		assertTrue(lookup.refOffset >= 0);
		
		lookup.evaluateScope(ASTNode.getPrimitivesScope());
		if(lookup.isFinished())
			return;
		
		doPerformNameLookupInLexicalScope(lookup);
	}
	
	protected final void doPerformNameLookupInLexicalScope(CommonScopeLookup lookup) {
		if(this instanceof IScopeElement) {
			IScopeElement scope = (IScopeElement) this;
			lookup.evaluateScope(scope);
		}
		
		if(lookup.isFinished())
			return;
		
		CommonLanguageElement parent = getLexicalParent();
		if(parent != null) {
			parent.doPerformNameLookupInLexicalScope(lookup);
		}
	}
	
	@Override
	public void evaluateForScopeLookup(ScopeNameResolution scopeRes, boolean isSecondaryScope, 
			boolean publicImportsOnly) {
		// Default: do nothing.
	}
	
}