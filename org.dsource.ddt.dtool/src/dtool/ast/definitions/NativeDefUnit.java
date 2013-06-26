package dtool.ast.definitions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNode;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.INamedScope;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.api.IModuleResolver;

public abstract class NativeDefUnit extends SyntheticDefUnit implements INativeDefUnit, IScopeNode {
	
	/** A module like class, contained all native defunits. */
	public static class NativesScope implements IScope, INamedScope {
		
		public NativesScope() {
		}
		
		@Override
		public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
			// TODO: put intrinsics here?
			return IteratorUtil.getEMPTY_ITERATOR();
		}
		
		@Override
		public INamedScope getModuleScope() {
			return this;
		}
		
		@Override
		public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
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
		public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
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
	public void accept0(IASTVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	
	@Override
	public abstract IScopeNode getMembersScope(IModuleResolver moduleResolver);
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}
	
	//public abstract IScope getSuperScope();
	
	@Override
	public INamedScope getModuleScope() {
		return nativesScope;
	}
	
}