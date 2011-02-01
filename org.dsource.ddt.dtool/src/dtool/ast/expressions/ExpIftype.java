package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.TOK;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class ExpIftype extends Expression {

	public Reference arg;
	public TOK tok;
	public Reference specType;
	
	public ExpIftype(IsExp node, ASTConversionContext convContext) {
		convertNode(node);
		//Assert.isNull(node.id); //Can occur in error in illegal D code
		this.tok = node.tok;
		this.arg = ReferenceConverter.convertType(node.targ, convContext);
		this.specType = ReferenceConverter.convertType(node.tspec, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, specType);
		}
		visitor.endVisit(this);
	}

}
