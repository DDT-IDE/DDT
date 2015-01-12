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
package dtool.engine.analysis;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * A named element corresponding to a partial package namespace.
 * It does not represent the full package namespace, but just one of the elements containted in the namespace.
 * (the containted element must be a sub-package, or a module) 
 */
public class PackageNamespaceFragment extends AbstractNamedElement implements IScopeElement, IConcreteNamedElement {
	
	public static PackageNamespaceFragment createNamespaceFragments(String[] packages, INamedElement module, 
			ILanguageElement container) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createNamespaceFragments(defName, packages, module, container);
	}
	
	public static PackageNamespaceFragment createNamespaceFragments(String fqName, String[] packages, INamedElement module, 
			ILanguageElement container) {
		if(packages.length == 0) {
			return new PackageNamespaceFragment(fqName, module, container);
		} else {
			String childDefName = packages[0];
			String childFqName = fqName + "." + childDefName;
			packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PackageNamespaceFragment partialDefUnits = createNamespaceFragments(childFqName, packages, module, container);
			return new PackageNamespaceFragment(fqName, partialDefUnits, container);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected final String fqName;
	protected final INamedElement containedElement;
	
	public PackageNamespaceFragment(String fqName, INamedElement module, ILanguageElement container) {
		super(StringUtil.substringAfterLastMatch(fqName, "."), container);
		this.fqName = fqName;
		this.containedElement = assertNotNull(module);
	}
	
	public INamedElement getContainedElement() {
		return containedElement;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Package;
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fqName;
	}
	
	@Override
	public INamedElement getParentNamespace() {
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
	public ElementDoc resolveDDoc() {
		return null;
	}
	
	@Override
	public String toString() {
		return "PNamespaceFragment[" + getFullyQualifiedName() + "]";
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected final NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new AliasSemantics(this, pickedElement) {
			
			protected final NotAValueErrorElement notAValueErrorElement = new NotAValueErrorElement(element);
			
			@Override
			protected IConcreteNamedElement resolveAliasTarget(ISemanticContext context) {
				return PackageNamespaceFragment.this;
			}
			
			@Override
			public INamedElement resolveTypeForValueContext() {
				return notAValueErrorElement;
			};
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				search.evaluateScope(PackageNamespaceFragment.this);
			}
			
		};
	}
	
	/* -----------------  ----------------- */
	
	public Iterable<? extends ILanguageElement> getScopeNodeList() {
		return IteratorUtil.iterable(containedElement);
	}
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(getScopeNodeList(), true);
	}
	
}