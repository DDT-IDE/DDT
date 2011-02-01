package dtool.ast;

import java.util.Collection;
import java.util.Iterator;

import melnorme.utilbox.tree.TreeDepthRecon;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.references.RefQualified;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoUpTreeVisitor {

	/* ===================== Helpers ============================ */

	public static String toStringParamListAsElements(ASTNeoNode[] nodes) {
		if(nodes == null)
			return "";
		return "("+toStringAsElements(nodes, ", ")+")";
	}
	
	/** Util for printing a collection of nodes. */
	public static String toStringAsElements(ASTNeoNode[] nodes, String sep) {
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nodes.length; i++) {
			if(i > 0)
				sb.append(sep);
			sb.append(nodes[i].toStringAsElement());
		}
		return sb.toString();
	}
	
	public final static String toStringAsElements(
			Collection<? extends ASTNeoNode> nodes, String sep) {
		StringBuilder sb = new StringBuilder();
		Iterator<? extends ASTNeoNode> iter = nodes.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			if(i > 0)
				sb.append(sep);
			sb.append(iter.next().toStringAsElement());
		}
		return sb.toString();
	}
	
	/** Gets a String representation of the whole node tree, with one
	 * line per node, and using toStringAsNodePlusExtra */
	public static String toStringAsFullNodeTree(IASTNode elem, boolean recurseUnconverted) {
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
	private String toStringAsNodePlusExtra(ASTNode elem) {
		return elem.toStringAsNode(printRangeInfo);
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
	@Override
	public boolean visit(ASTNode elem) {
		printNodeDecorations(elem, toStringAsNodePlusExtra(elem));
		return visitChildren && recurseUnconverted;
	}

	
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
	@Override
	public void endVisit(ASTNode element) {
		
		if(collapseLeafs && TreeDepthRecon.findMaxDepth(element) == 2) {
			allSiblingsAreLeafs = false;
			println(" )");
		}

		indent--;
	}


	

}

