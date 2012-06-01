package dtool.ast.references;

import java.util.Collection;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch;

public class RefImportSelection extends NamedReference 
	implements IImportSelectiveSelection {
	
	public final String name;
	
	public ImportSelective impSel; // non structural
	
	public RefImportSelection(String name, ImportSelective impSel, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.name = name;
		this.impSel = impSel; parentize(this.impSel);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String getReferenceName() {
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
