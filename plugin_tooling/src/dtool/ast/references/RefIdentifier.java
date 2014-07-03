package dtool.ast.references;

import dtool.ast.ASTNodeTypes;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;

public class RefIdentifier extends CommonRefIdentifier implements ITemplateRefNode {
	
	public RefIdentifier(String identifier) {
		super(identifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IDENTIFIER;
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		// Check if we are the qualifer of a parent qualified ref
		if(getParent() instanceof CommonRefQualified) {
			CommonRefQualified parent = (CommonRefQualified) getParent();
			if(parent.getQualifiedName() == this) {
				// if so, then we must do qualified search (use root as the lookup scope)
				parent.performRefSearch(search);
				return;
			}
		}
		super.performRefSearch(search);
	}
	
}