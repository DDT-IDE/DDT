package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.NodeList_OLD;
import dtool.ast.statements.BlockStatementUnscoped;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNode implements INonScopedBlock, IDeclaration {
	
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
		if(body == null) {
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		if(body instanceof NodeList_OLD) { /*BUG here MAKE, do DeclList instead of NodeList*/
			return ((NodeList_OLD<?>) body).nodes.iterator();
		}
		if(body instanceof DeclList) { /*BUG here MAKE, */
			return ((DeclList) body).nodes.iterator();
		}
		if(body instanceof BlockStatementUnscoped) { /*BUG here MAKE, comment*/
			return ((BlockStatementUnscoped) body).getMembersIterator();
		}
		return IteratorUtil.singletonIterator(body);
	}
	
	public void toStringAsCode_body(ASTCodePrinter cp) {
		cp.append(bodySyntax == AttribBodySyntax.COLON, " :\n");
		cp.append(body);
	}
	
}