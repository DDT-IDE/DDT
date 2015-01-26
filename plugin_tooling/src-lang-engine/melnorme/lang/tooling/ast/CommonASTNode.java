/*******************************************************************************
 * Copyright (c) 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.NodeData.CompleteNodeVisitor;
import melnorme.lang.tooling.ast.NodeData.CreatedStatusNodeData;
import melnorme.lang.tooling.ast.NodeData.ParsedNodeData;
import melnorme.lang.tooling.ast.util.ASTChildrenCollector;
import melnorme.lang.tooling.ast.util.ASTDirectChildrenVisitor;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.utilbox.collections.ArrayView;

public abstract class CommonASTNode extends SourceElement implements IASTNode {
	
	public static final ASTNode[] NO_ELEMENTS = new ASTNode[0]; 
	
	/** AST node parent, null if the node is the tree root. */
	protected ASTNode parent = null;
	/** Custom field to store various kinds of data */
	private NodeData data = NodeData.CREATED_STATUS; 
	
	
	public CommonASTNode() {
	}
	
	@Override
	public final ASTNode asNode() {
		return (ASTNode) this;
	}
	
	/* ------------------------  Parent and children visitor ------------------------ */
	
	/* FIXME: need to review this method, subside with getLexicalParent */
	public final ASTNode getParent() {
		return parent;
	}
	
	@Override
	public ILanguageElement getLexicalParent() {
		return parent;
	}
	
	@Override
	public ILanguageElement getOwnerElement() {
		return parent;
	}
	
	@Override
	public boolean isCompleted() {
		return isPostParseStatus();
	}
	
	/** Set the parent of this node. Cannot be null. Cannot set parent twice without explicitly detaching. */
	@Override
	public final void setParent(ASTNode parent) {
		assertTrue(parent != null);
		assertTrue(this.parent == null);
		this.parent = parent;
		checkNewParent();
	}
	
	protected void checkNewParent() {
		// Default implementation: do nothing
		// subclasses can implement to check a contract relating to their parent 
		// (usually, to ensure the parent is of a certain class)
		getParent_Concrete();
	}
	
	/** Same as {@link #getParent()}, but allows classes to cast to a more specific parent. */
	// Is this extra method really needed instead of just defining getParent as non-final?
	// Would the casts make a different in performance?
	protected ASTNode getParent_Concrete() {
		return getParent();
	}
	
	public void detachFromParent() {
		assertNotNull(this.parent);
		CommonASTNode parent_ = (CommonASTNode) this.parent;
		parent_.data = null; // Note, parent becomes an invalid node after this.
		this.parent = null;
	}
	
	/** Accept a visitor into this node. */
	@Override
	public final void accept(IASTVisitor visitor) {
		assertNotNull(visitor);
		
		// begin with the generic pre-visit
		if(visitor.preVisit(asNode())) {
			visitChildren(visitor);
		}
		// end with the generic post-visit
		visitor.postVisit(asNode());
	}
	
	public abstract void visitChildren(IASTVisitor visitor);
	
	public void visitDirectChildren(ASTDirectChildrenVisitor directChildrenVisitor) {
		accept(directChildrenVisitor); // This might be optimized in the future
	}
	
	@Override
	public boolean hasChildren() {
		CheckForChildrenVisitor checkForChildrenVisitor = new CheckForChildrenVisitor();
		visitDirectChildren(checkForChildrenVisitor);
		return checkForChildrenVisitor.hasChildren;
	}
	
	public static final class CheckForChildrenVisitor extends ASTDirectChildrenVisitor {
		boolean hasChildren = false;
		
		@Override
		protected void geneticChildrenVisit(ASTNode child) {
			hasChildren = true;
		}
	}
	
	@Override
	public ASTNode[] getChildren() {
		return ASTChildrenCollector.getChildrenArray(asNode());
	}
	
	// Utility methods
	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	public static void acceptVisitor(IASTVisitor visitor, IASTNode node) {
		if (node != null) {
			node.accept(visitor);
		}
	}
	
	/** Accepts the visitor on the children. If children is null, nothing happens. */
	public static void acceptVisitor(IASTVisitor visitor, Iterable<? extends IASTNode> nodes) {
		if (nodes == null)
			return;
		
		for(IASTNode node : nodes) {
			acceptVisitor(visitor, node);
		}
	}
	
	/* ----------------- cloning ----------------- */
	
	@Override
	public final CommonASTNode cloneTree() {
		final ASTNode clonedNode = (ASTNode) doCloneTree();
		
		assertTrue(hasSourceRangeInfo()); // This assertion might not be necessary, we could clone without range info.
		clonedNode.setSourceRange(getStartPos(), this.getLength());
		clonedNode.setParsedStatus();
		
		assertNotNull(clonedNode);
		assertTrue(clonedNode != this);
		assertTrue(clonedNode.getParent() == null);
		assertTrue(clonedNode.getClass() ==  this.getClass());
		assertTrue(clonedNode.isParsedStatus());
		assertTrue(clonedNode.isCompleted() == false);
		
		return clonedNode;
	}
	
	protected abstract CommonASTNode doCloneTree();
	
	@SuppressWarnings("unchecked")
	protected static <T extends IASTNode> T clone(T node) {
		if(node == null)
			return null;
		return (T) node.cloneTree();
	}
	
	protected static <T extends IASTNode> NodeVector<T> clone(NodeVector<T> nodeListView) {
		if(nodeListView == null)
			return null;
		return nodeListView.cloneTree();
	}
	
	/* ------------------------  Node data ------------------------  */
	
	public final NodeData getData() {
		return assertNotNull(data);
	}
	
	/** Set the data of this node. Cannot be null. Cannot set data twice without explicitly resetting */
	public final void setData(NodeData data) {
		assertNotNull(data);
		this.data = data;
	}
	
	/** Removes the data of this node. Can only remove data if node is in parsed status. 
	 * @return the previous data. */
	public NodeData resetData() {
		assertTrue(isParsedStatus()); // can only remove data if node is in parsed status
		NodeData oldData = data;
		this.data = NodeData.CREATED_STATUS;
		return oldData;
	}
	
	protected CreatedStatusNodeData getDataAtCreatedPhase() {
		assertTrue(data == NodeData.CREATED_STATUS); 
		//return (ParsedNodeData) this.data;
		return NodeData.CREATED_STATUS;
	}
	
	protected ParsedNodeData getDataAtParsedPhase() {
		assertTrue(data.isParsedStatus()); 
		return (ParsedNodeData) data;
	}
	
	public CommonASTNode setParsedStatus() {
		getDataAtCreatedPhase().setParsed(asNode());
		return this;
	}
	
	public void setParsedStatusWithErrors(ParserError... errors) {
		getDataAtCreatedPhase().setParsedWithErrors(asNode(), errors);
	}
	
	public final boolean isParsedStatus() {
		return getData().isParsedStatus();
	}
	
	/* =============== STRING FUNCTIONS =============== */
	
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toStringClassName());
		sb.append(isParsedStatus() ? "#" : ":" + getData());
		
		sb.append(toStringAsCode());
		sb.append("\n");
		return sb.toString(); 
	}
	
	/* =============== Parenting utils =============== */
	
	public static <T> ArrayView<T> nonNull(ArrayView<T> arrayView) {
		return arrayView != null ? arrayView : ArrayView.EMPTY_ARRAYVIEW.<T>upcastTypeParameter();
	}
	
	/** Set the parent of the given node to the receiver. @return node */
	protected <T extends IASTNode> T parentize(T node) {
		if (node != null) {
			node.setParent(asNode());
		}
		return node;
	}
	
	/** Set the parent of the given collection to the receiver. @return collection */
	protected final <C extends Iterable<? extends IASTNode>> C parentize(C collection) {
		parentizeCollection(collection, false, asNode());
		return collection;
	}
	
	public static void parentizeCollection(Iterable<? extends IASTNode> coll, boolean allowNulls, ASTNode parent) {
		if (coll == null) {
			return;
		}
		for (IASTNode node : coll) {
			if(node != null) {
				node.setParent(parent);
			} else {
				assertTrue(allowNulls);
			}
		}
	}
	
	/* =============== Analysis and semantics =============== */
	
	public final void completeLocalAnalysisOnNodeTree() {
		accept(CompleteNodeVisitor.instance);
	}
	
	protected final void completeNodeAnalysis() {
		assertTrue(isParsedStatus());
		doCompleteNodeAnalysis();
		getDataAtParsedPhase().setLocallyAnalysedData(asNode());
	}
	
	protected void doCompleteNodeAnalysis() {
		// Default implementation: do nothing
	}
	
	public boolean isPostParseStatus() {
		return getData().isLocallyAnalyzedStatus();
	}
	
	/* ------------------------------------------------------------ */
	
	public final IModuleNode getModuleNode() {
		return NodeElementUtil.getMatchingParent(this, IModuleNode.class);
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	/* ----------------- Lexical lookup ----------------- */
	
	/** 
	 * Perform a name lookup starting in this node.
	 * The exact mechanism in which the name lookup will be performed will depend on the node, 
	 * but the most common (and default) scenario is to perform a lexical lookup.
	 * */
	public void performNameLookup(CommonScopeLookup lookup) {
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
		
		ASTNode parent = getParent();
		if(parent != null) {
			parent.doPerformNameLookupInLexicalScope(lookup);
		}
	}
	
}