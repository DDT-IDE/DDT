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
package dtool.engine.operations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.ast.SourceRange;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
import dtool.ast.util.ReferenceSwitchHelper;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.operations.FindDefinitionResult.FindDefinitionResultEntry;

public class FindDefinitionOperation extends AbstractDToolOperation {
	
	public static final String FIND_DEF_PickedElementAlreadyADefinition = 
		"Element next to cursor is already a definition, not a reference.";
	public static final String FIND_DEF_NoReferenceFoundAtCursor = 
		"No reference found next to cursor.";
	public static final String FIND_DEF_MISSING_REFERENCE_AT_CURSOR = 
		FIND_DEF_NoReferenceFoundAtCursor;
	public static final String FIND_DEF_NoNameReferenceAtCursor = 
		"No name reference found next to cursor.";
	public static final String FIND_DEF_ReferenceResolveFailed = 
		"Definition not found for reference: ";
			
	public FindDefinitionOperation(SemanticManager semanticManager) {
		super(semanticManager);
	}
	
	public FindDefinitionResult findDefinition(Path filePath, final int offset) {
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
		assertEquals(node.getModuleNode().compilationUnitPath, filePath); /*FIXME: BUG here normalization */
		
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
				return new FindDefinitionResult(FIND_DEF_NoNameReferenceAtCursor);
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
			
			Path compilationUnitPath = null;
			SourceRange sourceRange = null;
			
			if(defUnit != null) { // This can happen with intrinsic elements 
				
				compilationUnitPath = defUnit.getModuleNode().compilationUnitPath;
				sourceRange = defUnit.defname.getSourceRangeOrNull();
				if(defUnit.getArcheType() == EArcheType.Module && sourceRange == null) {
					sourceRange = new SourceRange(0, 0);
				}
			}
			
			results.add(new FindDefinitionResultEntry(
				namedElement.getExtendedName(),
				namedElement.isLanguageIntrinsic(), 
				compilationUnitPath,
				sourceRange));
		}
		
		return new FindDefinitionResult(results);
	}

}