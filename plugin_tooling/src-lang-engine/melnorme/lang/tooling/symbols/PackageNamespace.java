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
package melnorme.lang.tooling.symbols;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.engine.resolver.ConcreteElementResult;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics.NotAValueErrorElement;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.utilbox.collections.Collection2;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.EArcheType;

/**
 * A package namespace, parented on a given scope, implicitly created from import statements.
 */
public class PackageNamespace extends AbstractResolvedNamedElement implements IScopeElement, IConcreteNamedElement {
	
	public static PackageNamespace createNamespaceElement(String[] packages, INamedElement module) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createNamespaceFragments(defName, packages, module);
	}
	
	public static PackageNamespace createNamespaceFragments(String fqName, String[] packages, INamedElement module) {
		if(packages.length == 0) {
			return new PackageNamespace(fqName, module);
		} else {
			String childDefName = packages[0];
			String childFqName = fqName + "." + childDefName;
			packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PackageNamespace subPackage = createNamespaceFragments(childFqName, packages, module);
			return new PackageNamespace(fqName, subPackage);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected final String fqName;
	protected final SymbolTable namedElementsTable;
	
	protected PackageNamespaceSemantics packageNamespaceSemantics;
	
	public PackageNamespace(String fqName, INamedElement firstMember) {
		this(fqName, new SymbolTable());
		namedElementsTable.addSymbol(assertNotNull(firstMember));
	}
	
	protected PackageNamespace(String fqName, SymbolTable namedElementsTable) {
		super(StringUtil.substringAfterLastMatch(fqName, "."), null, false);
		this.fqName = fqName;
		this.namedElementsTable = assertNotNull(namedElementsTable);
	}
	
	public SymbolTable getNamespaceForModification() {
		assertTrue(isCompleted() == false);
		return namedElementsTable;
	}
	
	public Collection2<INamedElement> getContainedElements() {
		return namedElementsTable.getElements();
	}
	
	public PackageNamespace doCloneTree() {
		SymbolTable newSymbolTable = new SymbolTable();
		newSymbolTable.addSymbols(namedElementsTable);
		return new PackageNamespace(fqName, newSymbolTable);
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
	public INamedElementNode resolveUnderlyingNode() {
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
	
	public Iterable<? extends ILanguageElement> getScopeNodeList() {
		return IteratorUtil.iterable(namedElementsTable.getElements());
	}
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(getScopeNodeList(), true);
	}
	
	@Override
	public void setCompleted() {
		super.setCompleted();
		this.packageNamespaceSemantics = new PackageNamespaceSemantics();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected INamedElementSemanticData doGetSemantics() {
		return packageNamespaceSemantics;
	}
	
	public class PackageNamespaceSemantics implements INamedElementSemanticData {
		
		protected final NotAValueErrorElement notAValueErrorElement = new NotAValueErrorElement(PackageNamespace.this);
		protected final ConcreteElementResult concreteResult = new ConcreteElementResult(PackageNamespace.this);
		
		@Override
		public INamedElement resolveTypeForValueContext() {
			return notAValueErrorElement;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			search.evaluateScope(PackageNamespace.this);
		}
		
		@Override
		public ConcreteElementResult resolveConcreteElement() {
			return concreteResult;
		}
		
	}
	
}