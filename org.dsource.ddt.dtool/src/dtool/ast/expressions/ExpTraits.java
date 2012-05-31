package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TraitsExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpTraits extends Expression {

	public final ASTNeoNode[] args;
	public final char[] traitsKeyword;
	
	public ExpTraits(TraitsExp node, ASTConversionContext convContext) {
		convertNode(node);
		this.traitsKeyword = node.ident.ident;
		this.args = DescentASTConverter.convertMany(node.args, ASTNeoNode.class, convContext);
	}
	
	public ExpTraits(char[] traitsKeyword, ASTNeoNode[] args) {
		this.traitsKeyword = traitsKeyword;
		this.args = args;
		
		if (this.args != null) {
			for (ASTNeoNode n : this.args) {
				n.setParent(this);
			}
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}

}
