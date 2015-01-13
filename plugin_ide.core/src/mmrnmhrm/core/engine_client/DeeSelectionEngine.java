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
package mmrnmhrm.core.engine_client;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.model_elements.DeeModelEngine;
import mmrnmhrm.core.search.SourceModuleFinder;

import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

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
		Path filePath = DToolClient_Bad.getFilePathOrNull(sourceModule);
		if(filePath == null) {
			return null;
		}
		
		Module deeModule = DToolClient.getDefaultModuleCache().getExistingParsedModuleNode(filePath);
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
		
		ISemanticContext moduleResolver = DToolClient_Bad.getResolverFor(filePath);
		Collection<INamedElement> defElements = ref.findTargetDefElements(moduleResolver);
		// We assume namespace Parent is the same
		if(defElements == null) {
			return new IModelElement[0];
		}
		
		ArrayList<IModelElement> list = new ArrayList<IModelElement>();
		for (INamedElement namedElement : defElements) {
			IMember modelElement = getModelElement(namedElement, sourceModule);
			if(modelElement != null) {
				list.add(modelElement);
			}
		}
		
		return ArrayUtil.createFrom(list, IModelElement.class);
	}
	
	protected IMember getModelElement(INamedElement namedElement, ISourceModule sourceModule) {
		INamedElementNode defUnit = namedElement.resolveUnderlyingNode();
		if(defUnit == null) {
			return null;
		}
		ModuleFullName moduleFullName = ModuleFullName.fromString(namedElement.getModuleFullName());
		if(moduleFullName == null) {
			return null;
		}
		try {
			IScriptProject scriptProject = sourceModule.getScriptProject();
			ISourceModule moduleUnit = SourceModuleFinder.findModuleUnit(scriptProject, moduleFullName);
			return DeeModelEngine.findCorrespondingModelElement(defUnit, moduleUnit);
		} catch (ModelException e) {
			return null;
		}
	}
	
}