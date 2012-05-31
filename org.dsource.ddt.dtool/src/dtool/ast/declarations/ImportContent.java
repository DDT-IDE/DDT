package dtool.ast.declarations;

import descent.internal.compiler.parser.Import;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.ReferenceResolver;

public class ImportContent extends ImportStatic {

	public ImportContent(Import elem) {
		super(elem);
	}
	
	public ImportContent(RefModule refModule) {
		super(refModule);
	}
	
	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		ReferenceResolver.findDefUnitInContentImport(this, options);
	}
}