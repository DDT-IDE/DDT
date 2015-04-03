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

import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.ast.util.ASTNodeFinderExtension;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.ECompletionResultStatus;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult.CompletionLocationInfo;
import melnorme.utilbox.core.CommonException;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.engine.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeTokens;
import dtool.parser.common.IToken;
import dtool.parser.common.LexerResult.TokenAtOffsetResult;

public class CodeCompletionOperation extends AbstractDToolOperation {
	
	public CodeCompletionOperation(SemanticManager semanticManager, Path filePath, int offset, Path compilerPath,
			String dubPath) throws CommonException {
		super(semanticManager, filePath, offset, compilerPath, dubPath);
	}
	
	public DeeCompletionSearchResult doCodeCompletion() throws CommonException {
		ResolvedModule resolvedModule = getResolvedModule(fileLoc);
		return doCodeCompletion(resolvedModule, offset);
	}
	
	public static DeeCompletionSearchResult doCodeCompletion(ResolvedModule resolvedModule, int offset) {
		return completionSearch(resolvedModule.getParsedModule(), offset, resolvedModule.getSemanticContext());
	}
	
	public static boolean canCompleteInsideToken(IToken token) {
		return !(token.getType() == DeeTokens.WHITESPACE || token.getType().isAlphaNumeric());
	}
	
	public static DeeCompletionSearchResult completionSearch(DeeParserResult parseResult, int offset, 
			ISemanticContext mr) {
		
		assertTrue(isInRange(0, offset, parseResult.source.length()));
		
		TokenAtOffsetResult tokenAtOffsetResult = parseResult.findTokenAtOffset(offset);
		IToken tokenAtOffsetLeft = tokenAtOffsetResult.atLeft;
		IToken tokenAtOffsetRight = tokenAtOffsetResult.atRight;
		
		if(tokenAtOffsetResult.isSingleToken() 
			&& isInsideRange(tokenAtOffsetLeft.getStartPos(), offset, tokenAtOffsetLeft.getEndPos()) 
			&& canCompleteInsideToken(tokenAtOffsetLeft)
		) {
			return new DeeCompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION);
		}
		if(tokenAtOffsetLeft != null 		
			&& tokenAtOffsetLeft.getType().getGroupingToken() == DeeTokens.GROUP_FLOAT
			&& tokenAtOffsetLeft.getSourceValue().endsWith(".")) {
			return new DeeCompletionSearchResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION_FLOAT);
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
		ASTNode pickedNode = new ASTNodeFinderExtension(module, offset, true).match;
		assertTrue(pickedNode.getSourceRange().contains(offset));
		
		CommonLanguageElement elementAtOffset = pickedNode;
		
		if(elementAtOffset instanceof CommonQualifiedReference) {
			CommonQualifiedReference namedRef = (CommonQualifiedReference) elementAtOffset;
			assertTrue(nameToken == null);
			
			if(offset <= namedRef.getDotOffset()) {
				elementAtOffset = namedRef.getLexicalParent();
			}
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset);
			return performCompletionSearch(locationInfo, mr, elementAtOffset);
		} else if(elementAtOffset instanceof RefModule) {
			RefModule refModule = (RefModule) elementAtOffset;
			// RefModule has a specialized way to setup prefix len things
			
			String source = parseResult.source;
			CompletionLocationInfo locationInfo = codeCompletionRefModule(offset, tokenAtOffsetRight, source, refModule);
			return performCompletionSearch(locationInfo, mr, elementAtOffset);
		} 
		
		if(nameToken != null) {
			assertTrue(nameToken.getSourceRange().contains(offset));
			
			
			String searchPrefix = nameToken.getSourceValue().substring(0, offset - nameToken.getStartPos());
			int rplLen = nameToken.getEndPos() - offset;
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset, searchPrefix, rplLen);
			
			// Because of some parser limitations, in some cases nodeForNameLookup needs to be corrected,
			// such that it won't be the same as nodeForNameLookup
			ASTNode nodeForNameLookup = getStartingNodeForNameLookup(nameToken.getStartPos(), module);
			
			return performCompletionSearch(locationInfo, mr, nodeForNameLookup);
			
		} else {
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset);
			return performCompletionSearch(locationInfo, mr, elementAtOffset);
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
	
	public static CompletionLocationInfo codeCompletionRefModule(final int offset, IToken tokenAtOffsetRight, 
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
		
		return new CompletionLocationInfo(offset, moduleQualifiedNameCanonicalPrefix, rplLen);
	}
	
	public static DeeCompletionSearchResult performCompletionSearch(CompletionLocationInfo locationInfo, 
			ISemanticContext context, CommonLanguageElement element) {
		CompletionScopeLookup search = new CompletionScopeLookup(locationInfo.offset, context, locationInfo.searchPrefix);
		element.performNameLookup(search);
		return new DeeCompletionSearchResult(locationInfo, search.getMatchedElements());
	}
	
}