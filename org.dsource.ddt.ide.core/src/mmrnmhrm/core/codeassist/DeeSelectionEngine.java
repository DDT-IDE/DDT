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
import mmrnmhrm.core.model_elements.DeeModelEngine;
import mmrnmhrm.core.parser.DeeModuleParsingUtil;

import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;

/**
 * XXX: what is the exact contract of this class. Do returned model elements have to exist?
 * Current known uses:
 * - Initializing text for ScripSearchPage
 * - Picking element for type hierarchy action
 */
public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	public IModelElement[] select(IModuleSource sourceUnit, int offset) {
		return select(sourceUnit, offset, 0);
	}
	
	// BM: param i is something like endOffset-1, due to weird API
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		
		Module deeModule = DeeModuleParsingUtil.getParsedDeeModule(sourceModule);
		ASTNode node = ASTNodeFinder.findElement(deeModule, offset);
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getDefUnit();
			try {
				IMember modelElement = DeeModelEngine.findCorrespondingModelElement(defUnit, sourceModule);
				return modelElement == null ? null : new IModelElement[] { modelElement };
			} catch (ModelException e) {
				return null;
			}
		}
		
		if(!(node instanceof Reference)) {
			return new IModelElement[0];
		}
		Reference ref = (Reference) node;
		
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule);
		Collection<DefUnit> defunits = ref.findTargetDefUnits(moduleResolver, false);
		// We assume namespace Parent is the same
		if(defunits == null) {
			return new IModelElement[0];
		}
		
		ArrayList<IModelElement> list = new ArrayList<IModelElement>();
		for (DefUnit defUnit : defunits) {
			IMember modelElement = getModelElement(defUnit, moduleResolver, sourceModule);
			if(modelElement != null) {
				list.add(modelElement);
			}
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	protected IMember getModelElement(DefUnit defUnit, DeeProjectModuleResolver mr, ISourceModule sourceModule) {
		Module module = defUnit.getModuleNode();
		if(module == null) {
			return null;
		}
		try {
			ISourceModule moduleUnit = mr.findModuleUnit(module, sourceModule);
			return DeeModelEngine.findCorrespondingModelElement(defUnit, moduleUnit);
		} catch (ModelException e) {
			return null;
		}
	}
	
}