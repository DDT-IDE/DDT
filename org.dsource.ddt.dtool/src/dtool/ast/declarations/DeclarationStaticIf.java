package dtool.ast.declarations;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.StaticIfCondition;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class DeclarationStaticIf extends DeclarationConditional {
	
	public Resolvable exp;
	
	public DeclarationStaticIf(ASTDmdNode elem, StaticIfCondition condition, NodeList thendecls, NodeList elsedecls
			, ASTConversionContext convContext) {
		convertNode(elem);
		this.exp = ExpressionConverter.convert(condition.exp, convContext);
		this.thendecls = thendecls; 
		this.elsedecls = elsedecls;
	}
	
	
	public DeclarationStaticIf(Resolvable exp, Collection<IStatement> thenDecls, Collection<IStatement> elseDecls) {
		super(
			NodeList.createNodeList(thenDecls, false),
			NodeList.createNodeList(elseDecls, false)
		);
		this.exp = exp;
		if (this.exp != null)
			this.exp.setParent(this);
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thendecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[static if("+"..."+")]";
	}
	
}
