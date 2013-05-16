package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

public class DeclarationPragma extends DeclarationAttrib implements IDeclaration, IStatement {
	
	public final boolean isStatement;
	public final Symbol pragmaId;
	public final ArrayView<Resolvable> expressions; // TODO
	
	public DeclarationPragma(Symbol id, ArrayView<Resolvable> expressions, AttribBodySyntax abs, ASTNode bodyDecls) {
		super(abs, bodyDecls);
		this.pragmaId = parentize(id);
		this.expressions = parentize(expressions);
		this.isStatement = false;
	}
	
	public DeclarationPragma(Symbol id, ArrayView<Resolvable> expressions, IStatement thenBody) {
		super(AttribBodySyntax.SINGLE_DECL, (ASTNode) thenBody);
		assertTrue(!(thenBody instanceof BlockStatement));
		this.pragmaId = parentize(id);
		this.expressions = parentize(expressions);
		this.isStatement = true;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_PRAGMA;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if(children) {
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
			cp.append(pragmaId);
			cp.appendList(", ", expressions, ", ", "");
		}
		cp.append(") ");
		toStringAsCode_body(cp);
	}
}