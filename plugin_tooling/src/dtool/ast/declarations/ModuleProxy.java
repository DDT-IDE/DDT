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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public class ModuleProxy extends AbstractNamedElement {
	
	protected final IModuleResolver moduleResolver;
	protected final String fqModuleName;
	
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
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return fqModuleName;
	}
	
	@Override
	public String toString() {
		return "module["+getModuleFullyQualifiedName()+"]";
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
	public DefUnit resolveUnderlyingNode() {
		INamedElement module = ReferenceResolver.findModuleUnchecked(moduleResolver, getModuleFullyQualifiedName());
		if(module instanceof DefUnit) {
			return (DefUnit) module;
		}
		return null; 
	}
	
	@Override
	public Ddoc resolveDDoc() {
		DefUnit resolvedModule = resolveUnderlyingNode();
		if(resolvedModule != null) {
			return resolveUnderlyingNode().getDDoc();
		}
		return null;
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
			DefUnit resolvedModule = resolveUnderlyingNode();
			if(resolvedModule != null) {
				resolvedModule.resolveSearchInMembersScope(search);
			}
		}
		
	};
	
}