package dtool.ast.declarations;

import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A synthetic DefUnit (not derived from source code), for partial package "definitions" of imports. 
 * This partial DefUnit holds one DefUnit DefUnit and represents only 
 * part of it's complete namespace.
 */
public abstract class PartialPackageDefUnit extends DefUnit implements IScopeNode {
	
	public PartialPackageDefUnit(String defName) {
		super(defName); // These are synthetic defUnits so they have no sourceRange.
	}
	
	public static PartialPackageDefUnit createPartialDefUnits(String[] packages, RefModule entModule, Module module) {
		String defName = packages[0];
		if(packages.length == 1 ) {
			return new PartialPackageDefUnitOfModule(defName, module, entModule);
		} else {
			String[] newNames = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PartialPackageDefUnit partialDefUnits = createPartialDefUnits(newNames, entModule, null);
			return new PartialPackageDefUnitOfPackage(defName, partialDefUnits);
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Package;
	}
	
	
	@Override
	public String toStringForHoverSignature() {
		return getName();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName();
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
	}
	
	@Override
	public Module getModuleScope() {
		return null;
	}
	
	@Override
	public IScopeNode getMembersScope() {
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
	
}