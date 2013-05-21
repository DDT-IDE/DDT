package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.statements.IStatement;

public class DeclarationAtAttrib extends DeclarationAttrib implements IDeclaration, IStatement {
	
	public final Symbol attribId;
	
	public DeclarationAtAttrib(Symbol attribId, AttribBodySyntax abs, ASTNode bodyDecls) {
		super(abs, bodyDecls);
		this.attribId = parentize(assertNotNull_(attribId));
		
		localAnalysis();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_AT_ATTRIB;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, attribId);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("@", attribId, " ");
		toStringAsCode_body(cp);
	}
	
	public void localAnalysis() {
//		applyAttributes(this);
	}
	
}