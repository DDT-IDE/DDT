package dtool.ast.declarations;

import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A kinda fake DefUnit, for partial package "definitions" of imports. 
 * This partial DefUnit holds one DefUnit DefUnit and represents only 
 * part of it's complete namespace.
 */
public abstract class PartialPackageDefUnit extends DefUnit implements IScopeNode {
	
	public PartialPackageDefUnit(TokenInfo name) {
		super(null, name, null);
	}
	
	public static PartialPackageDefUnit createPartialDefUnits(
			String[] packages, RefModule entModule, Module module) {
		TokenInfo defname = new TokenInfo(packages[0]);
		if(packages.length == 1 ) {
			PartialPackageDefUnitOfModule packageDefUnit =  new PartialPackageDefUnitOfModule(defname);
			packageDefUnit.module = module;
			packageDefUnit.moduleRef = entModule;
			return packageDefUnit;
		} else {
			PartialPackageDefUnitOfPackage packageDefUnit =  new PartialPackageDefUnitOfPackage(defname);
			String[] newNames = ArrayUtil.copyOfRange(packages, 1, packages.length);
			packageDefUnit.child = createPartialDefUnits(newNames, entModule, null);
			return packageDefUnit;
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