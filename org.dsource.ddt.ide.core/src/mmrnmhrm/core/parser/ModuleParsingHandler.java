/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.ModelDeltaVisitor;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

import dtool.model.ModuleParseCache;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * Handler for parsing modules using the default {@link ModuleParseCache}.
 * The crucial aspect here is that this class maps the working copy contents of {@link ISourceModule}'s
 * to the working copy of the cache.
 */
public class ModuleParsingHandler {
	
	protected static final WorkingCopyListener wclistener = new WorkingCopyListener();
	
	public static void initialize() {
		DLTKCore.addElementChangedListener(wclistener, ElementChangedEvent.POST_CHANGE);
	}
	
	public static void dispose() {
		DLTKCore.removeElementChangedListener(wclistener);
	}
	
	
	protected static boolean isExternal(ISourceModule sourceModule) {
		return sourceModule.getResource() == null;
	}
	
	public static Path filePathFromSourceModule(ISourceModule sourceModule) {
		if(sourceModule.exists() == false) {
			DeeCore.logWarning("#getParsedDeeModule with module that does not exist: " + 
					sourceModule.getElementName());
		}
		
		Path filePath;
		
		IResource resource = sourceModule.getResource();
		if(resource == null) {
			filePath = EnvironmentPathUtils.getLocalPath(sourceModule.getPath()).toFile().toPath();
		} else {
			filePath = resource.getLocation().toFile().toPath();
		}
		return filePath;
	}
	
	public static ParsedModule parseModule(ISourceModule sourceModule) {
		Path filePath = filePathFromSourceModule(sourceModule);
		try {
			boolean isWorkingCopy = sourceModule.isWorkingCopy();
			if(!sourceModule.isConsistent()) {
				assertTrue(isWorkingCopy);
				String source = sourceModule.getSource();
				return ModuleParseCache.getDefault().getParsedModule(filePath, source);
			} else {
				// This method can be called during the scope of the discard/commit working copy method,
				// and as such the WorkingCopyListener has not yet had a chance to discard the cache working.
				// Because of that, we should check here as well if it's a WC, and discard it if so.
				if(!isWorkingCopy) {
					ModuleParseCache.getDefault().discardWorkingCopy(filePath);
				}
				return ModuleParseCache.getDefault().getParsedModule(filePath);
			}
		} catch (ParseSourceException | ModelException e) {
			DeeCore.logError(e);
			return null;
		}
	}
	
	public static ParsedModule parseModule(IModuleSource moduleSource) {
		String fileName = moduleSource.getFileName();
		
		if(moduleSource.getModelElement() instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) moduleSource.getModelElement();
			return parseModule(sourceModule);
		}
		
		String source = moduleSource.getSourceContents();
		return ModuleParseCache.getDefault().getParsedModule(fileName, source);
	}
	
	public static class WorkingCopyListener extends ModelDeltaVisitor {
		
		@Override
		protected void visitModule(IModelElementDelta moduleDelta, ISourceModule sourceModule) {
			if((moduleDelta.getFlags() & IModelElementDelta.F_PRIMARY_WORKING_COPY) != 0) {
				if(sourceModule.isWorkingCopy() == false) {
					Path filePath = filePathFromSourceModule(sourceModule);
					ModuleParseCache.getDefault().discardWorkingCopy(filePath);
				}
			}
		}
		
	}
	
}