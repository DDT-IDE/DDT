package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {
	
	public static enum AttribBodySyntax { SINGLE_DECL, BRACE_BLOCK, COLON }
	
	public final AttribBodySyntax bodySyntax;
	public final ASTNeoNode body; // Note: can be NodeList
	
	public DeclarationAttrib(AttribBodySyntax bodySyntax, ASTNeoNode bodyDecls) {
		this.bodySyntax = assertNotNull_(bodySyntax);
		this.body = parentize(bodyDecls);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(body instanceof NodeList2) {
			return ((NodeList2) body).nodes.iterator();
		}
		return IteratorUtil.singletonIterator(body);
	}
	
	public void toStringAsCode_body(ASTCodePrinter cp) {
		cp.append(bodySyntax == AttribBodySyntax.COLON, " :\n");
		cp.append(bodySyntax == AttribBodySyntax.BRACE_BLOCK, " {\n");
		cp.appendNode(body);
		cp.append(bodySyntax == AttribBodySyntax.BRACE_BLOCK, "}");
	}
	
}