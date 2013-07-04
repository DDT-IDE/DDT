/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeListView;
import dtool.ast.declarations.AbstractConditionalDeclaration.VersionSymbol;
import dtool.ast.declarations.AttribAlign;
import dtool.ast.declarations.AttribAtKeyword;
import dtool.ast.declarations.AttribBasic;
import dtool.ast.declarations.AttribBasic.AttributeKinds;
import dtool.ast.declarations.AttribCustom;
import dtool.ast.declarations.AttribLinkage;
import dtool.ast.declarations.AttribLinkage.Linkage;
import dtool.ast.declarations.AttribPragma;
import dtool.ast.declarations.AttribProtection;
import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.declarations.Attribute;
import dtool.ast.declarations.DeclList;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationDebugVersion;
import dtool.ast.declarations.DeclarationDebugVersionSpec;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.MissingDeclaration;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.statements.IStatement;
import dtool.parser.DeeParser_Definitions.DefinitionStartInfo;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;


public abstract class DeeParser_Declarations extends DeeParser_Parameters {
	
	public NodeResult<DeclarationImport> parseDeclarationImport() {
		ParseHelper parse = new ParseHelper(lookAheadElement().getStartPos());
		
		boolean isStatic = false;
		if(tryConsume(DeeTokens.KW_IMPORT)) {
		} else if(tryConsume(DeeTokens.KW_STATIC, DeeTokens.KW_IMPORT)) {
			isStatic = true;
		} else {
			return null;
		}
		
		ArrayList<IImportFragment> fragments = new ArrayList<IImportFragment>();
		do {
			IImportFragment fragment = parseImportFragment();
			assertNotNull(fragment);
			fragments.add(fragment);
		} while(tryConsume(DeeTokens.COMMA));
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DeclarationImport(isStatic, arrayView(fragments)));
	}
	
	public IImportFragment parseImportFragment() {
		ProtoDefSymbol aliasId = null;
		
		IImportFragment fragment;
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN
			|| lookAhead() == DeeTokens.ASSIGN) {
			aliasId = parseDefId();
			ParseHelper parse = new ParseHelper(aliasId.getStartPos());
			consumeLookAhead(DeeTokens.ASSIGN);
			
			RefModule refModule = parseRefModule();
			fragment = parse.conclude(new ImportAlias(aliasId, refModule));
		} else {
			RefModule refModule = parseRefModule();
			fragment = conclude(srOf(refModule, new ImportContent(refModule)));
		}
		
		if(tryConsume(DeeTokens.COLON)) {
			return parseSelectiveModuleImport(fragment);
		}
		
		return fragment;
	}
	
	public RefModule parseRefModule() {
		ArrayList<IToken> packages = new ArrayList<IToken>(2);
		
		ParseHelper parse = new ParseHelper(-1);
		while(true) {
			BaseLexElement id = parse.consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id);
			} else {
				parse.setStartPosition(packages.size() > 0 ? packages.get(0).getStartPos() : id.getStartPos());
				return parse.conclude(new RefModule(arrayViewG(packages), id.getSourceValue()));
			}
		}
	}
	
	public ImportSelective parseSelectiveModuleImport(IImportFragment fragment) {
		ParseHelper parse = new ParseHelper(fragment.asNode());
		ArrayList<IImportSelectiveSelection> selFragments = new ArrayList<IImportSelectiveSelection>();
		
		do {
			IImportSelectiveSelection importSelSelection = parseImportSelectiveSelection();
			selFragments.add(importSelSelection);
			
		} while(tryConsume(DeeTokens.COMMA));
		
		return parse.conclude(new ImportSelective(fragment, arrayView(selFragments)));
	}
	
	public IImportSelectiveSelection parseImportSelectiveSelection() {
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN
			|| lookAhead() == DeeTokens.ASSIGN) {
			ProtoDefSymbol defId = parseDefId();
			consumeLookAhead(DeeTokens.ASSIGN);
			ParseHelper parse = new ParseHelper(defId.getStartPos());
			
			RefImportSelection refImportSelection = parseRefImportSelection();
			return parse.conclude(new ImportSelectiveAlias(defId, refImportSelection));
		} else {
			return parseRefImportSelection();
		}
	}
	
	public RefImportSelection parseRefImportSelection() {
		BaseLexElement lexToken = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return conclude(lexToken.getMissingError(), srOf(lexToken, new RefImportSelection(idTokenToString(lexToken))));
	}
	
	public static final ParseRuleDescription RULE_DECLBODY = 
		new ParseRuleDescription("DeclOrBlock", "Declaration or Block");
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public ASTNode declList;
		
		public AttribBodyParseRule parseAttribBody(ParseHelper parse, boolean acceptEmptyDecl, 
			DefinitionStartInfo defStartInfo, boolean autoDeclEnabled) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else {
				parseDeclBlockOrSingle(parse, acceptEmptyDecl, defStartInfo, autoDeclEnabled);
			}
			return this;
		}
		
		public AttribBodyParseRule parseDeclBlockOrSingle(ParseHelper parse, boolean acceptEmptyDecl, 
			DefinitionStartInfo defStartInfo, boolean autoDeclEnabled) {
			if(lookAhead() == DeeTokens.OPEN_BRACE) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parse.checkResult(thisParser().parseDeclarationBlock());
			} else {
				declList = parse.checkResult(thisParser().parseDeclaration(false, autoDeclEnabled, defStartInfo));
				if(declList == null) {
					declList = parseMissingDeclaration(RULE_DECLBODY);
				} else if(declList instanceof DeclarationEmpty && !acceptEmptyDecl) {
					parse.storeError(createSyntaxError(DeeParser.RULE_DECLARATION));
				}
			}
			return this;
		}
	}
	
	protected DeclList parseDeclList(DeeTokens bodyListTerminator) {
		ParseHelper parse = new ParseHelper(getSourcePosition());
		
		ArrayView<ASTNode> declDefs = thisParser().parseDeclarations(bodyListTerminator, false);
		advanceSubChannelTokens();
		return parse.conclude(new DeclList(declDefs));
	}
	
	public MissingDeclaration parseMissingDeclaration(ParseRuleDescription expectedRule) {
		int nodeStart = getSourcePosition();
		advanceSubChannelTokens();
		ParserError error = createErrorExpectedRule(expectedRule);
		return conclude(error, srToPosition(nodeStart, new MissingDeclaration()));
	}
	
	public static ASTNodeTypes getLastAttributeKind(ArrayView<Attribute> attributes) {
		if(attributes == null) {
			return ASTNodeTypes.NULL;
		}
		assertTrue(attributes.size() > 0);
		Attribute lastAttrib = attributes.get(attributes.size() - 1);
		return lastAttrib.getNodeType();
	}
	
	public NodeResult<AttribLinkage> parseAttribLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		String linkageStr = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				linkageStr = "";
				
				LexElement linkageToken = consumeIf(DeeTokens.IDENTIFIER);
				if(linkageToken != null ) {
					linkageStr = linkageToken.getSourceValue();
					if(linkageStr.equals("C") && tryConsume(DeeTokens.INCREMENT)) {
						linkageStr = Linkage.CPP.name;
					}
				}
				
				if(Linkage.fromString(linkageStr) == null) {
					parse.storeError(createErrorOnLastToken(ParserErrorTypes.INVALID_EXTERN_ID, null));
				}
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
		}
		
		return parse.resultConclude(new AttribLinkage(linkageStr));
	}
	
	public NodeResult<AttribAlign> parseAttribAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		BaseLexElement alignNum = null;
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				alignNum = consumeExpectedContentToken(DeeTokens.INTEGER_DECIMAL);
				parse.storeError(alignNum.getMissingError());
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
		}
		
		return parse.resultConclude(new AttribAlign(alignNum));
	}
	
	public NodeResult<AttribPragma> parseAttribPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Symbol pragmaId = null;
		NodeListView<Expression> expList = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			pragmaId = parseIdSymbol();
			
			if(tryConsume(DeeTokens.COMMA)) {
				expList = parseExpArgumentList(parse, false, DeeTokens.CLOSE_PARENS);
			} else {
				parse.consumeRequired(DeeTokens.CLOSE_PARENS);
			}
			if(parse.ruleBroken) break parsing;
		}
		
		return parse.resultConclude(new AttribPragma(pragmaId, expList));
	}
	
	public NodeResult<AttribProtection> parseAttribProtection() {
		if(lookAheadGrouped() != DeeTokens.GROUP_PROTECTION_KW) {
			return null;
		}
		LexElement protToken = consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		EProtection protection = DeeTokenSemantics.getProtectionFromToken(protToken.type);
		
		return parse.resultConclude(new AttribProtection(protection));
	}
	
	public NodeResult<AttribBasic> parseAttribBasic() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null)
			return null;
		
		consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		if(attrib == AttributeKinds.DEPRECATED) {
			parseExpressionAroundParentheses(parse, false, false);
			// TODO: tests for this, confirm spec
		}
		return parse.resultConclude(new AttribBasic(attrib));
	}
	
	public static final ParseRuleDescription RULE_ID_OR_EXP_ARGLIST = 
		new ParseRuleDescription("IdOrExpArgList", "ID or (<Expression List>)");
	
	public NodeResult<? extends Attribute> parseAmpersatAttrib() {
		if(!tryConsume(DeeTokens.AT)) 
			return null;
		
		ParseHelper parse = new ParseHelper();
		
		RefIdentifier refId = null;
		NodeListView<Expression> args = null;
		
		if(lookAhead() == DeeTokens.IDENTIFIER && DeeTokenSemantics.isPredefinedAttribId(lookAheadElement())) {
			BaseLexElement traitsId = consumeLookAhead(DeeTokens.IDENTIFIER);
			Symbol attribIdentifier = conclude(srOf(traitsId, new Symbol(traitsId.getSourceValue())));
			return parse.resultConclude(new AttribAtKeyword(attribIdentifier));
		}
		
		parsing: {
			refId = attemptParseRefIdentifier();
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				args = parseExpArgumentList(parse, true, DeeTokens.CLOSE_PARENS);
			} else {
				parse.storeError(refId == null ?
					createErrorExpectedRule(RULE_ID_OR_EXP_ARGLIST) : 
					createExpectedTokenError(DeeTokens.OPEN_PARENS));
			}
		}
		return parse.resultConclude(new AttribCustom(refId, args));		
	}
	
	public NodeResult<DeclarationStaticIf> parseDeclarationStaticIf(boolean isStatement) {
		ParseHelper parse = new ParseHelper(lookAheadElement());
		if(!tryConsume(DeeTokens.KW_STATIC, DeeTokens.KW_IF))
			return null;
		
		Expression exp = null;
		ConditionalBodyParseRule body = new ConditionalBodyParseRule();
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			exp = parseAssignExpression_toMissing();
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			body.parseConditionalBody(parse, isStatement);
		}
		if(isStatement) {
			return parse.resultConclude(new DeclarationStaticIf(exp, body.thenBodySt, body.elseBodySt));
		}
		return parse.resultConclude(new DeclarationStaticIf(exp, body.bodySyntax, body.declList, body.elseBody));
	}
	
	public NodeResult<DeclarationDebugVersion> parseDeclarationDebugVersion(boolean isStatement) {
		if(!(tryConsume(DeeTokens.KW_DEBUG) || tryConsume(DeeTokens.KW_VERSION)))
			return null;
		boolean isDebug = lastLexElement().type == DeeTokens.KW_DEBUG;
		ParseHelper parse = new ParseHelper();
		
		VersionSymbol value = null;
		ConditionalBodyParseRule body = new ConditionalBodyParseRule();
		
		parsing: {
			if(parse.consume(DeeTokens.OPEN_PARENS, isDebug, true)) {
				if(lookAhead() == DeeTokens.KW_ASSERT || lookAhead() == DeeTokens.KW_UNITTEST) {
					value = createVersionSymbol(consumeLookAhead());
				} else {
					value = parseConditionalValue(isDebug, parse);
				}
				parse.consumeRequired(DeeTokens.CLOSE_PARENS);
			}
			if(parse.ruleBroken) break parsing;
			
			body.parseConditionalBody(parse, isStatement);
		}
		if(isStatement) {
			return parse.resultConclude(new DeclarationDebugVersion(isDebug, value, body.thenBodySt, body.elseBodySt));
		}
		return parse.resultConclude(
			new DeclarationDebugVersion(isDebug, value, body.bodySyntax, body.declList, body.elseBody));
	}
	
	protected class ConditionalBodyParseRule extends AttribBodyParseRule {
		
		public ASTNode elseBody = null;
		public IStatement thenBodySt = null;
		public IStatement elseBodySt = null;
		
		public void parseConditionalBody(ParseHelper parse, boolean isStatement) {
			
			if(isStatement) {
				thenBodySt = parse.checkResult(thisParser().parseUnscopedStatement_toMissing()); 
				if(parse.ruleBroken) return;
				
				if(tryConsume(DeeTokens.KW_ELSE)) {
					elseBodySt = parse.checkResult(thisParser().parseUnscopedStatement_toMissing());
				}
			} else {
				parseAttribBody(parse, false, null, false);
				if(parse.ruleBroken) return;
				
				if(bodySyntax != AttribBodySyntax.COLON) {
					if(tryConsume(DeeTokens.KW_ELSE)) {
						elseBody = new AttribBodyParseRule().
							parseDeclBlockOrSingle(parse, false, null, false).declList;
					}
				}
			}
		}
		
	}
	
	/* ----------------------------------------- */
	
	public static final ParseRuleDescription RULE_DEBUG_ARG = 
		new ParseRuleDescription("DebugArg", "DebugArgument");
	public static final ParseRuleDescription RULE_VERSION_ARG = 
		new ParseRuleDescription("VersionArg", "VersionArgument");
	
	public NodeResult<DeclarationDebugVersionSpec> parseDeclarationDebugVersionSpec() {
		if(!(tryConsume(DeeTokens.KW_DEBUG) || tryConsume(DeeTokens.KW_VERSION)))
			return null;
		boolean isDebug = lastLexElement().type == DeeTokens.KW_DEBUG;
		ParseHelper parse = new ParseHelper();
		
		VersionSymbol value = null;
		if(parse.consumeExpected(DeeTokens.ASSIGN)) {
			value = parseConditionalValue(isDebug, parse);
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new DeclarationDebugVersionSpec(isDebug, value));
	}
	
	protected VersionSymbol parseConditionalValue(boolean isDebug, ParseHelper parse) {
		if(lookAhead() == DeeTokens.IDENTIFIER || lookAheadGrouped() == DeeTokens.GROUP_INTEGER) {
			return createVersionSymbol(consumeLookAhead());
		} else { 
			parse.storeError(createErrorExpectedRule(isDebug ? RULE_DEBUG_ARG : RULE_VERSION_ARG));
			return createVersionSymbol(consumeSubChannelTokensNoError());
		}
	}
	
	public VersionSymbol createVersionSymbol(BaseLexElement token) {
		return conclude(srOf(token, new VersionSymbol(token.getSourceValue())));
	}
	
	public NodeResult<DeclarationStaticAssert> parseDeclarationStaticAssert() {
		ParseHelper parse = new ParseHelper(lookAheadElement());
		if(!tryConsume(DeeTokens.KW_STATIC, DeeTokens.KW_ASSERT)) 
			return null;
		
		Expression pred = null;
		Expression msg = null;
		
		if(parse.consumeExpected(DeeTokens.OPEN_PARENS)) {
			
			pred = parseAssignExpression_toMissing();
			if(tryConsume(DeeTokens.COMMA)) {
				msg = parseAssignExpression_toMissing();
			}
			
			parse.consumeExpected(DeeTokens.CLOSE_PARENS);
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new DeclarationStaticAssert(pred, msg));
	}
	
	public NodeResult<DeclarationMixinString> parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN))
			return null;
		ParseHelper parse = new ParseHelper();
		Expression exp = null;
		
		if(parse.consumeExpected(DeeTokens.OPEN_PARENS)) {
			exp = parseExpression_toMissing();
			parse.consumeExpected(DeeTokens.CLOSE_PARENS);
		}
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DeclarationMixinString(exp));
	}
	
}