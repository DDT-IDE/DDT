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
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;
import dtool.refmodel.NodeUtil;


public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	public IModelElement[] select(IModuleSource sourceUnit, int offset) {
		return select(sourceUnit, offset, 0);
	}
	
	// BM: don't quite understand what param i is used, if anything
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		
		DeeModuleDeclaration deeModule = DeeModelUtil.getParsedDeeModule(sourceModule);
		ASTNeoNode node = ASTNodeFinder.findElement(deeModule.neoModule, offset);
		
		if(node instanceof DefSymbol) {
			IType modelElement = getModelElement(((DefSymbol) node).getDefUnit());
			return new IModelElement[] { modelElement };
		}
		
		if(!(node instanceof Reference)) {
			return new IModelElement[0];
		}
		Reference ref = (Reference) node;
		
		Collection<DefUnit> defunits = ref.findTargetDefUnits(false);
		// We assume namespace Parent is the same
		if(defunits == null) {
			return new IModelElement[0];
		}
		
		ArrayList<IModelElement> list = new ArrayList<IModelElement>();
		int occurrenceCount = 1;
		for (DefUnit defUnit : defunits) {
			list.add(getModelElement(defUnit, occurrenceCount));
			occurrenceCount++;
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	
	protected IType getModelElement(DefUnit defUnit) {
		int parentOccurenceCount = 1; // This is a bug, but it will suffice for now...
		// instead, we need to uniquely identify the defunit in defunit hierarchy
		return getModelElement(defUnit, parentOccurenceCount);
	}
	
	protected IType getModelElement(DefUnit defUnit, int occurrenceCount) {
		DefUnit parentDefUnit = NodeUtil.getOuterDefUnit(defUnit);
		
		if(parentDefUnit == null) {
			return defUnit.getModuleNode().getModuleUnit().getType(defUnit.getName());
		} else {
			return getModelElement(parentDefUnit).getType(defUnit.getName(), occurrenceCount);
		}
		
	}
	
}