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
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

/**
 * A named element corresponding to a partial package namespace.
 * It does not represent the full package namespace, but just one of the elements containted in the namespace.
 * (the containted element must be a sub-package, or a module) 
 */
public class PackageNamespace extends AbstractNamedElement implements IScopeProvider {
	
	protected final String fqName;
	protected final INamedElement containedElement;
	
	public PackageNamespace(String fqName, INamedElement module) {
		super(StringUtil.substringAfterLastMatch(fqName, "."));
		this.fqName = fqName;
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
	public DefUnit resolveUnderlyingNode() {
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
	
	/* -----------------  ----------------- */
	
	@Override
	public final INamedElementSemantics getNodeSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public IConcreteNamedElement resolveConcreteElement() {
			return null; /*FIXME: BUG here TODO*/
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			ReferenceResolver.findDefUnitInScope(PackageNamespace.this, search);
		}
		
	};
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		if(containedElement.getArcheType() == EArcheType.Module) {
			INamedElementNode resolvedDefUnit = containedElement.resolveUnderlyingNode();
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