package dtool.ast.definitions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.INativeDefUnit;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public abstract class NativeDefUnit extends DefUnit implements INativeDefUnit, IScopeNode {
	
	/** A module like class, contained all native defunits. */
	public static class NativesScope implements IScope {
		
		public NativesScope() {
		}
		
		@Override
		public Iterator<? extends ASTNeoNode> getMembersIterator() {
			// TODO: put intrinsics here?
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		
		@Override
		public IScope getModuleScope() {
			return this;
		}
		
		@Override
		public List<IScope> getSuperScopes() {
			return null;
		}
		
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}
		
		@Override
		public String toString() {
			return "<natives>";
		}
		
		@Override
		public String toStringAsElement() {
			return toString();
		}
	}
	
	private static final class UndeterminedReference implements IDefUnitReference {
		@Override
		public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
			return null;
		}
		@Override
		public String toStringAsElement() {
			return "<unknown>";
		}
	}
	
	public static final NativesScope nativesScope = new NativesScope();
	//public static final DefUnit unknown = new NativesScope();
	public static final IDefUnitReference nullReference = new UndeterminedReference();
	
	public NativeDefUnit(String name) {
		super(name);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	
	@Override
	public abstract IScopeNode getMembersScope();
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	//public abstract IScope getSuperScope();
	
	@Override
	public IScope getModuleScope() {
		return nativesScope;
	}
	
}