package dtool.ast.declarations;

import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeList;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.definitions.DefinitionAggregate.IAggregateBody;
import dtool.ast.definitions.DefinitionClass;

public class DeclBlock extends NodeList<ASTNode> implements IAggregateBody, IScopeElement {
	
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
	public void resolveSearchInScope(CommonScopeLookup search) {
		search.evaluateScopeNodeList(nodes, false);
		// TODO: a more typesafe alternative to this check
		if(getParent() instanceof DefinitionClass) {
			DefinitionClass definitionClass = (DefinitionClass) getParent();
			definitionClass.getSemantics().resolveSearchInSuperScopes(search);
		}
	}
	
}