package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.RefIdentifier;
import dtool.ast.statements.IStatement;

/**
 * @see http://dlang.org/declaration.html#AliasThisDeclaration
 * 
 * (Technically not allowed as statement, but parse so anyways.)
 */
public class DeclarationAliasThis extends ASTNode implements IDeclaration, IStatement {
	
	public final boolean isAssignSyntax;
	public final RefIdentifier targetMember;
	
	public DeclarationAliasThis(boolean isAssignSyntax, RefIdentifier targetMember) {
		this.isAssignSyntax = isAssignSyntax;
		this.targetMember = parentize(targetMember);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ALIAS_THIS;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, targetMember);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		if(isAssignSyntax) {
			cp.append("this");
			cp.append(" = ", targetMember);
		} else {
			cp.append(targetMember);
			cp.append(" this");
		}
		cp.append(";");
	}
	
}
