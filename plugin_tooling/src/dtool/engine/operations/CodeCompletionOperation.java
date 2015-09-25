/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.misc.NumberUtil.isInRange;
import static melnorme.utilbox.misc.NumberUtil.isInsideRange;

import java.nio.file.Path;

import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.ast.util.ASTNodeFinderExtension;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.completion.CompletionLocationInfo;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import dtool.ast.definitions.Module;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.engine.ResolvedModule;
import dtool.engine.SemanticManager;
import dtool.engine.operations.DeeSymbolCompletionResult.ECompletionResultStatus;
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
	
	public DeeSymbolCompletionResult doCodeCompletion() throws CommonException {
		ResolvedModule resolvedModule = getResolvedModule(fileLoc);
		return doCodeCompletion(resolvedModule, offset);
	}
	
	public static DeeSymbolCompletionResult doCodeCompletion(ResolvedModule resolvedModule, int offset) {
		return completionSearch(resolvedModule.getParsedModule(), offset, resolvedModule.getSemanticContext());
	}
	
	public static boolean canCodeCompleteInsideToken(IToken token, int offset) {
		if(token.getType() == DeeTokens.WHITESPACE || token.getType().isAlphaNumeric()) {
			return true;
		}
		
		if(token.getType() == DeeTokens.STRING_TOKENS) {
			if(token.getSourceValue().length() < 3) {
				return false; // This should never happen, actually.
			}
			// This string token is often used for code, so we allow completion inside it:
			return isInRange(token.getStartPos()+2, offset, token.getEndPos()-1);
		}
		
		return false;
	}
	
	public static DeeSymbolCompletionResult completionSearch(DeeParserResult parseResult, int offset, 
			ISemanticContext context) {
		
		assertTrue(isInRange(0, offset, parseResult.source.length()));
		
		TokenAtOffsetResult tokenAtOffsetResult = parseResult.findTokenAtOffset(offset);
		IToken tokenAtOffsetLeft = tokenAtOffsetResult.atLeft;
		IToken tokenAtOffsetRight = tokenAtOffsetResult.atRight;
		
		if(tokenAtOffsetResult.isSingleToken() 
			&& isInsideRange(tokenAtOffsetLeft.getStartPos(), offset, tokenAtOffsetLeft.getEndPos()) 
			&& !canCodeCompleteInsideToken(tokenAtOffsetLeft, offset)
		) {
			return new DeeSymbolCompletionResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION);
		}
		if(tokenAtOffsetLeft != null 		
			&& tokenAtOffsetLeft.getType().getGroupingToken() == DeeTokens.GROUP_FLOAT
			&& tokenAtOffsetLeft.getSourceValue().endsWith(".")) {
			return new DeeSymbolCompletionResult(ECompletionResultStatus.INVALID_TOKEN_LOCATION_FLOAT);
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
		assertTrue(pickedNode.getSourceRange().inclusiveContains(offset));
		
		CommonLanguageElement elementAtOffset = pickedNode;
		
		if(elementAtOffset instanceof CommonQualifiedReference) {
			CommonQualifiedReference namedRef = (CommonQualifiedReference) elementAtOffset;
			assertTrue(nameToken == null);
			
			if(offset <= namedRef.getDotOffset()) {
				elementAtOffset = namedRef.getLexicalParent();
			}
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset);
			return performCompletionSearch(locationInfo, context, elementAtOffset);
		} else if(elementAtOffset instanceof RefModule) {
			RefModule refModule = (RefModule) elementAtOffset;
			// RefModule has a specialized way to setup prefix len things
			
			String source = parseResult.source;
			CompletionLocationInfo locationInfo = codeCompletionRefModule(offset, tokenAtOffsetRight, source, refModule);
			return performCompletionSearch(locationInfo, context, elementAtOffset);
		} 
		
		if(nameToken != null) {
			assertTrue(nameToken.getSourceRange().inclusiveContains(offset));
			
			
			String searchPrefix = nameToken.getSourceValue().substring(0, offset - nameToken.getStartPos());
			int rplLen = nameToken.getEndPos() - offset;
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset, searchPrefix, rplLen);
			
			// Because of some parser limitations, in some cases nodeForNameLookup needs to be corrected,
			// such that it won't be the same as nodeForNameLookup
			ASTNode nodeForNameLookup = getStartingNodeForNameLookup(nameToken.getStartPos(), module);
			
			return performCompletionSearch(locationInfo, context, nodeForNameLookup);
			
		} else {
			CompletionLocationInfo locationInfo = new CompletionLocationInfo(offset);
			return performCompletionSearch(locationInfo, context, elementAtOffset);
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
		String moduleQualifiedNameCanonicalPrefix = parseModuleQualifiedNamePrefix(refModuleSnippedSource);
		
		return new CompletionLocationInfo(offset, moduleQualifiedNameCanonicalPrefix, rplLen);
	}
	
	protected static String parseModuleQualifiedNamePrefix(String refModuleSnippedSource) {
		DeeParser parser = new DeeParser(refModuleSnippedSource);
		String moduleQualifiedNameCanonicalPrefix;
		try {
			moduleQualifiedNameCanonicalPrefix = parser.parseRefModule().toStringAsCode();
		} catch(OperationCancellation e) {
			throw assertUnreachable();
		}
		
		DeeTokens lookAhead = parser.lookAhead();
		if(lookAhead != DeeTokens.EOF) {
			assertTrue(lookAhead.isKeyword());
			moduleQualifiedNameCanonicalPrefix += lookAhead.getSourceValue();
		}
		return moduleQualifiedNameCanonicalPrefix;
	}
	
	public static DeeSymbolCompletionResult performCompletionSearch(CompletionLocationInfo locationInfo, 
			ISemanticContext context, CommonLanguageElement element) {
		CompletionScopeLookup search = new CompletionScopeLookup(locationInfo.offset, context, 
			locationInfo.searchPrefix);
		element.performNameLookup(search);
		return new DeeSymbolCompletionResult(locationInfo, search.getMatchedElements());
	}
	
}