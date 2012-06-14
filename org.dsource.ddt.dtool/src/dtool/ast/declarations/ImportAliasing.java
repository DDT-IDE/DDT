package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
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
import dtool.refmodel.pluginadapters.IModuleResolver;

public class ImportAliasing extends ImportFragment implements INonScopedBlock {
	
	public static class ImportAliasingDefUnit extends DefUnit {
		
		public final RefModule targetModule; // Non-structural element
		
		public ImportAliasingDefUnit(DefUnitDataTuple dudt, RefModule targetModule, ImportAliasing impAlias) {
			super(dudt);
			setParent(impAlias);
			this.targetModule = targetModule;
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias; // Maybe should be ImportAlias
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			return targetModule.getTargetScope(moduleResolver);
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
	
	public final ImportAliasingDefUnit aliasDefUnit;
	
	public ImportAliasing(DefUnitDataTuple dudt, RefModule refModule, SourceRange sourceRange) {
		super(refModule, sourceRange);
		this.aliasDefUnit = new ImportAliasingDefUnit(dudt, refModule, this);
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
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return IteratorUtil.singletonIterator(aliasDefUnit);
	}
	
	@Override
	public String toStringAsElement() {
		return aliasDefUnit.toStringAsElement() + " = "+ moduleRef.toStringAsElement() ;
	}
	
}