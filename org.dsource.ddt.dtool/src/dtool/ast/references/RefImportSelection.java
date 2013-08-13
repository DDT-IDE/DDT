package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTNodeTypes;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

// TODO: retire this element in favor of RefIdentifier?
public class RefImportSelection extends CommonRefIdentifier implements IImportSelectiveSelection {
	
	public ImportSelective impSel; // non-structural member
	
	public RefImportSelection(String identifier) {
		super(identifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IMPORT_SELECTION;
	}
	
	@Override
	public String getTargetSimpleName() { 
		return identifier;
	}
	
	public ImportSelective getImportSelectiveContainer() {
		return impSel;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		if(syntaxIsMissingIdentifier()) {
			return null;
		}
		RefModule refMod = getImportSelectiveContainer().getModuleRef();
		Module targetModule = refMod.findTargetModule(moduleResolver);
		if(targetModule != null) {
			DefUnitSearch search = new DefUnitSearch(identifier, this, findOneOnly, moduleResolver);
			ReferenceResolver.findDefUnitInScope(targetModule, search);
			return search.getMatchDefUnits();
		}
		return null;
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		/*BUG here make consistent with findTargetDefUnits */
		RefModule refMod = getImportSelectiveContainer().getModuleRef();
		Collection<DefUnit> targetModules = refMod.findTargetDefUnits(search.getModResolver(), false);
		CommonRefQualified.findDefUnitInMultipleDefUnitScopes(targetModules, search);
	}
	
}