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
import static melnorme.utilbox.misc.NumberUtil.isInRange;
import static melnorme.utilbox.misc.NumberUtil.isInsideRange;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import dtool.ast.ASTNode;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonQualifiedReference;
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
import dtool.parser.common.LexerResult.TokenAtOffsetResult;
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
		return completionSearch(resolvedModule.getParsedModule(), offset, resolvedModule.getModuleResolver());
	}
	
	public static boolean canCompleteInsideToken(IToken token) {
		return !(token.getType() == DeeTokens.WHITESPACE || token.getType().isAlphaNumeric());
	}
	
	public static CompletionSearchResult completionSearch(DeeParserResult parseResult, int offset, 
			IModuleResolver mr) {
		
		assertTrue(isInRange(0, offset, parseResult.source.length()));
		
		TokenAtOffsetResult tokenAtOffsetResult = parseResult.findTokenAtOffset(offset);
		IToken tokenAtOffsetLeft = tokenAtOffsetResult.atLeft;
		IToken tokenAtOffsetRight = tokenAtOffsetResult.atRight;
		
		if(tokenAtOffsetResult.isSingleToken() 
			&& isInsideRange(tokenAtOffsetLeft.getStartPos(), offset, tokenAtOffsetLeft.getEndPos()) 
			&& canCompleteInsideToken(tokenAtOffsetLeft)
		) {
			return new CompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION);
		}
		if(tokenAtOffsetLeft != null 		
			&& tokenAtOffsetLeft.getType().getGroupingToken() == DeeTokens.GROUP_FLOAT
			&& tokenAtOffsetLeft.getSourceValue().endsWith(".")) {
			return new CompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION_FLOAT);
		}
		
		final IToken nameToken;
		
		if(tokenAtOffsetLeft != null && tokenAtOffsetLeft.getType().isAlphaNumeric()) {
			nameToken = tokenAtOffsetLeft;
		} else if(tokenAtOffsetRight != null && tokenAtOffsetRight.getType().isAlphaNumeric()) {
			nameToken = tokenAtOffsetRight;
		} else {
			nameToken = null;
		}
		
		Module module = parseResult.getModuleNode();
		ASTNode nodeAtOffset = new ASTNodeFinderExtension(module, offset, true).match;
		assertTrue(nodeAtOffset.getSourceRange().contains(offset));
		
		if(nodeAtOffset instanceof CommonQualifiedReference) {
			CommonQualifiedReference namedRef = (CommonQualifiedReference) nodeAtOffset;
			assertTrue(nameToken == null);
			
			if(offset <= namedRef.getDotOffset()) {
				nodeAtOffset = namedRef.getParent();
			}
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			return performCompletionSearch(offset, mr, module, nodeAtOffset, searchOptions);
		} else if(nodeAtOffset instanceof RefModule) {
			RefModule refModule = (RefModule) nodeAtOffset;
			// RefModule has a specialized way to setup prefix len things
			
			String source = parseResult.source;
			PrefixSearchOptions searchOptions = codeCompletionRefModule(offset, tokenAtOffsetRight, source, refModule);
			return performCompletionSearch(offset, mr, module, nodeAtOffset, searchOptions);
		} 
		
		if(nameToken != null) {
			assertTrue(nameToken.getSourceRange().contains(offset));
			
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			
			String searchPrefix = nameToken.getSourceValue().substring(0, offset - nameToken.getStartPos());
			int rplLen = nameToken.getEndPos() - offset;
			searchOptions.setPrefixSearchOptions(searchPrefix, rplLen);
			
			// Because of some parser limitations, in some cases nodeForNameLookup needs to be corrected,
			// such that it won't be the same as nodeForNameLookup
			ASTNode nodeForNameLookup = getStartingNodeForNameLookup(nameToken.getStartPos(), module);
			
			return performCompletionSearch(offset, mr, module, nodeForNameLookup, searchOptions);
			
		} else {
			PrefixSearchOptions searchOptions = new PrefixSearchOptions();
			return performCompletionSearch(offset, mr, module, nodeAtOffset, searchOptions);
		}
		
	}
	
	protected static ASTNode getStartingNodeForNameLookup(int offset, Module module) {
		ASTNodeFinderExtension nodeFinder = new ASTNodeFinderExtension(module, offset, true);
		ASTNode node = nodeFinder.match;
		if(nodeFinder.matchOnLeft instanceof NamedReference) {
			NamedReference reference = (NamedReference) nodeFinder.matchOnLeft;
			if(reference.isMissingCoreReference()) {
				node = nodeFinder.matchOnLeft;
			}
		}
		return node;
	}
	
	public static PrefixSearchOptions codeCompletionRefModule(final int offset, IToken tokenAtOffsetRight, 
			String source, RefModule refModule) {
		
		int idEnd = refModule.getEndPos();
		if(refModule.isMissingCoreReference()) {
			if(tokenAtOffsetRight.getType().isKeyword()) {
				idEnd = tokenAtOffsetRight.getEndPos(); // Fix for attached keyword ids
			} else {
				idEnd = refModule.moduleToken.getFullRangeStartPos();
			}
		}
		int rplLen = offset > idEnd ? 0 : idEnd - offset;
		
		// We reparse the snipped source as it's the easiest way to determine search prefix
		String refModuleSnippedSource = source.substring(refModule.getStartPos(), offset);
		DeeParser parser = new DeeParser(refModuleSnippedSource);
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
		return new CompletionSearchResult(search.searchOptions, search.getMatchedElements());
	}
	
}