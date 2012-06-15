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

import org.dsource.ddt.ide.core.model.DeeModuleParsingUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;

/**
 * XXX: what is the exact contract of this class. Do returned model elements have to exists?
 * Current known uses:
 * - Initializing text for ScripSearchPage
 * - Picking element for type hierarchy action
 */
public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	public static final boolean ELEMENT_DDOC_SELECTION__INCLUSIVE_END = false;
	
	public IModelElement[] select(IModuleSource sourceUnit, int offset) {
		return select(sourceUnit, offset, 0);
	}
	
	// BM: don't quite understand what param i is used, if anything
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		
		DeeModuleDeclaration deeModule = DeeModuleParsingUtil.getParsedDeeModule(sourceModule);
		ASTNeoNode node = ASTNodeFinder.findElement(deeModule.neoModule, offset, 
				ELEMENT_DDOC_SELECTION__INCLUSIVE_END);
		
		if(node instanceof DefSymbol) {
			DefUnit defUnit = ((DefSymbol) node).getDefUnit();
			IMember modelElement = getModelElement(defUnit, defUnit.getModuleNode(), sourceModule.getScriptProject());
			return modelElement == null ? null : new IModelElement[] { modelElement };
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
		for (DefUnit defUnit : defunits) {
			IMember modelElement = getModelElement(defUnit, defUnit.getModuleNode(), sourceModule.getScriptProject());
			if(modelElement != null) {
				list.add(modelElement);
			}
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	protected IMember getModelElement(DefUnit defUnit, Module module, IScriptProject scriptProject) {
		DeeProjectModuleResolver mr = new DeeProjectModuleResolver(scriptProject);
		try {
			ISourceModule moduleUnit = mr.findModuleUnit(module);
			return DeeModelEngine.findCorrespondingModelElement(defUnit, moduleUnit);
		} catch (ModelException e) {
			return null;
		}
	}
	
}