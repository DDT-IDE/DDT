package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;

public class DeclarationAlign extends DeclarationAttrib {
	
	public final int alignNum; // Negative value means null/invalid;
	
	public DeclarationAlign(int alignNum, AttribBodySyntax bodySyntax, NodeList2 decls, SourceRange sr) {
		super(bodySyntax, decls, sr);
		this.alignNum = alignNum;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("align");
		if(alignNum >= 0) {
			cp.append("(", String.valueOf(alignNum), ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}