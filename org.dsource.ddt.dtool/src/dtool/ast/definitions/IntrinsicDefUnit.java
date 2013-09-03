package dtool.ast.definitions;

import descent.core.ddoc.Ddoc;
import dtool.resolver.CommonDefUnitSearch;

/**
 * Base class for intrinsic elements. See {@link #isLanguageIntrinsic()} 
 */
public abstract class IntrinsicDefUnit implements INamedElement {
	
	protected String name;
	
	public IntrinsicDefUnit(String name) {
		this.name = name;
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
	public DefUnit asDefUnit() {
		return null;
	}

	@Override
	public DefUnit resolveDefUnit() {
		return null;
	}
	
	@Override
	public abstract Ddoc resolveDDoc();
	
	@Override
	public abstract void resolveSearchInMembersScope(CommonDefUnitSearch search);
	
}