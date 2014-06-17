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
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import melnorme.utilbox.misc.MiscUtil.InvalidPathExceptionX;
import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.ModelDeltaVisitor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
import dtool.ast.util.ReferenceSwitchHelper;
import dtool.ddoc.TextUI;
import dtool.model.ModuleParseCache;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.project.IModuleResolver;
import dtool.project.NullModuleResolver;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;

/**
 * Handle communication with DToolServer.
 */
public class DToolClient {
	
	public static DToolClient getDefault() {
		return DeeCore.getDToolClient();
	}
	
	public static DToolClient initializeNew() {
		return new DToolClient();
	}
	
	protected final WorkingCopyListener wclistener = new WorkingCopyListener();
	
	protected final ModuleParseCache moduleParseCache = new ModuleParseCache();
	
	public DToolClient() {
		DLTKCore.addElementChangedListener(wclistener, ElementChangedEvent.POST_CHANGE);
	}
	
	public void shutdown() {
		DLTKCore.removeElementChangedListener(wclistener);
	}
	
	public ModuleParseCache getModuleParseCache() {
		return moduleParseCache;
	}
	
	public static Path validateFilePath(ISourceModule input) {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			DeeCore.logError("Invalid path from DLTK: " + e);
			return null;
		}
	}
	public static Path validateFilePath(IModuleSource input) {
		try {
			return DLTKUtils.getFilePath(input);
		} catch (InvalidPathExceptionX e) {
			DeeCore.logError("Invalid path from DLTK: " + e);
			return null;
		}
	}
	
	public ParsedModule getParsedModuleOrNull(ISourceModule input) {
		Path filePath = validateFilePath(input);
		return filePath == null ? null : getParsedModuleOrNull(filePath);
	}
	public ParsedModule getParsedModuleOrNull(Path filePath) {
		try {
			return getModuleParseCache().getParsedModule(filePath);
		} catch (ParseSourceException e) {
			// Most likely a file IO error ocurred. 
			DeeCore.logWarning("Error in getParsedModule", e);
			return null;
		}
	}
	
	public ParsedModule getParsedModuleOrNull(IModuleSource input) {
		Path filePath = validateFilePath(input);
		if(filePath == null) { 
			return null;
		}
		if(!filePath.isAbsolute()) {
			// If it's a special path, there will not be an underlying file, so we must retrieve source directly.
			return getModuleParseCache().getParsedModule(filePath, input.getSourceContents());
		}
		return getParsedModuleOrNull(filePath);
	}
	
	public ParsedModule getParsedModuleOrNull_updateSource(IModuleSource input) {
		Path filePath = validateFilePath(input);
		if(filePath == null) { 
			return null;
		}
		return getModuleParseCache().getParsedModule(filePath, input.getSourceContents());
	}
	
	public Module getModuleNodeOrNull(ISourceModule input) {
		ParsedModule parseModule = getParsedModuleOrNull(input);
		return parseModule == null ? null : parseModule.module;
	}
	
	
	public ParsedModule getExistingParsedModuleOrNull(ISourceModule input) {
		Path filePath = validateFilePath(input);
		return filePath == null ? null : getModuleParseCache().getExistingParsedModule(filePath);
	}
	public ParsedModule getExistingParsedModuleOrNull(IModuleSource input) {
		Path filePath = validateFilePath(input);
		return filePath == null ? null : getModuleParseCache().getExistingParsedModule(filePath);
	}
	
	public Module getExistingModuleNodeOrNull(ISourceModule input) {
		ParsedModule parsedModule = getExistingParsedModuleOrNull(input);
		return parsedModule == null ? null : parsedModule.module;
	}
	public Module getExistingModuleNodeOrNull(IModuleSource input) {
		ParsedModule parsedModule = getExistingParsedModuleOrNull(input);
		return parsedModule == null ? null : parsedModule.module;
	}
	
	/* ----------------- working copy handling ----------------- */
	
	public ParsedModule getParsedModuleOrNull_fromBuildStructure(IModuleSource input) {
		ISourceModule sourceModule = tryCast(input, ISourceModule.class);
		if(sourceModule != null) {
			getParsedModuleOrNull_fromWorkingCopy(sourceModule);
		}
		return getParsedModuleOrNull(input);
	}
	
	public ParsedModule getParsedModuleOrNull_fromWorkingCopy(ISourceModule sourceModule) {
		Path filePath = validateFilePath(sourceModule);
		if(filePath == null) 
			return null;
		
		try {
			boolean isWorkingCopy = sourceModule.isWorkingCopy();
			if(!sourceModule.isConsistent()) {
				assertTrue(isWorkingCopy);
				String source = sourceModule.getSource();
				return getModuleParseCache().getParsedModule(filePath, source);
			} else {
				// This method can be called during the scope of the discard/commit working copy method,
				// and as such the WorkingCopyListener has not yet had a chance to discard the cache working.
				// Because of that, we should check here as well if it's a WC, and discard it if so.
				if(!isWorkingCopy) {
					getModuleParseCache().discardWorkingCopy(filePath);
				}
				return getModuleParseCache().getParsedModule(filePath);
			}
		} catch (ParseSourceException | ModelException e) {
			DeeCore.logWarning("Error in parseModule", e);
			return null;
		}
	}
	
	public class WorkingCopyListener extends ModelDeltaVisitor {
		
		@Override
		public void visitModule(IModelElementDelta moduleDelta, ISourceModule sourceModule) {
			if((moduleDelta.getFlags() & IModelElementDelta.F_PRIMARY_WORKING_COPY) != 0) {
				if(sourceModule.isWorkingCopy() == false) {
					Path filePath = validateFilePath(sourceModule);
					if(filePath != null) {
						getModuleParseCache().discardWorkingCopy(filePath);
					}
				}
			}
		}
		
	}
	
	/* ----------------- Specific semantic operations: ----------------- */
	
	public ParsedModule doParseForRebuild(IModuleSource input, IProblemReporter reporter) {
		ParsedModule parsedModule = DToolClient.getDefault().getParsedModuleOrNull_fromBuildStructure(input);
		DeeSourceParserFactory.reportErrors(reporter, parsedModule);
		return parsedModule;
	}
	
	public void provideModelElements(IModuleSource moduleSource, IProblemReporter pr, 
			ISourceElementRequestor requestor) {
		ParsedModule parsedModule = doParseForRebuild(moduleSource, pr);
		if (parsedModule != null) {
			new DeeSourceElementProvider(requestor).provide(parsedModule);
		}
	}
	
	/* -----------------  ----------------- */
	
	public static final String FIND_DEF_PickedElementAlreadyADefinition = 
		"Element next to cursor is already a definition, not a reference.";
	public static final String FIND_DEF_NoReferenceFoundAtCursor = 
		"No reference found next to cursor.";
	public static final String FIND_DEF_MISSING_REFERENCE_AT_CURSOR = FIND_DEF_NoReferenceFoundAtCursor;
	public static final String FIND_DEF_NoNamedReferenceAtCursor = 
		"No named reference found next to cursor.";
	public static final String FIND_DEF_ReferenceResolveFailed = 
		"Definition not found for reference: ";
	
	public static FindDefinitionResult doFindDefinition(final ISourceModule sourceModule, final int offset) {
		Module module = DToolClient.getDefault().getModuleNodeOrNull(sourceModule);
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		if(node == null) {
			return new FindDefinitionResult("No node found at offset: " + offset);
		}
		
		ReferenceSwitchHelper<FindDefinitionResult> refPickHelper = new ReferenceSwitchHelper<FindDefinitionResult>() {
			
			@Override
			protected FindDefinitionResult nodeIsDefSymbol(DefSymbol defSymbol) {
				return new FindDefinitionResult(FIND_DEF_PickedElementAlreadyADefinition);
			}
			
			@Override
			protected FindDefinitionResult nodeIsNotReference() {
				return new FindDefinitionResult(FIND_DEF_NoReferenceFoundAtCursor);
			}
			
			@Override
			protected FindDefinitionResult nodeIsNonNamedReference(Reference reference) {
				return new FindDefinitionResult(FIND_DEF_NoNamedReferenceAtCursor);
			}
			
			@Override
			protected FindDefinitionResult nodeIsNamedReference_missing(NamedReference namedReference) {
				return new FindDefinitionResult(FIND_DEF_MISSING_REFERENCE_AT_CURSOR);
			}
			
			@Override
			protected FindDefinitionResult nodeIsNamedReference_ok(NamedReference namedReference) {
				return doFindDefinitionForRef(namedReference, sourceModule);
			}
		};
		
		return refPickHelper.switchOnPickedNode(node);
	}
	
	public static FindDefinitionResult doFindDefinitionForRef(Reference ref, ISourceModule sourceModule) {
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule.getScriptProject());
		Collection<INamedElement> defElements = ref.findTargetDefElements(moduleResolver, false);
		
		if(defElements == null || defElements.size() == 0) {
			return new FindDefinitionResult(FIND_DEF_ReferenceResolveFailed + ref.toStringAsCode());
		}
		
		List<FindDefinitionResultEntry> results = new ArrayList<>();
		for (INamedElement namedElement : defElements) {
			final DefUnit defUnit = namedElement.resolveDefUnit();
			results.add(new FindDefinitionResultEntry(
				defUnit.defname.getSourceRangeOrNull(),
				namedElement.getExtendedName(), 
				namedElement.isLanguageIntrinsic(),
				defUnit.getModuleNode().compilationUnitPath));
		}
		
		return new FindDefinitionResult(results, ref.getModuleNode().compilationUnitPath);
	}
	
	
	/* -----------------  ----------------- */
	
	
	
	public PrefixDefUnitSearch doCodeCompletion(ISourceModule moduleUnit, int offset) throws ModelException {
		DeeParserResult parseResult = getParsedModuleOrNull(moduleUnit); 
		return doCodeCompletionDo(parseResult, offset, moduleUnit);
	}
	
	public PrefixDefUnitSearch doCodeCompletion(IModuleSource moduleSource, int offset) throws ModelException {
		DeeParserResult parseResult;
		
		if(moduleSource instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) moduleSource;
			sourceModule.makeConsistent(new NullProgressMonitor());
			
			parseResult = getParsedModuleOrNull(sourceModule);
		} else {
			parseResult = getParsedModuleOrNull_updateSource(moduleSource);
		}
		
		IModelElement modelElement = moduleSource.getModelElement();
		return doCodeCompletionDo(parseResult, offset, modelElement);
	}
	
	protected PrefixDefUnitSearch doCodeCompletionDo(DeeParserResult parseResultOrNull, int offset, 
			IModelElement modelElement) throws ModelException {
		if(parseResultOrNull == null) {
			throw new ModelException(DeeCore.createCoreException("Source not found for content assist.", null));
		}
		IModuleResolver mr = modelElement == null ? 
				new NullModuleResolver() :
				new DeeProjectModuleResolver(modelElement.getScriptProject());
		return PrefixDefUnitSearch.doCompletionSearch(parseResultOrNull, offset, mr);
	}
	
	/* -----------------  ----------------- */
	
	public static String getDDocHTMLView(ISourceModule sourceModule, int offset) {
		Module module = getDefault().getModuleNodeOrNull(sourceModule);
		ASTNode pickedNode = ASTNodeFinder.findElement(module, offset);
		
		INamedElement relevantElementForDoc = null;
		if(pickedNode instanceof DefSymbol) {
			relevantElementForDoc = ((DefSymbol) pickedNode).getDefUnit();
		} else if(pickedNode instanceof NamedReference) {
			DeeProjectModuleResolver mr = new DeeProjectModuleResolver(sourceModule.getScriptProject());
			relevantElementForDoc = ((NamedReference) pickedNode).findTargetDefElement(mr);
		}
		
		return relevantElementForDoc == null ? null : TextUI.getDDocHTMLRender(relevantElementForDoc);
	}
	
}