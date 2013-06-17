package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;

/**
 * Align declaration 
 * 
 * Technicaly DMD doesn't accept this declaration as a statement, but structurally we allow it,
 * even though a syntax or semantic error may still be issued.
 * 
 */
public class DeclarationAlign extends DeclarationAttrib implements IDeclaration, IStatement {
	
	public final String alignNum;
	
	public DeclarationAlign(String alignNum, AttribBodySyntax bodySyntax, ASTNode bodyDecls) {
		super(bodySyntax, bodyDecls);
		this.alignNum = alignNum;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ALIGN;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("align");
		if(alignNum != null) {
			cp.appendStrings("(", alignNum, ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}