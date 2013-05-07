package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.DeclList;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNode implements INonScopedBlock {
	
	public static enum AttribBodySyntax { SINGLE_DECL, BRACE_BLOCK, COLON }
	
	public final AttribBodySyntax bodySyntax;
	public final ASTNode body; // Note: can be DeclList
	
	public DeclarationAttrib(AttribBodySyntax bodySyntax, ASTNode bodyDecls) {
		this.bodySyntax = assertNotNull_(bodySyntax);
		this.body = parentize(bodyDecls);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		return getBodyIterator(body);
	}
	
	public static Iterator<? extends ASTNode> getBodyIterator(ASTNode body) {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(body instanceof DeclList) {
			return ((DeclList) body).nodes.iterator();
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