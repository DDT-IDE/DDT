package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefUnit;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.IModuleResolver;

// TODO: retire this element in favor of RefIdentifier?
public class RefImportSelection extends NamedReference implements IImportSelectiveSelection {
	
	public final String name;
	
	public ImportSelective impSel; // non-structural member
	
	public RefImportSelection(String name) {
		this.name = name;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IMPORT_SELECTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	public boolean isMissing() {
		return name == null;
	}
	
	@Override
	public String getTargetSimpleName() {
		return name;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
	@Override
	public String toStringAsElement() {
		return name;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, findOneOnly, moduleResolver);
		RefModule refMod = impSel.getModuleRef();
		Collection<DefUnit> targetmodules = refMod.findTargetDefUnits(moduleResolver, findOneOnly);
		CommonRefQualified.findDefUnitInMultipleDefUnitScopes(targetmodules, search);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		RefModule refMod = impSel.getModuleRef();
		Collection<DefUnit> targetModules = refMod.findTargetDefUnits(search.getModResolver(), false);
		CommonRefQualified.findDefUnitInMultipleDefUnitScopes(targetModules, search);
	}
	
}