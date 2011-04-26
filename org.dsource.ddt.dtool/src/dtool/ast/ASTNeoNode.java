package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;
import melnorme.utilbox.tree.IElement;
import melnorme.utilbox.tree.IVisitable;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.core.ISourceRange;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ast.IASTNode;
import descent.internal.compiler.parser.ast.IASTVisitor;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DefinitionConverter;
import dtool.refmodel.IScope;
import dtool.refmodel.NodeUtil;

public abstract class ASTNeoNode extends ASTNode implements IASTNode, IElement, IVisitable<IASTVisitor>{
	
	public static final ASTNeoNode[] NO_ELEMENTS = new ASTNeoNode[0]; 
	
	public ASTNeoNode() {
		super(-1, -1);
	}
	
	/** AST node parent, null if the node is the tree root. */
	public ASTNeoNode parent = null;
	
	@Override
	public ASTNeoNode getParent() {
		return parent;
	}
	
	/** Set the parent of this node. Can be null. */
	public void setParent(ASTNeoNode parent) {
		this.parent = parent;
	}
	
	/** Gets the source range start position, aka offset. */
	@Override
	public final int getStartPos() {
		return sourceStart();
	}
	
	/** Gets the source range start position, aka offset. */
	@Override
	public final int getOffset() {
		return getStartPos();
	}
	
	/** Gets the source range length. */
	@Override
	public final int getLength() {
		Assert.isTrue(sourceStart() != -1);
		return getEndPos() - getStartPos();
	}
	
	public final int getLengthUnchecked() {
		return getEndPos() - getStartPos();
	}
	
	/** Gets the source range end position (start position + length). */
	@Override
	public final int getEndPos() {
		return sourceEnd();
	}
	/** Sets the source range end position (start position + length). */
	public final void setEndPos(int endPos) {
		assertTrue(endPos >= sourceStart());
		assertTrue(sourceStart() != -1);
		setEnd(endPos);
	}
	
	/** Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 */
	public final void setSourceRange(int startPosition, int length) {
		//AssertIn.isTrue(startPosition >= 0 && length > 0);
		// source positions are not considered a structural property
		// but we protect them nevertheless
		//checkModifiable();
		setStart(startPosition);
		setEnd(startPosition + length);
	}
	
	/** Gets an ISourceRange of this node's source range. */
	@Override
	public ISourceRange getSourceRange () {
		return super.getSourceRange();
	}
	
	/** Gets an source range (different API Neo). */
	public NeoSourceRange getSourceRangeNeo() {
		return new NeoSourceRange(getStartPos(), getLength(), false);
	}
	
	/** Checks if the node has no defined source range info. */
	@Override
	public final boolean hasNoSourceRangeInfo() {
		return sourceStart() == -1;
	}
	
	@Override
	public boolean hasChildren() {
		return getChildren().length > 0;
	}
	
	
	/******************************/
	
	/** All Neo nodes return the same type 
	 * (until a need arise for otherwise). */
	@Override
	public int getElementType() {
		return 0; 
	}
	
	@Override
	public ASTNeoNode[] getChildren() {
		return (ASTNeoNode[]) ASTNeoChildrenCollector.getChildrenArray(this);
	}
	
	public void convertNode(ASTDmdNode node) {
		setSourceRange(node);
	}
	
	/**
	 * Same as ASTNode.accept but makes sub-elements accept0 use ASTNeoVisitor.
	 * This is a temporary adapting solution.
	 */
	@Override
	public final void accept(IASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		
		IASTNeoVisitor neovisitor = (IASTNeoVisitor) visitor;
		// begin with the generic pre-visit
		neovisitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		this.accept0(neovisitor);
		// end with the generic post-visit
		neovisitor.postVisit(this);
	}
	
	
	public final void accept0(@SuppressWarnings("unused") IASTVisitor visitor) {
		Assert.fail("NEO AST elements should not use IASTVisitor");
	}
	
	// Neo AST elements use ASTNeoVisitor
	public abstract void accept0(IASTNeoVisitor visitor);
	
	
	/** DLTK visitor mechanism */
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		if (visitor.visit(this)) {
			// Use the dtool's visitor to obtain children
			ASTNeoNode[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].traverse(visitor);
			}
		}
		visitor.endvisit(this);	 			
	}
	
	public IScope getModuleScope() {
		return NodeUtil.getParentModule(this);
	}
	
	public Module getModuleNode() {
		return NodeUtil.getParentModule(this);
	}
	
	
	/** Sets the source range the same as the given elem, even if the range is invalid. */
	@Deprecated
	public final void setSourceRange(ASTDmdNode elem) {
		maybeSetSourceRange(DefinitionConverter.convertSourceRange(elem));
	}
	
	/** Sets the source range according to given sourceRange. */
	public final void setSourceRange(NeoSourceRange sourceRange) {
		setStart(sourceRange.getOffset());
		setEnd(sourceRange.getOffset() + sourceRange.getLength());
	}
	
	public final void maybeSetSourceRange(NeoSourceRange sourceRange) {
		if(sourceRange != null) {
			setSourceRange(sourceRange);
		}
	}
	
	
	/* =============== STRING FUNCTIONS =============== */
	
	/** Gets the node's classname striped of package qualifier,  plus optional range info. */
	@Override
	public final String toStringAsNode(boolean printRangeInfo) {
		String str = toStringClassName();
		
		if(printRangeInfo) {
			str += " ["+ getStartPos()  +"+"+ getLengthUnchecked() +"]";
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
	@Deprecated
	public final String toString() {
		//assertFail("ASTNeoNode.toString is for debugging purposes only.");
		return toStringClassName() +" "+ toStringAsCode();
		//return ASTPrinter.toStringAsFullNodeTree(this, true);
	}
	
	/** Returns a simple representation of this node, element-like and for for a line. */
	public String toStringAsElement() {
		return "?";
	}
	
	/** Returns a simple representation of this node (ie. one liner, no members). */
	protected String toStringAsCode() {
		return "<"+toStringAsElement()+">";
	}
	
	
}