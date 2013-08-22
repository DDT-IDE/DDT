package mmrnmhrm.core.model_elements;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * A descriptor of a DefUnit that has enough info do determine an icon
 */
public class DefElementDescriptor {
	
	public final int modifierFlags;
	protected final EArcheType archeType;
	
	public DefElementDescriptor(int elementFlags) {
		this.modifierFlags = elementFlags & ~DeeModelConstants.FLAGMASK_KIND;
		this.archeType = DeeModelElementUtil.elementFlagsToArcheType(elementFlags);
	}
	
	public DefElementDescriptor(DefUnit defUnit) {
		this.modifierFlags = DeeSourceElementProvider.modifierFlagsFromDefUnit(defUnit);
		this.archeType = defUnit.getArcheType();
	}
	
	public EArcheType getArcheType() {
		return archeType;
	}
	
	public boolean isNative() {
		return (modifierFlags & DeeModelConstants.FLAG_NATIVE) != 0;
	}
	
}