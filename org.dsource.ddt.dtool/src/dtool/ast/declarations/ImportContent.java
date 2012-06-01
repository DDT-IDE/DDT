package dtool.ast.declarations;

import dtool.ast.SourceRange;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;

public class ImportContent extends ImportStatic {

	public ImportContent(RefModule refModule, SourceRange sourceRange) {
		super(refModule, sourceRange);
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		ReferenceResolver.findDefUnitInContentImport(this, options);
	}
}