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
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
import dtool.ast.util.ReferenceSwitchHelper;
import dtool.ddoc.TextUI;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;

public class DToolServer {
	
	protected final SemanticManager semanticManager = new SemanticManager(this);
	
	public DToolServer() {
		logMessage(" ------ DTool engine started ------ ");
	}
	
	public SemanticManager getSemanticManager() {
		return semanticManager;
	}
	
	protected void shutdown() {
		semanticManager.shutdown();
	}
	
	public void logMessage(String message) {
		System.out.println("> " + message);
	}
	
	public final void logError(String message) {
		logError(message, null);
	}
	public void logError(String message, Throwable throwable) {
		System.out.println("!! " + message);
		if(throwable != null) {
			System.out.println(throwable);
		}
	}
	
	public void handleInternalError(Throwable throwable) {
		logError("!!!! INTERNAL ERRROR: ", throwable);
		throwable.printStackTrace(System.err);
	}
	
	
	protected ResolvedModule getResolvedModule(Path filePath) throws ExecutionException {
		return getSemanticManager().getUpdatedResolvedModule(filePath);
	}
	
	/* -----------------  ----------------- */
	
	public class DToolTaskAgent extends ExecutorTaskAgent {
		public DToolTaskAgent(String name) {
			super(name);
		}
		
		@Override
		protected void handleUnexpectedException(Throwable throwable) {
			handleInternalError(throwable);
		}
	}
	
	/* ----------------- Operations ----------------- */
	
	public static final String FIND_DEF_PickedElementAlreadyADefinition = 
		"Element next to cursor is already a definition, not a reference.";
	public static final String FIND_DEF_NoReferenceFoundAtCursor = 
		"No reference found next to cursor.";
	public static final String FIND_DEF_MISSING_REFERENCE_AT_CURSOR = 
		FIND_DEF_NoReferenceFoundAtCursor;
	public static final String FIND_DEF_NoNamedReferenceAtCursor = 
		"No named reference found next to cursor.";
	public static final String FIND_DEF_ReferenceResolveFailed = 
			"Definition not found for reference: ";
	
	
	public FindDefinitionResult doFindDefinition(Path filePath, final int offset) {
		if(filePath == null) {
			return new FindDefinitionResult("Invalid path for file: " );
		}
		final ResolvedModule resolvedModule;
		try {
			resolvedModule = getResolvedModule(filePath);
		} catch (ExecutionException e) {
			return new FindDefinitionResult("Error awaiting operation result: " + e);
		}
		Module module = resolvedModule.getModuleNode();
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		if(node == null) {
			return new FindDefinitionResult("No node found at offset: " + offset);
		}
		assertEquals(node.getModuleNode().compilationUnitPath, filePath); /*BUG here normalization */
		
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
				return doFindDefinitionForRef(namedReference, resolvedModule);
			}
		};
		
		return refPickHelper.switchOnPickedNode(node);
	}
	
	protected FindDefinitionResult doFindDefinitionForRef(Reference ref, ResolvedModule resolvedModule) {
		IModuleResolver moduleResolver = resolvedModule.getModuleResolver();
		Collection<INamedElement> defElements = ref.findTargetDefElements(moduleResolver, false);
		
		if(defElements == null || defElements.size() == 0) {
			return new FindDefinitionResult(FIND_DEF_ReferenceResolveFailed + ref.toStringAsCode());
		}
		
		List<FindDefinitionResultEntry> results = new ArrayList<>();
		for (INamedElement namedElement : defElements) {
			final DefUnit defUnit = namedElement.resolveDefUnit();
			Path compilationUnitPath = defUnit.getModuleNode().compilationUnitPath;
			SourceRange sourceRange = defUnit.defname.getSourceRangeOrNull();
			if(defUnit.getArcheType() == EArcheType.Module && sourceRange == null) {
				sourceRange = new SourceRange(0, 0);
			}
			
			results.add(new FindDefinitionResultEntry(
				namedElement.getExtendedName(),
				namedElement.isLanguageIntrinsic(), 
				compilationUnitPath,
				sourceRange));
		}
		
		return new FindDefinitionResult(results);
	}
	
	/* -----------------  ----------------- */

	public String getDDocHTMLView(Path filePath, int offset) {
		ResolvedModule resolvedModule;
		try {
			resolvedModule = filePath == null ? null : getResolvedModule(filePath);
		} catch (ExecutionException e) {
			resolvedModule = null;
		}
		if(resolvedModule == null) {
			return null;
		}
		Module module = resolvedModule.getModuleNode();
		ASTNode pickedNode = ASTNodeFinder.findElement(module, offset);
		
		INamedElement relevantElementForDoc = null;
		if(pickedNode instanceof DefSymbol) {
			relevantElementForDoc = ((DefSymbol) pickedNode).getDefUnit();
		} else if(pickedNode instanceof NamedReference) {
			IModuleResolver mr = resolvedModule.getModuleResolver();
			relevantElementForDoc = ((NamedReference) pickedNode).findTargetDefElement(mr);
		}
		
		return relevantElementForDoc == null ? null : TextUI.getDDocHTMLRender(relevantElementForDoc);
	}
	
	/* ----------------- code completion ----------------- */
	
	public PrefixDefUnitSearch doCodeCompletion(Path filePath, int offset) throws ExecutionException {
		return doCodeCompletion(filePath, offset, null);
	}
	
	public PrefixDefUnitSearch doCodeCompletion(Path filePath, int offset, Path compilerPath) 
			throws ExecutionException {
		if(filePath == null) { 
			throw new ExecutionException(new Exception("Invalid path for content assist source.")); 
		}
		
		ResolvedModule resolvedModule = getSemanticManager().getUpdatedResolvedModule(filePath, compilerPath);
		return PrefixDefUnitSearch.doCompletionSearch(resolvedModule.getParsedModule(), offset, 
			resolvedModule.getModuleResolver());
	}
	
}