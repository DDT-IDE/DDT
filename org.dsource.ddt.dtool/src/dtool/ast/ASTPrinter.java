package dtool.ast;

import java.util.Iterator;

import melnorme.utilbox.tree.TreeDepthRecon;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.references.RefQualified;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoUpTreeVisitor {
	
	/* ===================== Helpers ============================ */
	
	
	public static String toStringParamListAsElements(Iterable<? extends ASTNeoNode> nodes) {
		if(nodes == null)
			return "";
		return "("+toStringAsElements(nodes, ", ")+")";
	}
	
	/** Util for printing a collection of nodes. */
	public final static String toStringAsElements(Iterable<? extends ASTNeoNode> nodes, String sep) {
		StringBuilder sb = new StringBuilder();
		Iterator<? extends ASTNeoNode> iter = nodes.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			ASTNeoNode next = iter.next();
			if(i > 0) {
				sb.append(sep);
			}
			sb.append(next.toStringAsElement());
		}
		return sb.toString();
	}
	
	/** Gets a String representation of the whole node tree, with one
	 * line per node, and using toStringAsNodePlusExtra */
	public static String toStringAsFullNodeTree(ASTNeoNode elem, boolean recurseUnconverted) {
		ASTPrinter astPrinter = new ASTPrinter();
		astPrinter.recurseUnconverted = recurseUnconverted;
		elem.accept(astPrinter);
		return astPrinter.strbuffer.toString();
	}
	
	/* ====================================================== */
	
	
	// print source range
	public boolean printRangeInfo = true;
	//Print leaf nodes in same line?
	public boolean collapseLeafs = false; 
	//recurse UncovertedElements?	
	public boolean recurseUnconverted = true; 
	// visit the Element's children
	public boolean visitChildren = true;
	// visit children of node type QualifiedName
	public boolean visitQualifiedNameChildren = false;
	
	private int indent;
	private boolean allSiblingsAreLeafs;
	
	
	// A string buffer to where the string representation is written 
	protected StringBuffer strbuffer;
	
	private ASTPrinter() {
		this.indent = 0;
		this.strbuffer = new StringBuffer();
	}
	
	/** Gets a String representation of elem only, with extra info. */
	private String toStringAsNodePlusExtra(ASTNeoNode elem) {
		return elem.toStringAsNode(printRangeInfo) +" "+ elem.toStringAsElement();
	}
	
	/* ---------------------------------- */
	protected void print(String str) {
		strbuffer.append(str);
	}
	protected void println(String str) {
		strbuffer.append(str);
		strbuffer.append("\n");
	}	
	
	
	private void printIndent() {
		print(melnorme.utilbox.misc.StringUtil.newFilledString(indent, "  "));
	}
	
	private void printNodeDecorations(IASTNode node, String str) {
		int maxdepth = collapseLeafs? TreeDepthRecon.findMaxDepth(node) : -1;
		
		if(collapseLeafs && maxdepth == 1 && allSiblingsAreLeafs)
			print("  ");
		else
			printIndent();
		
		print(str);
		
		if(collapseLeafs && maxdepth == 2) {
			allSiblingsAreLeafs = true;
			print("    (");
		} else if(collapseLeafs && maxdepth == 1 && allSiblingsAreLeafs) {
			
		} else {
			println("");
		}
		indent++;
	}
	
	/* ====================================================== */
	
	/* ---------------- Neo ------------------ */
	@Override
	public boolean visit(ASTNeoNode elem) {
		printNodeDecorations(elem, toStringAsNodePlusExtra(elem) );
		return visitChildren;
	}
	
	public boolean visit(RefQualified elem) {
		printNodeDecorations(elem, toStringAsNodePlusExtra(elem));
		return visitChildren && visitQualifiedNameChildren;
	}
	
	
	/* ---------------------------------- */
	
}

