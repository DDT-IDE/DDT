package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationImport.ImportFragment;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefUnit.DefUnitDataTuple;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefModule;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScopeNode;

public class ImportAliasing extends ImportFragment implements INonScopedBlock {
	
	public static class ImportAliasingDefUnit extends DefUnit {
		
		public ImportAliasing impAlias; // Non-structural Element

		public ImportAliasingDefUnit(DefUnitDataTuple dudt, ImportAliasing impAlias) {
			super(dudt);
			this.impAlias = impAlias; parentize(this.impAlias);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias; // Maybe should be ImportAlias
		}

		@Override
		public IScopeNode getMembersScope() {
			return impAlias.moduleRef.getTargetScope();
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, defname);
			}
			visitor.endVisit(this);
		}
	}
	
	ImportAliasingDefUnit aliasDefUnit;
	
	public ImportAliasing(DefUnitDataTuple dudt, RefModule refModule, SourceRange sourceRange) {
		super(refModule, sourceRange);
		this.aliasDefUnit = new ImportAliasingDefUnit(dudt, this); parentize(this.aliasDefUnit);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, aliasDefUnit);
			TreeVisitor.acceptChildren(visitor, moduleRef);
		}
		visitor.endVisit(this);
	}

	@Override
	public void searchInSecondaryScope(CommonDefUnitSearch options) {
		// Do nothing. Aliasing imports do not contribute secondary-space DefUnits
		// TODO: this is a bug in D, it's not according to spec.
	}
	
	@Override
	public Iterator<? extends IASTNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(aliasDefUnit);
	}
	
	@Override
	public String toStringAsElement() {
		return aliasDefUnit.toStringAsElement() 
		+ " = "+ moduleRef.toStringAsElement() ;
	}
}