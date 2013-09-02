package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTNodeTypes;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.CommonDefUnitSearch;

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
	
	public ImportSelective getImportSelectiveContainer() {
		return impSel;
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		RefModule refMod = getImportSelectiveContainer().getModuleRef();
		Collection<INamedElement> targetModules = refMod.findTargetDefElements(search.getModuleResolver(), false);
		CommonRefQualified.resolveSearchInMultipleElements(targetModules, search);
	}
	
}