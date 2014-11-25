/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine;

import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NullNamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.DeeLanguageIntrinsics;

public class NotFoundErrorElement extends AbstractElement implements IConcreteNamedElement {
	
	public static final String NOT_FOUND__NAME = "<not_found>";
	
	protected final Ddoc parseDDoc;
	
	public NotFoundErrorElement(IResolvable resolvable) {
		parseDDoc = DeeLanguageIntrinsics.parseDDoc("Could not resolve: " + resolvable.toStringAsCode());
	}
	
	@Override
	public String getName() {
		return NOT_FOUND__NAME;
	}
	
	@Override
	public String getExtendedName() {
		return getName();
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return getName(); ///*FIXME: BUG here*/
	}
	
	@Override
	public String getFullyQualifiedName() {
		return getName();
	}
	
	@Override
	public String getModuleFullyQualifiedName() {
		return getName();
	}
	
	@Override
	public ModuleFullName getModuleFullName() {
		return ModuleFullName.fromString("");
	}
	
	@Override
	public INamedElement getParentElement() {
		return null;
	}
	
	@Override
	public IConcreteNamedElement resolveConcreteElement(ISemanticContext sr) {
		return this;
	}
	
	protected final INamedElementSemantics nodeSemantics = new NullNamedElementSemantics();
	
	@Override
	public INamedElementSemantics getSemantics() {
		return nodeSemantics;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonScopeLookup search) {
		// Do nothing.
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(ISemanticContext mr) {
		// Do nothing.
		return null;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias; // TODO: add error archetype /* FIXME: */
	}
	
	@Override
	public INamedElementNode resolveUnderlyingNode() {
		return null;
	}
	
	@Override
	public Ddoc resolveDDoc() {
		return parseDDoc;
	}
	
}