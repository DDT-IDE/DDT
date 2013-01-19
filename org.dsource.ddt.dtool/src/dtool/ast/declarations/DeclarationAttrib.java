package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {
	
	public static enum AttribBodySyntax { SINGLE_DECL, BRACE_BLOCK, COLON }
	
	public final AttribBodySyntax bodySyntax;
	public final NodeList2 body;
	
	public DeclarationAttrib(AttribBodySyntax bodySyntax, NodeList2 bodyDecls, SourceRange sr) {
		this.bodySyntax = assertNotNull_(bodySyntax);
		this.body = parentize(bodyDecls);
		initSourceRange(sr);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return body.nodes.iterator();
	}
	
	public void toStringAsCode_body(ASTCodePrinter cp) {
		if(bodySyntax == AttribBodySyntax.COLON) {
			cp.append(" :\n");
		} else if(bodySyntax == AttribBodySyntax.BRACE_BLOCK) {
			cp.append(" {\n");
		}
		cp.appendNode(body);
		if(bodySyntax == AttribBodySyntax.BRACE_BLOCK) {
			cp.append("}");
		}
	}
	
}