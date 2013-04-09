package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class DeclarationAlign extends DeclarationAttrib {
	
	public final Token alignNum;
	
	public DeclarationAlign(Token alignNum, AttribBodySyntax bodySyntax, ASTNeoNode bodyDecls, SourceRange sr) {
		super(bodySyntax, bodyDecls, sr);
		this.alignNum = alignNum;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_ALIGN;
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
			cp.appendStrings("(", alignNum.getSourceValue(), ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}