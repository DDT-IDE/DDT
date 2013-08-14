package dtool.ast.references;

import dtool.ast.ASTNodeTypes;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public class RefIdentifier extends CommonRefIdentifier implements ITemplateRefNode {
	
	public RefIdentifier(String identifier) {
		super(identifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IDENTIFIER;
	}
	
	@Override
	public void doSearch(CommonDefUnitSearch search) {
		doSearchForPossiblyQualifiedSingleRef(search, this);
	}
	
	/** Does a search determining the correct lookup scope when
	 * the CommonRefSingle is part of a qualified reference. */
	public void doSearchForPossiblyQualifiedSingleRef(CommonDefUnitSearch search, RefIdentifier refSingle) {
		// First determine the lookup scope.
		if(refSingle.getParent() instanceof CommonRefQualified) {
			CommonRefQualified parent = (CommonRefQualified) refSingle.getParent();
			// check if the ref id is qualifier or qualified
			if(parent.getQualifiedName() == refSingle) {
				// then we must do qualified search (use root as the lookup scopes)
				parent.doSearch(search);
				return;
			}
			// continue using outer scope as the lookup
		}
		
		ReferenceResolver.resolveSearchInFullLexicalScope(refSingle, search);
	}
	
}