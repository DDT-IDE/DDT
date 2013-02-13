package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class DeclarationAlign extends DeclarationAttrib {
	
	public final Token alignNum;
	
	public DeclarationAlign(Token alignNum, AttribBodySyntax bodySyntax, NodeList2 decls, SourceRange sr) {
		super(bodySyntax, decls, sr);
		this.alignNum = alignNum;
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
			cp.append("(", alignNum.getSourceValue(), ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}