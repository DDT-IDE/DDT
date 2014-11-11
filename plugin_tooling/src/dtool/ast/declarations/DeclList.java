package dtool.ast.declarations;

import melnorme.utilbox.collections.ArrayView;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList;

public class DeclList extends NodeList<ASTNode> {
	
	public DeclList(ArrayView<ASTNode> nodes) {
		super(nodes);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_LIST;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(nodes, "\n", true);
	}
	
}