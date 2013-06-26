package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.api.IModuleResolver;

public class RefIdentifier extends NamedReference implements ITemplateRefNode {
	
	// this is private because of contract restriction, use appropriate getters instead
	private final String identifier; 
	
	public RefIdentifier(String name) {
		this.identifier = name;
		assertTrue(name == null || name.length() > 0); 
		assertTrue(getIdString().indexOf(' ') == -1);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IDENTIFIER;
	}
	
	public boolean isMissing() {
		return identifier == null;
	}
	
	public String getIdString() {
		return identifier == null ? "" : identifier;
	}
	
	@Override
	public String getTargetSimpleName() { 
		return identifier;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
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
	public static void doSearchForPossiblyQualifiedSingleRef(CommonDefUnitSearch search, RefIdentifier refSingle) {
		// First determine the lookup scope.
		if(refSingle.getParent() instanceof CommonRefQualified) {
			CommonRefQualified parent = (CommonRefQualified) refSingle.getParent();
			// check if this single ref is the sub ref of a qualified ref
			if(parent.getQualifiedName() == refSingle) {
				// then we must do qualified search (use root as the lookup scopes)
				CommonRefQualified.doQualifiedSearch(search, parent);
				return;
			} else {
				assertTrue(((RefQualified)parent).qualifier == refSingle);
				// continue using outer scope as the lookup
			}
		}
		
		IScopeNode lookupScope = ReferenceResolver.getStartingScope(refSingle);
		ReferenceResolver.findDefUnitInExtendedScope(lookupScope, search);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(identifier);
	}
	
}