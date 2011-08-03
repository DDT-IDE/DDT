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
import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;


public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	public static final boolean ELEMENT_DDOC_SELECTION__INCLUSIVE_END = false;
	
	public IModelElement[] select(IModuleSource sourceUnit, int offset) {
		return select(sourceUnit, offset, 0);
	}
	
	// BM: don't quite understand what param i is used, if anything
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		
		DeeModuleDeclaration deeModule = DeeModelUtil.getParsedDeeModule(sourceModule);
		ASTNeoNode node = ASTNodeFinder.findNeoElement(deeModule.neoModule, offset, 
				ELEMENT_DDOC_SELECTION__INCLUSIVE_END);
		
		if(node instanceof DefSymbol) {
			IMember modelElement = getModelElement(((DefSymbol) node).getDefUnit(), sourceModule);
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
			IMember modelElement = getModelElement(defUnit, defUnit.getModuleNode().getModuleUnit());
			if(modelElement != null) {
				list.add(modelElement);
			}
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	
	protected IMember getModelElement(DefUnit defUnit, ISourceModule sourceModule) {
		try {
			return DeeModelEngine.findCorrespondingModelElement(defUnit, sourceModule);
		} catch(ModelException e) {
			return null;
		}
	}
	
}