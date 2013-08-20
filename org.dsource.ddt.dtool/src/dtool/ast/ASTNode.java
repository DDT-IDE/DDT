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
package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.NodeData.CreatedStatusNodeData;
import dtool.ast.NodeData.ParsedNodeData;
import dtool.ast.definitions.Module;
import dtool.ast.util.ASTChildrenCollector;
import dtool.ast.util.ASTDirectChildrenVisitor;
import dtool.ast.util.NodeUtil;
import dtool.parser.ParserError;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public abstract class ASTNode implements IASTNode {
	
	public static final ASTNode[] NO_ELEMENTS = new ASTNode[0]; 
	
	/** Source range start position */
	protected int sourceStart = -1;
	/** Source range end position */
	protected int sourceEnd = -1;
	
	/** AST node parent, null if the node is the tree root. */
	public ASTNode parent = null;
	/** Custom field to store various kinds of data */
	private NodeData data = NodeData.CREATED_STATUS; 
	
	
	public ASTNode() {
	}
	
	@Override
	public final ASTNode asNode() {
		return this;
	}
	
	/* ------------------------  Source range ------------------------ */

	
	/** Gets the source range start position. */
	@Override
	public final int getStartPos() {
		return sourceStart;
	}
	
	/** Gets the source range end position. */
	@Override
	public final int getEndPos() {
		return sourceEnd;
	}
	
	/** Gets the source range start position, aka offset. */
	@Override
	public final int getOffset() {
		Assert.isTrue(hasSourceRangeInfo());
		return getStartPos();
	}
	
	/** Gets the source range length. */
	@Override
	public final int getLength() {
		Assert.isTrue(hasSourceRangeInfo());
		return getEndPos() - getStartPos();
	}
	
	public final SourceRange getSourceRange() {
		assertTrue(hasSourceRangeInfo());
		return new SourceRange(getStartPos(), getLength());
	}
	
	/** Checks if the node has source range info. */
	public final boolean hasSourceRangeInfo() {
		return this.sourceStart != -1;
	}
	
	/** Sets the source positions, which must be valid. */
	public final void setSourcePosition(int startPos, int endPos) {
		assertTrue(!hasSourceRangeInfo()); // Can only be set once
		assertTrue(startPos >= 0);
		assertTrue(endPos >= startPos);
		this.sourceStart = startPos;
		this.sourceEnd = endPos;
	}
	
	/** Sets the source range of the receiver to given startPositon and given length */
	public final void setSourceRange(int startPosition, int length) {
		setSourcePosition(startPosition, startPosition + length);
	}
	
	/** Sets the source range according to given sourceRange. */
	public final void setSourceRange(SourceRange sourceRange) {
		setSourcePosition(sourceRange.getOffset(), sourceRange.getOffset() + sourceRange.getLength());
	}
	
	/* ------------------------  Parent and children visitor ------------------------ */
	
	@Override
	public final ASTNode getParent() {
		return parent;
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
		this.parent.data = null; // Note, parent becomes an invalid node after this.
		this.parent = null;
	}
	
	/** Accept a visitor into this node. */
	@Override
	public final void accept(IASTVisitor visitor) {
		assertNotNull(visitor);
		
		// begin with the generic pre-visit
		if(visitor.preVisit(this)) {
			visitChildren(visitor);
		}
		// end with the generic post-visit
		visitor.postVisit(this);
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
		return ASTChildrenCollector.getChildrenArray(this);
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
	
	/* ------------------------  Node type ------------------------  */
	
	public abstract ASTNodeTypes getNodeType();

	@Override
	public int getElementType() {
		return getNodeType().ordinal(); 
	}
	
	/* ------------------------  Node data ------------------------  */
	
	public final NodeData getData() {
		return assertNotNull(data);
	}
	
	/** Set the data of this node. Cannot be null. Cannot set data twice without explicitly resetting */
	protected final void setData(NodeData data) {
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
	
	public void setParsedStatus() {
		getDataAtCreatedPhase().setParsed(this);
	}
	
	public void setParsedStatusWithErrors(ParserError... errors) {
		getDataAtCreatedPhase().setParsedWithErrors(this, errors);
	}
	
	public final boolean isParsedStatus() {
		return getData().isParsedStatus();
	}
	
	/* ------------------------------------------------------------ */
	
	public String getModuleFullyQualifiedName() {
		/* BUG here: can be null with synthetic defUnits */
		return getModuleNode().getFullyQualifiedName();
	}
	
	public Module getModuleNode() {
		return NodeUtil.getParentModule(this);
	}
	
	public IScopeNode getOuterLexicalScope() {
		return ReferenceResolver.getOuterLexicalScope(this);
	}
	
	/* =============== STRING FUNCTIONS =============== */
	
	/** Gets the node's classname striped of package qualifier,  plus optional range info. */
	@Override
	public final String toStringAsNode(boolean printRangeInfo) {
		String str = toStringClassName();
		
		if(printRangeInfo) {
			str += " ["+ getStartPos() +"+"+ getLength() +"]";
		}
		return str;
	}
	
	/** Gets the node's classname striped of package qualifier. */
	public final String toStringClassName() {
		String str = this.getClass().getName();
		int lastIx = str.lastIndexOf('.');
		return str.substring(lastIx+1);
	}
	
	@Override
	public final String toString() {
		StringBuilder string = new StringBuilder();
		string.append(toStringClassName());
		string.append(isParsedStatus() ? "#" : ":" + getData());
		
		string.append(toStringAsCode());
		string.append("\n");
		return string.toString(); 
	}
	
	/** Returns a source representation of this node. 
	 * If node parsed without errors then this representation should be equal 
	 * to the original parsed source (disregarding sub-channel tokens).
	 * Otherwise, if there were errors, this method should still try to print something as close as possible
	 * to the original parsed source: 
	 * All tokens that were consumed should be printed.
	 * Expected tokens that were *not* consumed should preferably be printed as well, but it is not strictly required. 
	 */
	public final String toStringAsCode() {
		ASTCodePrinter cp = new ASTCodePrinter();
		toStringAsCode(cp);
		return cp.toString();
	}
	
	/** @see #toStringAsCode() */
	public abstract void toStringAsCode(ASTCodePrinter cp);
	
	/* =============== Parenting utils =============== */
	
	/** Set the parent of the given collection to the receiver. @return collection */
	protected <T extends ArrayView<? extends ASTNode>> T parentize(T collection) {
		parentize(collection, false);
		return collection;
	}
	
	protected <T extends ArrayView<? extends ASTNode>> T parentize(T collection, boolean allowNulls) {
		if (collection != null) {
			for (ASTNode node : collection) {
				if(node != null) {
					node.setParent(this);
				} else {
					assertTrue(allowNulls);
				}
			}
		}
		return collection;
	}
	
	/** Set the parent of the given node to the receiver. @return node */
	protected <T extends IASTNode> T parentize(T node) {
		if (node != null) {
			node.setParent(this);
		}
		return node;
	}
	
	protected <T extends IASTNode> T parentizeI(T node) {
		return parentize(node);
	}
	
	protected <T extends IASTNode> ArrayView<T> parentizeI(ArrayView<T> collection) {
		parentize(CoreUtil.<ArrayView<ASTNode>>blindCast(collection), false);
		return collection;
	}
	
	/* =============== Analysis =============== */
	
	public static void doSimpleAnalysisOnTree(ASTNode treeNode) {
		ASTVisitor childrenVisitor = new LocalAnalysisVisitor();
		treeNode.accept(childrenVisitor);
	}
	
	protected static final class LocalAnalysisVisitor extends ASTVisitor {
		@Override
		public boolean preVisit(ASTNode node) {
			node.doNodeSimpleAnalysis();
			return true;
		}
		
		@Override
		public void postVisit(ASTNode node) {
			node.endNodeSimpleAnalysis();
		}
	}
	
	
	public void doNodeSimpleAnalysis() {
		assertTrue(isParsedStatus());
		// Default implementation: do nothing
	}
	
	public void endNodeSimpleAnalysis() {
		getDataAtParsedPhase().setLocallyAnalysedData(this);
	}
	
	public boolean isPostParseStatus() {
		return getData().isLocallyAnalyzedStatus();
	}
	
}