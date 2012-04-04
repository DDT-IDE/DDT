package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode {
	
	public ArrayView<IStatement> statements;
	public boolean hasCurlyBraces; // syntax-structural?
	
	public BlockStatement(IStatement[] statements, boolean hasCurlyBraces, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.statements = new ArrayView<IStatement>(statements); parentize(this.statements);
		this.hasCurlyBraces = hasCurlyBraces;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}


	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<ASTNode> getMembersIterator() {
		return (Iterator) statements.iterator();
	}
	@Override
	public List<IScope> getSuperScopes() {
		return null;
	}
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	//@Override
	/*public IScope getAdaptedScope() {
		return this;
	}*/

}
