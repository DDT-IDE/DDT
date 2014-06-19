package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.ResolverUtil;
import dtool.resolver.ResolverUtil.ModuleNameDescriptor;

public class ModuleProxy implements INamedElement {
	
	protected final IModuleResolver moduleResolver;
	protected final String fqModuleName;
	protected final String moduleName;
	
	public ModuleProxy(String fqModuleName, IModuleResolver moduleResolver) {
		this.moduleName = StringUtil.substringAfterLastMatch(fqModuleName, "."); 
		assertTrue(getName().trim().isEmpty() == false);
		this.fqModuleName = fqModuleName;
		this.moduleResolver = moduleResolver;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public String getName() {
		return moduleName;
	}
	
	@Override
	public String getExtendedName() {
		return moduleName;
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return fqModuleName;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fqModuleName;
	}
	
	@Override
	public INamedElement getParentElement() {
		return null;
	}
	
	@Override
	public Module resolveDefUnit() {
		ModuleNameDescriptor nameDescriptor = ResolverUtil.getNameDescriptor(getModuleFullyQualifiedName());
		return ReferenceResolver.findModuleUnchecked(moduleResolver, 
				nameDescriptor.packages, nameDescriptor.moduleName);
	}
	
	@Override
	public Ddoc resolveDDoc() {
		DefUnit resolvedModule = resolveDefUnit();
		if(resolvedModule != null) {
			return resolveDefUnit().getDDoc();
		}
		return null;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		DefUnit resolvedModule = resolveDefUnit();
		if(resolvedModule != null) {
			resolvedModule.resolveSearchInMembersScope(search);
		}
	}
	
}