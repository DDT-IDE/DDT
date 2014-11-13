/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.engine.common.AbstractNamedElement;
import dtool.engine.common.DefElementCommon;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeProvider;
import dtool.resolver.ReferenceResolver;

/**
 * A named element corresponding to a partial package namespace.
 * It does not represent the full package namespace, but just one of the elements containted in the namespace.
 * (the containted element must be a sub-package, or a module) 
 */
public class PackageNamespace extends AbstractNamedElement implements IScopeProvider {
	
	protected final String fqName;
	protected final IDeeNamedElement containedElement;
	
	public PackageNamespace(String fqName, IDeeNamedElement module) {
		super(StringUtil.substringAfterLastMatch(fqName, "."));
		this.fqName = fqName;
		this.containedElement = assertNotNull(module);
	}
	
	public static PackageNamespace createPartialDefUnits(String[] packages, IDeeNamedElement module) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createPartialDefUnits(defName, packages, module);
	}
	
	public static PackageNamespace createPartialDefUnits(String fqName, String[] packages, IDeeNamedElement module) {
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
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fqName;
	}
	
	@Override
	public IDeeNamedElement getParentElement() {
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
	public String toString() {
		return getClass().getSimpleName() + ":" + getFullyQualifiedName() 
			+ "{" + containedElement.getFullyQualifiedName() + "}";
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		ReferenceResolver.findDefUnitInScope(this, search);
	}
	
	@Override
	public IDeeNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.returnError_ElementIsNotAValue(this);
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
	
}