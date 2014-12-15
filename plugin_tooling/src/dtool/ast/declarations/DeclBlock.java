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

import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeList;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.definitions.DefinitionAggregate.IAggregateBody;
import dtool.ast.definitions.DefinitionClass;

public class DeclBlock extends NodeList<ASTNode> implements IAggregateBody, IScopeElement.IExtendedScopeElement {
	
	public DeclBlock(ArrayView<ASTNode> nodes) {
		super(nodes);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECL_BLOCK;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("{\n", nodes, "\n", "\n}\n");
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public boolean allowsForwardReferences() {
		return true;
	}
	
	@Override
	public void resolveLookupInSuperScopes(CommonScopeLookup lookup) {
		// TODO: a more typesafe alternative to this check
		if(getParent() instanceof DefinitionClass) {
			DefinitionClass definitionClass = (DefinitionClass) getParent();
			definitionClass.getSemantics(lookup.modResolver).resolveSearchInSuperScopes(lookup);
		}
	}
	
}