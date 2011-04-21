package dtool.ast.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DeclarationConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationAttrib extends ASTNeoNode implements INonScopedBlock {
	
	public final NodeList body;
	
	public DeclarationAttrib(Statement elem, Statement body, ASTConversionContext convContex) {
		convertNode(elem);
		this.body = NodeList.createNodeList(body, convContex);
	}
	
	public DeclarationAttrib(AttribDeclaration elem, List<Dsymbol> bodydecls, ASTConversionContext convContex) {
		convertNode(elem);
		DeclarationConverter.doSetParent(elem, bodydecls);
		this.body = NodeList.createNodeList(bodydecls, convContex);
	}
	
	protected void acceptBodyChildren(IASTNeoVisitor visitor) {
		if(body != null) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(body == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return body.getNodeIterator();
	}
	
}