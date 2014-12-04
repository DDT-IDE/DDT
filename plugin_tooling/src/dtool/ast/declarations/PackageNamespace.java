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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.NotAValueErrorElement;
import melnorme.lang.tooling.engine.NotFoundErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

/**
 * A named element corresponding to a partial package namespace.
 * It does not represent the full package namespace, but just one of the elements containted in the namespace.
 * (the containted element must be a sub-package, or a module) 
 */
public class PackageNamespace extends AbstractNamedElement implements IScopeElement {
	
	public static PackageNamespace createPartialDefUnits(String[] packages, INamedElement module, 
			ISemanticElement container) {
		String defName = packages[0];
		packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
		return createPartialDefUnits(defName, packages, module, container);
	}
	
	public static PackageNamespace createPartialDefUnits(String fqName, String[] packages, INamedElement module, 
			ISemanticElement container) {
		if(packages.length == 0) {
			return new PackageNamespace(fqName, module, container);
		} else {
			String childDefName = packages[0];
			String childFqName = fqName + "." + childDefName;
			packages = ArrayUtil.copyOfRange(packages, 1, packages.length);
			PackageNamespace partialDefUnits = createPartialDefUnits(childFqName, packages, module, container);
			return new PackageNamespace(fqName, partialDefUnits, container);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected final String fqName;
	protected final INamedElement containedElement;
	
	public PackageNamespace(String fqName, INamedElement module, ISemanticElement container) {
		super(StringUtil.substringAfterLastMatch(fqName, "."), container);
		this.fqName = fqName;
		this.containedElement = assertNotNull(module);
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
	public INamedElement getParentNamedElement() {
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
		return getClass().getSimpleName() + ":" + getFullyQualifiedName() 
			+ "{" + containedElement.getFullyQualifiedName() + "}";
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected final INamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new AliasSemantics(this, pickedElement) {

			protected final NotFoundErrorElement errorElement = new NotFoundErrorElement(PackageNamespace.this, null);
			
			protected final NotAValueErrorElement notAValueErrorElement = new NotAValueErrorElement(element, 
				null /*FIXME: BUG here*/);
			
			@Override
			public INamedElement resolveTypeForValueContext() {
				return notAValueErrorElement;
			};
			
			@Override
			protected IConcreteNamedElement doResolveConcreteElement(ISemanticContext context) {
				return errorElement;
			}
			
			@Override
			protected IResolvable getAliasTarget() {
				throw assertUnreachable();
			};
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				search.evaluateScope(PackageNamespace.this);
			}
			
		};
	}
	
	@Override
	public void resolveSearchInScope(CommonScopeLookup search) {
		if(containedElement.getArcheType() == EArcheType.Module) {
			INamedElementNode resolvedDefUnit = containedElement.resolveUnderlyingNode();
			if(resolvedDefUnit == null) {
				// Note that we dont use resolvedDefUnit for evaluateNodeForSearch,
				// this means modules with mismatched names will match again the import name (file name),
				// instead of the module declaration name
				return;
			}
		}
		search.evaluateNamedElementForSearch(containedElement);
	}
	
}