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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mmrnmhrm.core.DLTKUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.ModelDeltaVisitor;
import mmrnmhrm.core.parser.ModuleParsingHandler;

import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.env.IModuleSource;
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
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.project.DeeNamingRules;
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
	
	public ParsedModule parseModule(ISourceModule sourceModule) {
		try {
			Path filePath = DLTKUtils.filePathFromSourceModule(sourceModule);
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
	
	public ParsedModule parseModule(IModuleSource moduleSource) {
		
		ISourceModule sourceModule = tryCast(moduleSource.getModelElement(), ISourceModule.class);
		if(sourceModule != null) {
			return parseModule(sourceModule);
		}
		
		String fileName = moduleSource.getFileName();
		String source = moduleSource.getSourceContents();
		return getModuleParseCache().getParsedModule(fileName, source);
	}
	
	public class WorkingCopyListener extends ModelDeltaVisitor {
		
		@Override
		public void visitModule(IModelElementDelta moduleDelta, ISourceModule sourceModule) {
			if((moduleDelta.getFlags() & IModelElementDelta.F_PRIMARY_WORKING_COPY) != 0) {
				if(sourceModule.isWorkingCopy() == false) {
					try {
						Path filePath = DLTKUtils.filePathFromSourceModule(sourceModule);
						getModuleParseCache().discardWorkingCopy(filePath);
					} catch (ModelException e) {
						DeeCore.logError(e);
					}
				}
			}
		}
		
	}
	
	/* ----------------- new API ----------------- */
	
	public ParsedModule getParsedModule(IModuleSource input) {
		return parseModule(input);
	}
	public ParsedModule getParsedModule(ISourceModule input) {
		return parseModule(input);
	}
	
	
	public Module getParsedModuleNode(IModuleSource input) {
		ParsedModule parseModule = parseModule(input);
		return parseModule == null ? null : parseModule.module;
	}
	
	// TODO /*BUG here*/
	public DeeParserResult getExistingParsedModule(ISourceModule input) {
		return parseModule(input);
	}
	
	public ParsedModule getParsedModule_forDeprecatedAPIs(IModuleSource input) {
		return parseModule(input);
	}
	public ParsedModule getParsedModule_forDeprecatedAPIs(ISourceModule input) {
		return parseModule(input);
	}
	
	public ParsedModule getParsedModule_fromWorkingCopy(IModuleSource input) {
		return parseModule(input);
	}
	
	public void provideModelElements(IModuleSource moduleSource, ISourceElementRequestor requestor) {
		ParsedModule parsedModule = getParsedModule(moduleSource);
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
		Module module = DToolClient.getDefault().getParsedModule(sourceModule).module;
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		if(node == null) {
			return  new FindDefinitionResult("No node found at offset: " + offset);
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
	
	
	
	public static PrefixDefUnitSearch doCodeCompletion(int offset, ISourceModule moduleUnit) {
		return getDefault().doCodeCompletionDo(offset, moduleUnit);
	}
	
	public PrefixDefUnitSearch doCodeCompletionDo(int offset, ISourceModule moduleUnit) {
		DeeParserResult parseResult = ModuleParsingHandler.parseModule(moduleUnit);
		DeeProjectModuleResolver mr = new DeeProjectModuleResolver(moduleUnit.getScriptProject());
		return PrefixDefUnitSearch.doCompletionSearch(parseResult, offset, mr);
	}
	
	public static PrefixDefUnitSearch doCodeCompletion2(IModuleSource moduleSource, final int position) {
		DeeParserResult parseResult;
		IModuleResolver mr;
		
		if(moduleSource instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) moduleSource;
			parseResult = ModuleParsingHandler.parseModule(sourceModule);
			mr = new DeeProjectModuleResolver(sourceModule.getScriptProject());
		} else {
			String defaultModuleName = DToolClient.getDefaultModuleName(moduleSource);
			parseResult = DeeParser.parseSource(moduleSource.getSourceContents(), defaultModuleName);
			
			IModelElement modelElement = moduleSource.getModelElement();
			if(modelElement != null) {
				mr = new DeeProjectModuleResolver(modelElement.getScriptProject());
			} else {
				mr = new NullModuleResolver();
			}
		}
		
		return PrefixDefUnitSearch.doCompletionSearch(parseResult, position, mr);
	}
	
	public static String getDefaultModuleName(IModuleSource moduleSource) {
		String fileName = moduleSource.getFileName();
		return fileName == null ? "" : DeeNamingRules.getDefaultModuleNameFromFileName(fileName);
	}
	
	/* -----------------  ----------------- */
	
	public static String getDDocHTMLView(ISourceModule sourceModule, int offset) {
		Module module = getDefault().getParsedModule_forDeprecatedAPIs(sourceModule).module;
		ASTNode pickedNode = ASTNodeFinder.findElement(module, offset);
		
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule.getScriptProject());
		
		INamedElement relevantElementForDoc = null;
		if(pickedNode instanceof DefSymbol) {
			relevantElementForDoc = ((DefSymbol) pickedNode).getDefUnit();
		} else if(pickedNode instanceof NamedReference) {
			relevantElementForDoc = ((NamedReference) pickedNode).findTargetDefElement(moduleResolver);
		}
		
		return relevantElementForDoc == null ? null : TextUI.getDDocHTMLRender(relevantElementForDoc);
	}
	
}