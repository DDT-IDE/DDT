package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList;
import dtool.ast.definitions.DefinitionAggregate.IAggregateBody;
import dtool.ast.definitions.DefinitionClass;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public class DeclBlock extends NodeList<ASTNode> implements IAggregateBody, IScopeNode {
	
	public DeclBlock(ArrayView<ASTNode> nodes) {
		super(nodes);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_BLOCK;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("{\n", nodes, "\n", "\n}\n");
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, nodes, false);
		// TODO: a more typesafe alternative to this check
		if(getParent() instanceof DefinitionClass) {
			DefinitionClass definitionClass = (DefinitionClass) getParent();
			definitionClass.resolveSearchInSuperScopes(search);
		}
	}
	
}