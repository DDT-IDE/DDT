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
import java.util.List;

import melnorme.lang.tooling.ast.ASTNodeFinder;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Collection2;
import melnorme.utilbox.core.CommonException;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;
import dtool.engine.ResolvedModule;
import dtool.engine.SemanticManager;
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
			
	public FindDefinitionOperation(SemanticManager semanticManager, Path filePath, int offset, Path compilerPath,
			String dubPath) throws CommonException {
		super(semanticManager, filePath, offset, compilerPath, dubPath);
	}
	
	public FindDefinitionResult findDefinition() throws CommonException {
		final ResolvedModule resolvedModule = getResolvedModule(fileLoc);
		final ISemanticContext mr = resolvedModule.getSemanticContext();
		Module module = resolvedModule.getModuleNode();
		
		assertEquals(module.compilationUnitPath, fileLoc.path);
		return findDefinition(module, offset, mr);
	}
	
	public static FindDefinitionResult findDefinition(Module module, final int offset, final ISemanticContext mr) {
		
		ASTNodeFinder nodeFinder = new ASTNodeFinder(module, offset, true);
		
		if(nodeFinder.matchOnLeft instanceof NamedReference) {
			NamedReference namedReference = (NamedReference) nodeFinder.matchOnLeft;
			return doFindDefinition(namedReference, mr);
		} else if(nodeFinder.match instanceof Reference) {
			Reference reference = (Reference) nodeFinder.match;
			return doFindDefinition(reference, mr);
		} else if(nodeFinder.match instanceof DefSymbol){
			return new FindDefinitionResult(FIND_DEF_PickedElementAlreadyADefinition);
		}
		
		return new FindDefinitionResult(FIND_DEF_NoReferenceFoundAtCursor);
	}
	
	public static FindDefinitionResult doFindDefinition(Reference reference, final ISemanticContext mr) {
		if(reference instanceof NamedReference) {
			NamedReference namedReference = (NamedReference) reference;
			if(namedReference.isMissingCoreReference()) {
				return new FindDefinitionResult(FIND_DEF_MISSING_REFERENCE_AT_CURSOR, namedReference);
			} if(namedReference instanceof CommonQualifiedReference) {
				// Then the cursor is not actually next to an identifier.
				return new FindDefinitionResult(FIND_DEF_NoNameReferenceAtCursor);
			} else {
				return doFindDefinitionForRef(namedReference, mr);
			}
		} else {
			return new FindDefinitionResult(FIND_DEF_NoNameReferenceAtCursor);
		}
	}
	
	public static FindDefinitionResult doFindDefinitionForRef(Reference ref, ISemanticContext context) {
		
		INamedElement resolveResult = ref.resolveTargetElement(context);
		
		if(resolveResult instanceof ErrorElement) {
			return new FindDefinitionResult(FIND_DEF_ReferenceResolveFailed + ref.toStringAsCode(), ref);
		}
		
		// TODO need to refactor use of OverloadedNamedElement
		Collection2<INamedElement> namedElements = Reference.resolveResultToCollection(resolveResult);
		
		List<FindDefinitionResultEntry> results = new ArrayList<>();
		for (INamedElement namedElement : namedElements) {
			final INamedElementNode node = namedElement.resolveUnderlyingNode();
			
			Path compilationUnitPath = null;
			SourceRange sourceRange = null;
			
			if(node != null) { // This can happen with intrinsic elements 
				compilationUnitPath = node.getModuleNode().getCompilationUnitPath();
				sourceRange = node.getNameSourceRangeOrNull();
			}
			
			results.add(new FindDefinitionResultEntry(
				namedElement.getExtendedName(),
				namedElement.isLanguageIntrinsic(), 
				compilationUnitPath,
				sourceRange));
		}
		
		return new FindDefinitionResult(results, ref, namedElements);
	}

}