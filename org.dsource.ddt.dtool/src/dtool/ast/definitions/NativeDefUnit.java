package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeProvider;
import dtool.resolver.ReferenceResolver;
import dtool.util.ArrayView;

public abstract class NativeDefUnit extends SyntheticDefUnit implements INativeDefUnit {
	
	/** A module like class, contained all native defunits. */
	public static class NativesScope implements IScopeProvider {
		
		public final ArrayView<SyntheticDefUnit> intrinsics;
		
		public NativesScope() {
			NativeDefUnit intrinsicPrimitive = new NativeDefUnit("void") {
				@Override
				public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
				}
			};
			ArrayList<SyntheticDefUnit> intrincsList = new ArrayList<>();
			intrincsList.add(intrinsicPrimitive);
			
			SyntheticDefUnit[] createFrom = ArrayUtil.createFrom(intrincsList, SyntheticDefUnit.class);
			intrinsics = ArrayView.create(createFrom);
		}
		
		@Override
		public void resolveSearchInScope(CommonDefUnitSearch search) {
			ReferenceResolver.findInNodeList(search, intrinsics, false);
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
		assertFail();
	}
	
}