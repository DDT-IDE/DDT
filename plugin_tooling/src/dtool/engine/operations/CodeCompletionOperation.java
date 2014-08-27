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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.NumberUtil.isInsideRange;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import dtool.ast.ASTNode;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.CommonRefIdentifier;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.ast.util.ASTNodeFinderExtension;
import dtool.engine.AbstractBundleResolution.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.operations.CompletionSearchResult.ECompletionResultStatus;
import dtool.engine.operations.CompletionSearchResult.PrefixSearchOptions;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.common.IToken;
import dtool.resolver.PrefixDefUnitSearch;

public class CodeCompletionOperation extends AbstractDToolOperation {
	
	public CodeCompletionOperation(SemanticManager semanticManager) {
		super(semanticManager);
	}
	
	public CompletionSearchResult doCodeCompletion(Path filePath, int offset, Path compilerPath)
			throws ExecutionException {
		if(filePath == null) { 
			throw new ExecutionException(new Exception("Invalid path for content assist source.")); 
		}
		
		ResolvedModule resolvedModule = getSemanticManager().getUpdatedResolvedModule(filePath, compilerPath);
		return doCodeCompletion(resolvedModule, offset);
	}
	
	public static CompletionSearchResult doCodeCompletion(ResolvedModule resolvedModule, int offset) {
		return completionSearch(resolvedModule.getParsedModule(), offset, 
			resolvedModule.getModuleResolver());
	}
	
	public static boolean canCompleteInsideToken(IToken token) {
		return !(token.getType() == DeeTokens.WHITESPACE || tokenIsAlphaNumeric(token));
	}
	
	public static boolean tokenIsAlphaNumeric(IToken token) {
		return token.getType() == DeeTokens.IDENTIFIER || token.getType().isKeyword();
	}
	
	public static CompletionSearchResult completionSearch(DeeParserResult parseResult, int offset, 
			IModuleResolver mr) {
		
		String source = parseResult.source;
		assertTrue(offset >= 0 && offset <= source.length());		
		
		Module module = parseResult.getModuleNode();
		
		IToken tokenAtOffset = parseResult.findTokenAtOffset(offset);
		
		if(isInsideRange(tokenAtOffset.getStartPos(), offset, tokenAtOffset.getEndPos()) 
				&& canCompleteInsideToken(tokenAtOffset)) {
			return new CompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION);
		}
		if(tokenAtOffset.getType().getGroupingToken() == DeeTokens.GROUP_FLOAT) {
			if(tokenAtOffset.getSourceValue().endsWith(".")) {
				return new CompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION_FLOAT);
			}
		}
		
		// Determine node that will be starting point to determine lookup scope.
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(module, offset, true);
		ASTNode node = nodeFinder.match;
		if(nodeFinder.matchOnLeft instanceof NamedReference) {
			NamedReference reference = (NamedReference) nodeFinder.matchOnLeft;
			if(reference.isMissingCoreReference()) {
				node = nodeFinder.matchOnLeft;
			}
		}
		assertTrue(node.getSourceRange().containsInRange(offset));
		
		if(node instanceof CommonRefIdentifier) {
			CommonRefIdentifier refIdentifier = (CommonRefIdentifier) node;
			
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			
			int nameStart = refIdentifier.getStartPos();
			assertTrue(offset - nameStart >= 0);
			String searchPrefix = refIdentifier.getCoreReferenceName(/*BUG here*/).substring(0, offset - nameStart);
			int rplLen = refIdentifier.getEndPos() - offset;
			searchOptions.setPrefixSearchOptions(searchPrefix, rplLen);
			
			return performCompletionSearch(offset, mr, module, node, searchOptions);
			
		} else if(node instanceof CommonQualifiedReference) {
			CommonQualifiedReference namedRef = (CommonQualifiedReference) node;
			if(offset <= namedRef.getDotOffset()) {
				node = namedRef.getParent();
			}
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			return performCompletionSearch(offset, mr, module, node, searchOptions);
		} else if(node instanceof RefModule) {
			RefModule refModule = (RefModule) node;
			// RefModule has a specialized way to setup prefix len things
			
			PrefixSearchOptions searchOptions = codeCompletionRefModule(offset, tokenAtOffset, source, refModule);
			return performCompletionSearch(offset, mr, module, node, searchOptions);
		} else {
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			return performCompletionSearch(offset, mr, module, node, searchOptions);
		}
		
	}
	
	public static PrefixSearchOptions codeCompletionRefModule(final int offset, IToken tokenAtOffset, 
			String source, RefModule refModule) {
		
		int idEnd = refModule.getEndPos();
		if(refModule.isMissingCoreReference()) {
			if(tokenAtOffset.getType().isKeyword() && tokenAtOffset.getEndPos() > refModule.getEndPos()) {
				idEnd = tokenAtOffset.getEndPos(); // Fix for attached keyword ids
			} else {
				idEnd = refModule.moduleToken.getFullRangeStartPos();
			}
		}
		int rplLen = offset > idEnd ? 0 : idEnd - offset;
		
		// We reparse the snipped source as it's the easiest way to determine search prefix
		String moduleQualifiedNameSnippedSource = source.substring(refModule.getStartPos(), offset);
		DeeParser parser = new DeeParser(moduleQualifiedNameSnippedSource);
		String moduleQualifiedNameCanonicalPrefix = parser.parseRefModule().toStringAsCode();
		DeeTokens lookAhead = parser.lookAhead();
		if(lookAhead != DeeTokens.EOF) {
			assertTrue(lookAhead.isKeyword());
			moduleQualifiedNameCanonicalPrefix += lookAhead.getSourceValue();
		}
		PrefixSearchOptions searchOptions = new PrefixSearchOptions();
		searchOptions.setPrefixSearchOptions(moduleQualifiedNameCanonicalPrefix, rplLen);
		searchOptions.isImportModuleSearch = true;
		return searchOptions;
	}
	
	public static CompletionSearchResult performCompletionSearch(int offset, IModuleResolver mr, Module module,
			ASTNode node, PrefixSearchOptions searchOptions) {
		PrefixDefUnitSearch search = new PrefixDefUnitSearch(module, offset, mr, searchOptions);
		node.performRefSearch(search);
		return new CompletionSearchResult(search.searchOptions, search.getResults());
	}
	
}