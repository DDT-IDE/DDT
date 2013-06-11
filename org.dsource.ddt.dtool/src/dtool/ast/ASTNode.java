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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;
import melnorme.utilbox.core.CoreUtil;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.NodeData.ParsedNodeDataWithErrors;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DefinitionConverter;
import dtool.parser.ParserError;
import dtool.refmodel.INamedScope;
import dtool.util.ArrayView;

public abstract class ASTNode implements IASTNeoNode {
	
	public static final ASTNode[] NO_ELEMENTS = new ASTNode[0]; 
	
	protected int sourceStart = -1;
	protected int sourceEnd = -1;
	
	public ASTNode() {
	}
	
	@Override
	public final ASTNode asNode() {
		return this;
	}
	
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
	
	protected void setSourceStart(int start) {
		this.sourceStart = start;
	}
	
	protected void setSourceEnd(int end) {
		this.sourceEnd = end;
	}
	
	/** Sets the source positions, which must be valid. */
	public final void setSourcePosition(int startPos, int endPos) {
		assertTrue(startPos >= 0);
		assertTrue(endPos >= startPos);
		setSourceStart(startPos);
		setSourceEnd(endPos);
	}
	
	/** Checks if the node has source range info. */
	public final boolean hasSourceRangeInfo() {
		return this.sourceStart != -1;
	}
	
	/** Checks if the node has no source range info. */
	@Override
	public final boolean hasNoSourceRangeInfo() {
		return !hasSourceRangeInfo();
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
	
	/** Sets the source range of this onde to given startPositon and given length */
	public final void setSourceRange(int startPosition, int length) {
		setSourceStart(startPosition);
		setSourceEnd(startPosition + length);
	}
	
	/** Sets the source range according to given sourceRange. */
	public final void setSourceRange(SourceRange sourceRange) {
		setSourceStart(sourceRange.getOffset());
		setSourceEnd(sourceRange.getOffset() + sourceRange.getLength());
	}
	
	/** Sets the source range the same as the given elem, even if the range is invalid. */
	@Deprecated
	public final void setSourceRange(ASTDmdNode elem) {
		initSourceRange(DefinitionConverter.sourceRange(elem));
	}
	
	@Deprecated
	public final void initSourceRange(SourceRange sourceRange) {
		if(sourceRange != null) {
			setSourceRange(sourceRange);
		}
	}
	
	
	/** AST node parent, null if the node is the tree root. */
	public ASTNode parent = null;
	
	@Override
	public ASTNode getParent() {
		return parent;
	}
	
	/** Set the parent of this node. Cannot be null. Cannot set parent twice without explicitly detaching. */
	@Override
	public void setParent(ASTNode parent) {
		assertTrue(parent != null);
		assertTrue(this.parent == null);
		this.parent = parent;
	}
	
	public void detachFromParent() {
		assertNotNull(this.parent);
		this.parent.data = null; // Note, parent becomes an invalid node after this.
		this.parent = null;
	}
	
	protected NodeData data = NodeData.CREATED_STATUS; /* Custom field to store various kinds of data */
	
	public NodeData getData() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> klass) {
		assertTrue(klass.isAssignableFrom(data.getClass()));
		return (T) data;
	}
	
	/** Set the data of this node. Cannot be null. Cannot set data twice without explicitly resetting */
	public void setData(NodeData data) {
		assertTrue(data != null);
		assertTrue(this.data == NodeData.CREATED_STATUS); 
		this.data = data;
		this.data.attachedToNode(this);
	}
	
	/** Removes the data of this node. Checks that the previous data class was exactly the same as given klass. 
	 * @return the previous data. */
	public <T extends NodeData> T removeData(Class<T> klass) {
		assertTrue(klass.isAssignableFrom(data.getClass()));
		T oldData = klass.cast(data);
		this.data = NodeData.CREATED_STATUS;
		return oldData;
	}
	
	public void setParsedStatus() {
		setData(NodeData.DEFAULT_PARSED_STATUS);
	}
	
	public void setParsedStatusWithErrors(ParserError... errors) {
		setData(new ParsedNodeDataWithErrors(errors));
	}
	
	public boolean isParsedStatus() {
		return getData().isParsedStatus();
	}
	
	public void afterModuleParseCheck() {
		// TODO: call afterModuleParseCheck
		assertTrue(isParsedStatus());
	}
	
	/* ------------------------------------------------------------ */
	
	@Override
	public int getElementType() {
		return getNodeType().ordinal(); 
	}
	
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.OTHER;
	}
	
	@Override
	public ASTNode[] getChildren() {
		return ASTChildrenCollector.getChildrenArray(this);
	}
	
	@Override
	public boolean hasChildren() {
		// TODO: fix performance issue here.
		return getChildren().length > 0;
	}
	
	/** Accept a visitor into this node. */
	@Override
	public final void accept(IASTVisitor visitor) {
		assertNotNull(visitor);
		
		// begin with the generic pre-visit
		if(visitor.preVisit(this)) {
			// dynamic dispatch to internal method for type-specific visit/endVisit
			accept0(visitor);
		}
		// end with the generic post-visit
		visitor.postVisit(this);
	}
	
	public abstract void accept0(IASTVisitor visitor);
	
	
	public INamedScope getModuleScope() {
		return NodeUtil.getParentModule(this);
	}
	
	public Module getModuleNode() {
		return NodeUtil.getParentModule(this);
	}
	
	/* =============== STRING FUNCTIONS =============== */
	
	/** Gets the node's classname striped of package qualifier,  plus optional range info. */
	@Override
	public final String toStringAsNode(boolean printRangeInfo) {
		String str = toStringClassName();
		
		if(printRangeInfo) {
			if(hasNoSourceRangeInfo()) {
				str += " [?+?]";
			} else {
				str += " ["+ getStartPos() +"+"+ getLength() +"]";
			}
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
		String suffix = isParsedStatus() ? "#" : ":" + getData();
		return toStringClassName() + suffix +"【"+toStringAsCode()+"】";
	}
	
	/** Returns a simple representation of this node, element-like and for for a line. 
	 * TODO: need to fix this API */
	public String toStringAsElement() {
		return toStringAsCode();
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
	public void toStringAsCode(ASTCodePrinter cp) {
		throw assertFail();
	}
	
	/* =============== Parenting =============== */
	
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
	protected <T extends IASTNeoNode> T parentize(T node) {
		if (node != null) {
			node.setParent(this);
		}
		return node;
	}
	
	protected <T extends IASTNeoNode> T parentizeI(T node) {
		return parentize(node);
	}
	
	protected <T extends IASTNeoNode> ArrayView<T> parentizeI(ArrayView<T> collection) {
		parentize(CoreUtil.<ArrayView<ASTNode>>blindCast(collection), false);
		return collection;
	}
	
}