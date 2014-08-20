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

import java.util.Collection;

import dtool.ast.ASTNodeTypes;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.CommonDefUnitSearch;

// TODO: retire this element in favor of RefIdentifier?
public class RefImportSelection extends CommonRefIdentifier implements IImportSelectiveSelection {
	
	public ImportSelective impSel; // non-structural member
	
	public RefImportSelection(String identifier) {
		super(identifier);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_IMPORT_SELECTION;
	}
	
	public ImportSelective getImportSelectiveContainer() {
		return impSel;
	}
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		RefModule refMod = getImportSelectiveContainer().getModuleRef();
		Collection<INamedElement> targetModules = refMod.findTargetDefElements(search.getModuleResolver(), false);
		CommonQualifiedReference.resolveSearchInMultipleContainers(targetModules, search);
	}
	
}