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

import java.util.Collection;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ConcreteElementSemantics;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.SymbolTable;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * A package namespace, parented on a given scope, implicitly created from import statements.
 */
public class PackageNamespace extends AbstractNamedElement implements IScopeElement, IConcreteNamedElement {
	
	public static PackageNamespace createNamespaceFragments(String[] packages, INamedElement module, 
			ILanguageElement container) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createNamespaceFragments(defName, packages, module, container);
	}
	
	public static PackageNamespace createNamespaceFragments(String fqName, String[] packages, INamedElement module, 
			ILanguageElement container) {
		if(packages.length == 0) {
			return new PackageNamespace(fqName, container, module);
		} else {
			String childDefName = packages[0];
			String childFqName = fqName + "." + childDefName;
			packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PackageNamespace subPackage = createNamespaceFragments(childFqName, packages, module, container);
			return new PackageNamespace(fqName, container, subPackage);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected final String fqName;
	protected final SymbolTable namedElementsTable;
	
	public PackageNamespace(String fqName, ILanguageElement owner, INamedElement firstMember) {
		super(StringUtil.substringAfterLastMatch(fqName, "."), null, owner);
		assertNotNull(firstMember);
		this.fqName = fqName;
		this.namedElementsTable = new SymbolTable();
		this.namedElementsTable.addSymbol(firstMember);
	}
	
	public SymbolTable getNamespace() {
		return namedElementsTable;
	}
	
	public Collection<INamedElement> getContainedElements() {
		return namedElementsTable.getElements();
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
	public String getModuleFullName() {
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
		return "PNamespace[" + getFullyQualifiedName() + "]";
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected final NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ConcreteElementSemantics(this, pickedElement) {
			
			protected final NotAValueErrorElement notAValueErrorElement = new NotAValueErrorElement(element);
			
			@Override
			public INamedElement resolveTypeForValueContext() {
				return notAValueErrorElement;
			};
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				search.evaluateScope(PackageNamespace.this);
			}
			
		};
	}
	
	/* -----------------  ----------------- */
	
	public Iterable<? extends ILanguageElement> getScopeNodeList() {
		return IteratorUtil.iterable(namedElementsTable.getElements());
	}
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(getScopeNodeList(), true);
	}
	
}