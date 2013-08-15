package dtool.ast.definitions;

import melnorme.utilbox.core.Assert;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.IScopeProvider;

public abstract class NativeDefUnit extends SyntheticDefUnit implements INativeDefUnit, IScopeNode {
	
	/** A module like class, contained all native defunits. */
	public static class NativesScope implements IScopeProvider {
		
		public NativesScope() {
		}
		
		@Override
		public String toString() {
			return "<natives>";
		}
		
		@Override
		public void resolveSearchInScope(CommonDefUnitSearch search) {
			// TODO Auto-generated method stub
		}
		
	}
	
	public static final NativesScope nativesScope = new NativesScope();
	
	public NativeDefUnit(String name) {
		super(name);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		Assert.fail("Intrinsics do not suppport accept.");
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		// TODO Auto-generated method stub
	}
	
}