package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {
	
	public final Symbol pragmaId;
	public final ArrayView<Resolvable> expressions; // TODO
	
	public DeclarationPragma(Symbol id, ArrayView<Resolvable> expressions, AttribBodySyntax abs, 
		ASTNode bodyDecls) {
		super(abs, bodyDecls);
		this.pragmaId = parentize(id);
		this.expressions = parentize(expressions);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_PRAGMA;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			// TODO, consider whether these should be structural or not
			TreeVisitor.acceptChildren(visitor, pragmaId);
//			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("pragma(");
		if(pragmaId != null) {
			cp.append(pragmaId.name);
			if(expressions != null) {
				cp.append(", ");
				cp.appendNodeList(expressions, ", ");
			}
		}
		cp.append(") ");
		toStringAsCode_body(cp);
	}
}