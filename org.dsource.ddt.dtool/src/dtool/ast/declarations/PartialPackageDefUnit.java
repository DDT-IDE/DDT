package dtool.ast.declarations;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;
import dtool.resolver.ReferenceResolver;

/**
 * A synthetic DefUnit (not derived from source code), for partial package "definitions" of imports. 
 * This partial DefUnit holds one DefUnit DefUnit and represents only 
 * part of it's complete namespace.
 */
public abstract class PartialPackageDefUnit extends SyntheticDefUnit implements IScopeNode {
	
	public PartialPackageDefUnit(String defName) {
		super(defName);
	}
	
	public static PartialPackageDefUnit createPartialDefUnits(String[] packages, RefModule refModule, Module module) {
		String defName = packages[0];
		if(packages.length == 1 ) {
			return new PartialPackageDefUnitOfModule(defName, module, refModule);
		} else {
			String[] newNames = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PartialPackageDefUnit partialDefUnits = createPartialDefUnits(newNames, refModule, module);
			return new PartialPackageDefUnitOfPackage(defName, partialDefUnits);
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Package;
	}
	
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return null;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		ReferenceResolver.resolveSearchInScope(search, this);
	}
	
}