package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTNodeTypes;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

public class RefIdentifier extends CommonRefIdentifier implements ITemplateRefNode {
	
	public RefIdentifier(String name) {
		super(name);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IDENTIFIER;
	}
	
	@Override
	public String getTargetSimpleName() { 
		return identifier;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		if(isMissing())
			return null;
		DefUnitSearch search = new DefUnitSearch(identifier, this, this.getStartPos(), findOneOnly, moduleResolver);
		doSearchForPossiblyQualifiedSingleRef(search, this);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
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
				CommonRefQualified.doQualifiedSearch(search, parent);
				return;
			}
			// continue using outer scope as the lookup
		}
		
		ReferenceResolver.resolveSearchInFullLexicalScope(refSingle, search);
	}
	
}