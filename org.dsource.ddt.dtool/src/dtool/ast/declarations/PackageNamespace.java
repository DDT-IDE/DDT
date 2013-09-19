package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeProvider;
import dtool.resolver.ReferenceResolver;

/**
 * A named element corresponding to a partial package namespace.
 * It does not represent the full package namespace, but just one of the elements containted in the namespace.
 * (the containted element must be a sub-package, or a module) 
 */
public class PackageNamespace implements INamedElement, IScopeProvider {
	
	protected final String name;
	protected final String fqName;
	protected final INamedElement containedElement;
	
	public PackageNamespace(String fqName, INamedElement module) {
		this.fqName = fqName;
		this.name = StringUtil.substringAfterLastMatch(fqName, ".");
		this.containedElement = assertNotNull(module);
	}
	
	public static PackageNamespace createPartialDefUnits(String[] packages, INamedElement module) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createPartialDefUnits(defName, packages, module);
	}
	
	public static PackageNamespace createPartialDefUnits(String fqName, String[] packages, INamedElement module) {
		if(packages.length == 0) {
			return new PackageNamespace(fqName, module);
		} else {
			String childDefName = packages[0];
			String childFqName = fqName + "." + childDefName;
			packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PackageNamespace partialDefUnits = createPartialDefUnits(childFqName, packages, module);
			return new PackageNamespace(fqName, partialDefUnits);
		}
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Package;
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
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fqName;
	}
	
	@Override
	public INamedElement getParentElement() {
		return null;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return null;
	}
	
	@Override
	public DefUnit resolveDefUnit() {
		return null;
	}
	
	@Override
	public Ddoc resolveDDoc() {
		return null;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInScope(this, search);
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		if(containedElement.getArcheType() == EArcheType.Module) {
			DefUnit resolvedDefUnit = containedElement.resolveDefUnit();
			if(resolvedDefUnit == null) {
				// Note that we dont use resolvedDefUnit for evaluateNodeForSearch,
				// this means modules with mismatched names will match again the import name (file name),
				// instead of the module declaration name
				return;
			}
		}
		ReferenceResolver.evaluateNamedElementForSearch(search, containedElement);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + getFullyQualifiedName() 
			+ "{" + containedElement.getFullyQualifiedName() + "}";
	}
	
}