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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.AliasSemantics.TypeAliasSemantics;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.AbstractNamedElement;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.Module;

/**
 * This class is an alias to an actual module element.
 * Used for performance reasons, to be able to use the proxy in some situations (Content Assist for example)
 * without forcing the referred module to be parsed.
 */
public class ModuleProxy extends AbstractNamedElement {
	
	protected final ISemanticContext context;
	protected final String fullModuleName;
	
	public ModuleProxy(String fullModuleName, ISemanticContext moduleResolver, ISemanticElement parent) {
		this(fullModuleName, moduleResolver, false, parent);
	}
	
	public ModuleProxy(String fullModuleName, ISemanticContext moduleResolver, boolean useFullName, 
			ISemanticElement parent) {
		super(getEffectiveModuleName(fullModuleName, useFullName), parent);
		assertTrue(getName().trim().isEmpty() == false);
		this.fullModuleName = fullModuleName;
		this.context = moduleResolver;
	}
	
	protected static String getEffectiveModuleName(String fullModuleName, boolean usefullName) {
		return usefullName ? fullModuleName : StringUtil.substringAfterLastMatch(fullModuleName, ".");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return fullModuleName;
	}
	
	@Override
	public String toString() {
		return "module["+getModuleFullyQualifiedName()+"]";
	}
	
	@Override
	public String getFullyQualifiedName() {
		return fullModuleName;
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return null;
	}
	
	@Override
	public Module resolveUnderlyingNode() {
		INamedElement module = ResolvableSemantics.findModuleUnchecked(context, getModuleFullyQualifiedName());
		if(module instanceof Module) {
			return (Module) module;
		}
		return null; 
	}
	
	@Override
	public ElementDoc resolveDDoc() {
		DefUnit resolvedModule = resolveUnderlyingNode();
		if(resolvedModule != null) {
			return resolveUnderlyingNode().getDDoc();
		}
		return null;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ISemanticContext getContextForThisElement(ISemanticContext parentContext) {
		return context;
	}
	
	@Override
	protected final INamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new TypeAliasSemantics(this, pickedElement) {
			
			{
				assertTrue(context == ModuleProxy.this.context);
			}
			
			@Override
			protected IConcreteNamedElement doResolveConcreteElement(ISemanticContext context) {
				return resolveUnderlyingNode();
			}
			
			@Override
			protected IResolvable getAliasTarget() {
				throw assertUnreachable();
			}
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				INamedElement resolvedModule = resolveUnderlyingNode();
				if(resolvedModule != null) {
					resolvedModule.resolveSearchInMembersScope(search);
				}
			}
			
		};
	}
	
}