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

import dtool.ast.ASTNodeTypes;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;

public class RefIdentifier extends CommonRefIdentifier implements ITemplateRefNode {
	
	public RefIdentifier(String identifier) {
		super(identifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IDENTIFIER;
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		// Check if we are the qualifer of a parent qualified ref
		if(getParent() instanceof CommonQualifiedReference) {
			CommonQualifiedReference parent = (CommonQualifiedReference) getParent();
			if(parent.getQualifiedName() == this) {
				// if so, then we must do qualified search (use root as the lookup scope)
				parent.performRefSearch(search);
				return;
			}
		}
		super.performRefSearch(search);
	}
	
}