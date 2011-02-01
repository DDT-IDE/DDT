package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AnonDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public class DeclarationAnonMember extends ASTNeoNode implements INonScopedBlock {

	public NodeList body;

	public DeclarationAnonMember(AnonDeclaration node, ASTConversionContext convContext) {
		convertNode(node);
		assertNotNull(node.decl);
		this.body = NodeList.createNodeList(node.decl, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body.nodes);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return Arrays.asList(body.nodes).iterator();
	}

}
