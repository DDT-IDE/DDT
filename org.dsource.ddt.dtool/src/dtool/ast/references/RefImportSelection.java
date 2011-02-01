package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.core.Assert;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch;

public class RefImportSelection extends NamedReference 
	implements IImportSelectiveSelection {
	
	public final String name;
	
	public ImportSelective impSel; // non structural
	
	public RefImportSelection(descent.internal.compiler.parser.IdentifierExp elem,
			ImportSelective impSel) {
		setSourceRange(elem);
		Assert.isTrue(!(elem.ident.length == 0));
		this.name = new String(elem.ident);
		this.impSel = impSel;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	protected String getReferenceName() {
		return name;
	}
	
	@Override
	public String toStringAsElement() {
		return name;
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, findOneOnly);
		RefModule refMod = impSel.moduleRef;
		CommonRefQualified.findDefUnitInMultipleDefUnitScopes(refMod.findTargetDefUnits(findOneOnly), search);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		RefModule refMod = impSel.moduleRef;
		CommonRefQualified.findDefUnitInMultipleDefUnitScopes(
				refMod.findTargetDefUnits(false), search);
	}
	
}
