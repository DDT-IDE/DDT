package dtool.ast.declarations;


import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.PragmaStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {

	public Symbol ident;
	public Resolvable[] expressions;
	
	public DeclarationPragma(PragmaDeclaration elem, ASTConversionContext convContex) {
		super(elem, elem.decl, convContex);
		this.ident = new Symbol(elem.ident);
		if(elem.args != null)
			this.expressions = Expression.convertMany(elem.args, convContex);
	}
	
	public DeclarationPragma(PragmaStatement elem, ASTConversionContext convContex) {
		super(elem, elem.body, convContex);
		this.ident = new Symbol(elem.ident);
		if(elem.args != null)
			this.expressions = Expression.convertMany(elem.args, convContex);
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[pragma("+ident+",...)]";
	}
}
