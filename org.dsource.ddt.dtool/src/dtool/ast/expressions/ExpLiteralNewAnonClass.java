package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.NewAnonClassExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.Declaration;
import dtool.ast.definitions.BaseClass;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;

public class ExpLiteralNewAnonClass extends Expression {
	
	public Resolvable[] allocargs;
	public Resolvable[] args;
	public BaseClass[] baseClasses;
	public ASTNeoNode[] members; 


	public ExpLiteralNewAnonClass(NewAnonClassExp elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.allocargs = ExpressionConverter.convertMany(elem.newargs, convContext); 
		this.args = ExpressionConverter.convertMany(elem.arguments, convContext); 
		this.baseClasses = DescentASTConverter.convertMany(elem.cd.sourceBaseclasses.toArray(),
				new BaseClass[elem.cd.sourceBaseclasses.size()], convContext);
		this.members = Declaration.convertMany(elem.cd.members, convContext);
		
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocargs);
			TreeVisitor.acceptChildren(visitor, args);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
