package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ResolverUtil;
import dtool.resolver.ResolverUtil.ModuleNameDescriptor;
import dtool.resolver.api.IModuleResolver;

public class ModuleProxy extends SyntheticDefUnit {
	
	protected final IModuleResolver moduleResolver;
	protected String fqModuleName;
	
	public ModuleProxy(String fqModuleName, IModuleResolver moduleResolver) {
		super(StringUtil.substringAfterLastMatch(fqModuleName, "."));
		assertTrue(getName().trim().isEmpty() == false);
		this.fqModuleName = fqModuleName;
		this.moduleResolver = moduleResolver;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		assertFail();
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
	public Module resolveDefUnit() {
		ModuleNameDescriptor nameDescriptor = ResolverUtil.getNameDescriptor(getModuleFullyQualifiedName());
		try {
			return moduleResolver.findModule(nameDescriptor.packages, nameDescriptor.moduleName);
		} catch(Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
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