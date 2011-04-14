/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.codeassist;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.ArrayUtil;

import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;
import dtool.refmodel.NodeUtil;


public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	public IModelElement[] select(IModuleSource sourceUnit, int offset) {
		return select(sourceUnit, offset);
	}
	
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		
		DeeModuleDeclaration deeModule = DeeModelUtil.getParsedDeeModule(sourceModule);
		ASTNeoNode node = ASTNodeFinder.findElement(deeModule.neoModule, offset);
		if(!(node instanceof Reference)) {
			//return new IModelElement[0];
		}
		Reference ref = (Reference) node;
		
		Collection<DefUnit> defunits = ref.findTargetDefUnits(true);
		
		ArrayList<IModelElement> list = new ArrayList<IModelElement>();
		for (DefUnit defUnit : defunits) {
			list.add(getModelElement(defUnit));
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	protected IType getModelElement(DefUnit defUnit) {
		DefUnit parentDefUnit = NodeUtil.getOuterDefUnit(defUnit);
		
		if(parentDefUnit == null) {
			return defUnit.getModuleNode().getModuleUnit().getType(defUnit.getName());
		} else {
			return getModelElement(parentDefUnit).getType(defUnit.getName(), 0);
		}

	}
	
}