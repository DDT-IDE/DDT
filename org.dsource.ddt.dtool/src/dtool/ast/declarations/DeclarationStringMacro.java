package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public class DeclarationStringMacro extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final Resolvable exp;

	public DeclarationStringMacro(CompileDeclaration node, ASTConversionContext convContext) {
		convertNode(node);
		this.exp = Expression.convert(node.exp, convContext);
	}

	public DeclarationStringMacro(CompileStatement node, ASTConversionContext convContext) {
		convertNode(node);
		this.exp = Expression.convert(node.exp, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		// TODO: parse the exp string
		return IteratorUtil.getEMPTY_ITERATOR();
	}

}
