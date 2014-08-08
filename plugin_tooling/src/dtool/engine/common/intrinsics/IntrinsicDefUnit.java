package dtool.engine.common.intrinsics;

import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;

/**
 * Base class for intrinsic elements. See {@link #isLanguageIntrinsic()} 
 */
public abstract class IntrinsicDefUnit implements INamedElement {
	
	protected final String name;
	protected final Ddoc doc;
	
	public IntrinsicDefUnit(String name, Ddoc doc) {
		this.name = name;
		this.doc = doc;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return true;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getExtendedName() {
		return name;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return name;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return null;
	}
	
	@Override
	public INamedElement getParentElement() {
		return null;
	}
	
	@Override
	public DefUnit resolveDefUnit() {
		return null;
	}
	
	@Override
	public final Ddoc resolveDDoc() {
		return doc;
	}
	
	@Override
	public String toString() {
		return "intrinsic_type#" + getName();
	}
	
}