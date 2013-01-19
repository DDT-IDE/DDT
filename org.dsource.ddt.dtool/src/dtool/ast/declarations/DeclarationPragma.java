package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {
	
	public final Symbol pragmaId;
	public final ArrayView<Resolvable> expressions;
	
	public DeclarationPragma(Symbol id, ArrayView<Resolvable> expressions, AttribBodySyntax abs, NodeList2 bodyDecls,
		SourceRange sr) {
		super(abs, bodyDecls, sr);
		this.pragmaId = parentize(id);
		this.expressions = parentize(expressions);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
			TreeVisitor.acceptChildren(visitor, pragmaId);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("pragma");
		if(pragmaId != null) {
			cp.append("(", pragmaId.name);
			if(expressions != null) {
				for(Resolvable resolvable : expressions) {
					cp.append(", ");
					cp.appendNode(resolvable);
				}
			}
			cp.append(")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
}