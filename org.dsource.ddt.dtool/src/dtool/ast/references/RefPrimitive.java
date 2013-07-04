package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.parser.IToken;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.ScopeUtil;
import dtool.resolver.api.IModuleResolver;

public class RefPrimitive extends NamedReference {
	
	public final IToken primitive;
	
	public RefPrimitive(IToken primitiveToken) {
		this.primitive = primitiveToken;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_PRIMITIVE;
	}
	
	@Override
	public String getTargetSimpleName() {
		return primitive.getSourceValue();
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(getTargetSimpleName(), this, this.getStartPos(), 
			findOneOnly, moduleResolver);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		IScopeNode lookupScope = ScopeUtil.getOuterScope(this);
		ReferenceResolver.findDefUnitInExtendedScope(lookupScope, search);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(primitive);
	}
	
}