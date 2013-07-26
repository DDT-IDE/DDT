package mmrnmhrm.core.model_elements;

import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * A descriptor of a DefUnit enough to provide an icon
 */
public class DefElementDescriptor {
	
	public final EArcheType archeType;
	public final int modifierFlags;
	
	public DefElementDescriptor(int elementFlags) {
		this.archeType = DeeModelElementUtil.elementFlagsToArcheType(elementFlags);
		this.modifierFlags = elementFlags & ~DeeModelConstants.FLAGMASK_KIND;
	}
	
	public DefElementDescriptor(DefUnit defUnit) {
		this.archeType = defUnit.getArcheType();
		this.modifierFlags = defUnit instanceof CommonDefinition ? 
			DeeSourceElementProvider.getCommonDefinitionModifiersInfo((CommonDefinition) defUnit) : 0;
	}
	
}