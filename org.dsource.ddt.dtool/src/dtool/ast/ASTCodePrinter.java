package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.parser.Token;
import dtool.util.ArrayView;

public class ASTCodePrinter {
	
	public final String ST_SEP = "\n";
	
	protected final StringBuilder sb = new StringBuilder();
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
	public String getPrintedSource() {
		return sb.toString();
	}
	
	public void append(Token obj) {
		if(obj != null) {
			sb.append(obj.source);
		}
	}
	
	public void append(String string) {
		if(string != null) {
			sb.append(string);
		}
	}
	
	public void append(String... strings) {
		for (String string : strings) {
			append(string);
		}
	}
	
	public void append(ASTNeoNode node) {
		appendNode(node);
	}
	
	public void appendNode(ASTNeoNode node) {
		if(node != null) {
			node.toStringAsCode(this);
		}
	}
	
	public void appendNode(ASTNeoNode node, String sep) {
		if(node != null) {
			node.toStringAsCode(this);
			assertNotNull(sep);
			append(sep);
		}
	}
	
	public void appendNode(String prefix, ASTNeoNode node) {
		if(node != null) {
			append(prefix);
			node.toStringAsCode(this);
		}
	}
	
	public void appendNode(String prefix, ASTNeoNode node, String suffix) {
		if(node != null) {
			append(prefix);
			node.toStringAsCode(this);
			append(suffix);
		}
	}
	
	public void appendNodeList(ArrayView<? extends ASTNeoNode> members, String sep) {
		appendNodeList(members, sep, false);
	}
	
	public void appendNodeList(ArrayView<? extends ASTNeoNode> members, String sep, boolean printLastSep) {
		for (int i = 0; i < members.size(); i++) {
			ASTNeoNode node = members.get(i);
			appendNode(node);
			if(printLastSep || i != members.size() - 1) {
				sb.append(sep);
			}
		}
	}
	
	
	public void appendList(String[] packages, String sep, boolean printLastSep) {
		for (int i = 0; i < packages.length; i++) {
			String string = packages[i];
			append(string);
			if(printLastSep || i != packages.length - 1) {
				sb.append(sep);
			}
		}
	}
	
	public void appendList(ArrayView<String> members, String sep, boolean printLastSep) {
		for (int i = 0; i < members.size(); i++) {
			String str = members.get(i);
			append(str);
			if(printLastSep || i != members.size() - 1) {
				sb.append(sep);
			}
		}
	}
	
	public void appendStatementSep() {
		append(ST_SEP);
	}
	
	/* ==== */
	@Deprecated
	public static String toStringParamListAsElements(Iterable<? extends DefUnit> nodes) {
		if(nodes == null)
			return "";
		return "("+toStringAsElements(nodes, ", ")+")";
	}
	
	/** Util for printing a collection of nodes. */
	@Deprecated
	public final static String toStringAsElements(Iterable<? extends DefUnit> nodes, String sep) {
		StringBuilder sb = new StringBuilder();
		Iterator<? extends DefUnit> iter = nodes.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			DefUnit next = iter.next();
			if(i > 0) {
				sb.append(sep);
			}
			sb.append(next.toStringAsElement());
		}
		return sb.toString();
	}
	
	public final void appendNodesAsElements(Iterable<? extends ASTNeoNode> nodes, String sep) {
		Iterator<? extends ASTNeoNode> iter = nodes.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			ASTNeoNode next = iter.next();
			if(i > 0) {
				sb.append(sep);
			}
			sb.append(next.toStringAsElement());
		}
	}
	
	public boolean appendArgList(String open, ArrayView<Expression> args, String sep, String close) {
		return appendArgList(open, args, sep, close, null);
	}
	
	public boolean appendArgList(String open, ArrayView<Expression> args, String sep, String close, String spacing) {
		if(args != null) {
			append(open);
			appendNodeList(args, sep);
			append(close);
			return true;
		} else {
			append(spacing);
		}
		return false;
	}
	
}