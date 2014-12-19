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
package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonQualifiedReference extends NamedReference implements ITemplateRefNode {
	
	public final RefIdentifier qualifiedId;
	
	public CommonQualifiedReference(RefIdentifier qualifiedId) {
		this.qualifiedId = parentize(assertNotNull(qualifiedId));
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedId;
	}
	
	@Override
	public String getCoreReferenceName() {
		return qualifiedId.getCoreReferenceName();
	}
	
	public abstract int getDotOffset();
	
	public abstract INamedElement resolveRootNameElement(ISemanticContext moduleResolver);
	
	@Override
	public void performNameLookup(CommonScopeLookup search) {
		performQualifiedRefSearch(search);
	}
	
	public void performQualifiedRefSearch(CommonScopeLookup search) {
		INamedElement rootElement = resolveRootNameElement(search.modResolver);
		// TODO: create new search object here.
		search.evaluateInMembersScope(rootElement);
	}
	
}