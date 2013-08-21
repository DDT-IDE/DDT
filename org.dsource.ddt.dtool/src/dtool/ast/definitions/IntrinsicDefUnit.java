package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.SyntheticDefUnit;

/**
 * Base class for intrinsic elements. See {@link #isLanguageIntrinsic()} 
 */
public abstract class IntrinsicDefUnit extends SyntheticDefUnit implements INativeDefUnit {
	
	public IntrinsicDefUnit(String name) {
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
	
	@Override
	public boolean isLanguageIntrinsic() {
		return true;
	}
	
}