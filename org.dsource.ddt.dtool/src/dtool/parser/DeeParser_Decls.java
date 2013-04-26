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

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationBasicAttrib.AttributeKinds;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.InvalidDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.definitions.DeclarationMixin;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasDecl;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionFunction.AutoReturnReference;
import dtool.ast.definitions.DefinitionFunction.FunctionAttributes;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.DefinitionVariable.DefinitionAutoVariable;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.NamedMixinDeclaration;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.TemplateAliasParam;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerArray;
import dtool.ast.expressions.InitializerArray.ArrayInitEntry;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerStruct.StructInitEntry;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefTypeFunction;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.EmptyBodyStatement;
import dtool.ast.statements.FunctionBody;
import dtool.ast.statements.FunctionBodyOutBlock;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.InOutFunctionBody;
import dtool.parser.DeeParser.DeeParserState;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public abstract class DeeParser_Decls extends DeeParser_RefOrExp {
	
	/* ----------------------------------------------------------------- */
	
	public ProtoDefSymbol defSymbol(BaseLexElement id) {
		// possible bug here, should be srEffectiveRange
		return new ProtoDefSymbol(id.getSourceValue(), id.getSourceRange(), id.getError());
	}
	
	public ProtoDefSymbol parseDefId() {
		BaseLexElement defId = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		return defSymbol(defId);
	}
	
	public ProtoDefSymbol nullIdToMissingDefId(ProtoDefSymbol defId) {
		if(defId == null) {
			return defSymbol(createExpectedToken(DeeTokens.IDENTIFIER));
		}
		return defId;
	}
	
	/* ----------------------------------------------------------------- */
	
	public AbstractParser.NodeResult<Module> parseModule() {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNeoNode> members = parseDeclDefs(null);
		assertTrue(lookAhead() == DeeTokens.EOF);
		consumeSubChannelTokens(); // Ensure pending whitespace is consumed as well
		assertTrue(getLexPosition() == getSource().length());
		
		SourceRange modRange = new SourceRange(0, getSource().length());
		
		if(md != null) {
			return result(false, conclude(modRange, new Module(md.getModuleSymbol(), null, md, members)));
		} else {
			return result(false, conclude(modRange, Module.createModuleNoModuleDecl("_unnamed"/*BUG here*/, members)));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(!tryConsume(DeeTokens.KW_MODULE)) {
			return null;
		}
		ParseHelper parse = new ParseHelper();
		
		ArrayList<Token> packagesList = new ArrayList<Token>(0);
		BaseLexElement moduleId;
		
		while(true) {
			BaseLexElement id = parse.consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.getToken());
				id = null;
			} else {
				parse.consumeRequired(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		return parse.conclude(new DeclarationModule(arrayViewG(packagesList), moduleId));
	}
	
	public ArrayView<ASTNeoNode> parseDeclDefs(DeeTokens nodeListTerminator) {
		ArrayList<ASTNeoNode> declarations = new ArrayList<ASTNeoNode>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			ASTNeoNode decl = parseDeclaration().node;
			if(decl == null) { 
				break;
			}
			declarations.add(decl);
		}
		
		return arrayView(declarations);
	}
	
	/* --------------------- DECLARATION --------------------- */
	
	public static final ParseRuleDescription RULE_DECLARATION = new ParseRuleDescription("Declaration");
	
	public NodeResult<? extends ASTNeoNode> parseDeclaration() {
		return parseDeclaration(true, false);
	}
	
	/** This rule always returns a node, except only on EOF where it returns null. */
	public NodeResult<? extends ASTNeoNode> parseDeclaration(boolean acceptEmptyDecl, boolean precedingIsSTCAttrib) {
		DeeTokens laGrouped = assertNotNull_(lookAheadGrouped());
		
		if(laGrouped == DeeTokens.EOF) {
			return nullResult();
		}
		
		switch (laGrouped) {
		case KW_IMPORT: return parseImportDeclaration();
		
		
		case KW_ALIGN: return result(parseDeclarationAlign());
		case KW_PRAGMA: return result(parseDeclarationPragma());
		case PROTECTION_KW: return result(parseDeclarationProtection());
		case KW_EXTERN:
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return result(parseDeclarationExternLinkage());
			}
			return result(parseDeclarationBasicAttrib());
		case ATTRIBUTE_KW: 
			if(lookAhead() == DeeTokens.KW_STATIC && lookAhead(1) == DeeTokens.KW_IMPORT) { 
				return parseImportDeclaration();
			}
			if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
				break; // go to parseReference
			}
			return result(parseDeclarationBasicAttrib());
		case AT:
			// TODO:
			
//				if((lookAhead(1) == DeeTokens.IDENTIFIER)) {
//					LexElement atId = lookAheadElement(1);
//					if(isSpecialAtToken(atId)) {
//						return parseDeclarationBasicAttrib();
//					}
//				}
			break;
		case KW_AUTO:
			if(lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.OPEN_PARENS) {
				return matchAutoReturnFunction();
			}
		case KW_ENUM:
			return result(parseDeclarationBasicAttrib());
			
		case KW_TEMPLATE: 
			return parseTemplateDefinition();
		case KW_MIXIN: 
			if(lookAhead(1) == DeeTokens.KW_TEMPLATE) {
				return parseTemplateDefinition();
			}
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return result(parseDeclarationMixinString());
			}
			return result(parseDeclarationMixin());
		case KW_ALIAS:
			if(lookAhead(1) == DeeTokens.KW_THIS ||  
				(lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.KW_THIS)) {
				return parseDeclarationAliasThis();
			}
			return parseAliasDefinition();
		default:
			break;
		}
		
		NodeResult<Reference> startRef = parseTypeReference_do(false); // This parses (BasicType + BasicType2) of spec
		if(startRef.node != null) {
			Reference ref = startRef.node;
			
			if(startRef.ruleBroken) {
				return resultConclude(true, srToPosition(ref, new InvalidDeclaration(ref, false)));
			}
			
			if(precedingIsSTCAttrib &&
				ref.getNodeType() == ASTNodeTypes.REF_IDENTIFIER && lookAhead(0) != DeeTokens.IDENTIFIER) {
				LexElement id = lastLexElement(); // Parse as auto declaration instead
				return parseDefinitionVariable_Reference_Identifier(null, id);
			}
			
			return parseDeclaration_referenceStart(ref);
		}
		
		if(tryConsume(DeeTokens.SEMICOLON)) {
			ParseHelper parse = new ParseHelper();
			if(!acceptEmptyDecl) {
				parse.store(createSyntaxError(RULE_DECLARATION));
			}
			return parse.resultConclude(new DeclarationEmpty());
		} else {
			Token badToken = consumeLookAhead();
			ParseHelper parse = new ParseHelper();
			parse.store(createSyntaxError(RULE_DECLARATION));
			return parse.resultConclude(new InvalidSyntaxElement(badToken));
		}
	}
	
	protected NodeResult<? extends ASTNeoNode> parseDeclaration_referenceStart(Reference ref) {
		assertNotNull(ref);
		if(lookAhead() == DeeTokens.IDENTIFIER) {
			LexElement defId = consumeInput();
			
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionFunction_Reference_Identifier(ref, defId);
			}
			
			return parseDefinitionVariable_Reference_Identifier(ref, defId);
		} else {
			ParseHelper parse = new ParseHelper(ref);
			parse.consumeExpected(DeeTokens.IDENTIFIER);
			boolean consumedSemiColon = tryConsume(DeeTokens.SEMICOLON);
			parse.ruleBroken = !consumedSemiColon;
			return parse.resultConclude(new InvalidDeclaration(ref, consumedSemiColon));
		}
	}
	
	protected NodeResult<? extends ASTNeoNode> matchAutoReturnFunction() {
		LexElement autoToken = consumeLookAhead(DeeTokens.KW_AUTO);
		AutoReturnReference autoReturn = conclude(srOf(autoToken, new AutoReturnReference()));
		LexElement id = consumeLookAhead(DeeTokens.IDENTIFIER);
		return parseDefinitionFunction_Reference_Identifier(autoReturn, id);
	}
	
	/* ----------------------------------------- */
	
	
	protected NodeResult<? extends DefinitionVariable> parseDefinitionVariable_Reference_Identifier(
		Reference ref, LexElement defId) 
	{
		ArrayList<DefinitionVarFragment> fragments = new ArrayList<DefinitionVarFragment>();
		Initializer init = null;
		
		final boolean isAutoRef = ref == null;
		ParseHelper parse = new ParseHelper(isAutoRef ? defId.getStartPos() : ref.getStartPos());
		
		if(tryConsume(DeeTokens.ASSIGN)){ 
			init = parseInitializer().node;
		} else if(isAutoRef) {
			parse.store(createExpectedTokenError(DeeTokens.ASSIGN));
		}
		
		while(tryConsume(DeeTokens.COMMA)) {
			DefinitionVarFragment defVarFragment = parseVarFragment(isAutoRef);
			fragments.add(defVarFragment);
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		if(isAutoRef) {
			parse.ruleBroken = false; // TODO
			return parse.resultConclude(new DefinitionAutoVariable(defSymbol(defId), init, arrayView(fragments)));
		}
		
		return parse.resultConclude(new DefinitionVariable(defSymbol(defId), ref, init, arrayView(fragments)));
	}
	
	protected DefinitionVarFragment parseVarFragment(boolean isAutoRef) {
		ProtoDefSymbol fragId = parseDefId();
		ParseHelper parse = new ParseHelper(fragId.getStartPos());
		Initializer init = null;
		
		if(!fragId.isMissing()) {
			if(tryConsume(DeeTokens.ASSIGN)){ 
				init = parseInitializer().node;
			} else if(isAutoRef) {
				parse.store(createExpectedTokenError(DeeTokens.ASSIGN));
			}
		}
		return parse.conclude(new DefinitionVarFragment(fragId, init));
	}
	
	public static final ParseRuleDescription RULE_INITIALIZER = new ParseRuleDescription("Initializer");
	
	public NodeResult<? extends Initializer> parseInitializer() {
		if(tryConsume(DeeTokens.KW_VOID)) {
			return resultConclude(false, srOf(lastLexElement(), new InitializerVoid()));
		}
		
		return parseNonVoidInitializer(true);
	}
	
	public NodeResult<? extends Initializer> parseNonVoidInitializer(boolean createMissing) {
		if(lookAhead() == DeeTokens.OPEN_BRACKET) {
			return parseArrayInitializer();
		}
		if(lookAhead() == DeeTokens.OPEN_BRACE) {
			DeeParserState savedParserState = thisParser().enterBacktrackableMode();
			NodeResult<InitializerStruct> structInitResult = parseStructInitializer();
			
			if(!structInitResult.ruleBroken) {
				return structInitResult;
			} else {
				thisParser().restoreOriginalState(savedParserState);
			}
		}
		
		NodeResult<Expression> expResult = parseAssignExpression_Rule(createMissing, 
			createMissing ? RULE_INITIALIZER : null);
		Expression exp = expResult.node;
		if(exp == null) {
			return nullResult();
		}
		return resultConclude(expResult.ruleBroken, srBounds(exp, new InitializerExp(exp)));
	}
	
	public NodeResult<InitializerArray> parseArrayInitializer() {
		if(!tryConsume(DeeTokens.OPEN_BRACKET))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		ArrayList<ArrayInitEntry> entries = new ArrayList<>();
		boolean endingComma = false;
		
		if(tryConsume(DeeTokens.CLOSE_BRACKET)) {
			
		} else while(true) {
			
			ArrayInitEntry initEntry = parseArrayInitEntry(lookAhead() != DeeTokens.COMMA);
			if(initEntry != null) {
				entries.add(initEntry);
				endingComma = false;
			}
			
			if(tryConsume(DeeTokens.COMMA)) {
				endingComma = true;
				if(tryConsume(DeeTokens.CLOSE_BRACE)) {
					break;
				} 
				continue;
			}
			parse.consumeRequired(DeeTokens.CLOSE_BRACKET);
			break;
		}
		
		return parse.resultConclude(new InitializerArray(arrayView(entries), endingComma));
	}
	
	public NodeResult<InitializerStruct> parseStructInitializer() {
		if(!tryConsume(DeeTokens.OPEN_BRACE))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		ArrayList<StructInitEntry> entries = new ArrayList<>();
		boolean endingComma = false;

		if(tryConsume(DeeTokens.CLOSE_BRACE)) {
			
		} else while(true) {
			
			StructInitEntry initEntry = parseStructInitEntry(lookAhead() != DeeTokens.COMMA);
			if(initEntry != null) {
				entries.add(initEntry);
				endingComma = false;
			}
			
			if(tryConsume(DeeTokens.COMMA)) {
				endingComma = true;
				if(tryConsume(DeeTokens.CLOSE_BRACE)) {
					break;
				} 
				continue;
			}
			parse.consumeRequired(DeeTokens.CLOSE_BRACE);
			break;
		}
		
		return parse.resultConclude(new InitializerStruct(arrayView(entries), endingComma));
	}
	
	public ArrayInitEntry parseArrayInitEntry(boolean nullIfMissing) {
		Expression index = null;
		Initializer initializer = null;
		
		if(lookAhead() == DeeTokens.COLON) {
			index = parseAssignExpression_toMissing();
			consumeLookAhead(DeeTokens.COLON);
			initializer = parseNonVoidInitializer(true).node;
		} else {
			initializer = parseNonVoidInitializer(!nullIfMissing).node;
			
			if(initializer == null)
				return null;
			
			if(initializer instanceof InitializerExp && lookAhead() == DeeTokens.COLON) {
				index = ((InitializerExp) initializer).exp;
				index.detachFromParent();
				consumeLookAhead(DeeTokens.COLON);
				initializer = parseNonVoidInitializer(true).node;
			}
		}
		
		ASTNeoNode startNode = index != null ? index : initializer;
		return conclude(srToPosition(startNode, new ArrayInitEntry(index, initializer)));
	}

	protected StructInitEntry parseStructInitEntry(boolean nullIfMissing) {
		RefIdentifier member = null;
		if(lookAhead() == DeeTokens.COLON || 
			(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.COLON)) {
			member = parseRefIdentifier();
			consumeLookAhead(DeeTokens.COLON);
		}
		Initializer init = parseNonVoidInitializer(!nullIfMissing || member != null).node;
		if(init == null)
			return null;
		
		ASTNeoNode startNode = member != null ? member : init;
		return conclude(srToPosition(startNode, new StructInitEntry(member, init)));
	}
	
	public static final ParseRuleDescription RULE_BODY = new ParseRuleDescription("Body");
	public static final ParseRuleDescription RULE_BLOCK = new ParseRuleDescription("Block");
	
	/**
	 * Parse a function from this point:
	 * http://dlang.org/declaration.html#DeclaratorSuffix
	 */
	protected NodeResult<DefinitionFunction> parseDefinitionFunction_Reference_Identifier(
		Reference retType, LexElement defId) {
		assertTrue(defId.isMissingElement() == false);
		
		ParseHelper parse = new ParseHelper(retType.getStartPos());
		
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<TemplateParameter> tplParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		Expression tplConstraint = null;
		IFunctionBody fnBody = null;
		
		parsing: {
			DeeParser_RuleParameters firstParams = parseParameters(parse);
			
			if(firstParams.mode == TplOrFnMode.FN) {
				fnParams = firstParams.getAsFunctionParameters();
			} else if(firstParams.mode == TplOrFnMode.TPL) {
				tplParams = firstParams.getAsTemplateParameters();
			}
			
			if(parse.ruleBroken) {
				if(firstParams.isAmbiguous()) {
					fnParams = firstParams.toFunctionParameters();
				}
				break parsing;
			}
			
			if(firstParams.isAmbiguous() && lookAhead() == DeeTokens.OPEN_PARENS) {
				/*BUG here*/
				tplParams = firstParams.toTemplateParameters();
			}
			
			if(tplParams != null) {
				fnParams = parseFunctionParameters(parse).getAsFunctionParameters();
				if(parse.ruleBroken) break parsing;
			} else if(firstParams.isAmbiguous()) {
				fnParams = firstParams.toFunctionParameters();
			}
			
			// Function attributes
			fnAttributes = parseFunctionAttributes();
			
			if(tplParams != null) {
				tplConstraint = parseTemplateConstraint(parse);
				if(parse.ruleBroken) break parsing;
			}
			
			if(tryConsume(DeeTokens.SEMICOLON)) { 
				fnBody = conclude(srOf(lastLexElement(), new EmptyBodyStatement()));
			} else {
				NodeResult<? extends IFunctionBody> resultFunctionBody = parseFunctionBody();
				fnBody = parse.storeResult(resultFunctionBody);
				if(fnBody == null) {
					parse.ruleBroken = true;
					parse.storeBreakError(createErrorExpectedRule(RULE_BODY));
				}
			}
		}
		
		return parse.resultConclude(new DefinitionFunction(
			defSymbol(defId), tplParams, retType, fnParams, fnAttributes, tplConstraint, fnBody));
	}
	
	protected ASTNeoNode matchTemplateAliasParameter() {
		consumeLookAhead(DeeTokens.KW_ALIAS);
		ParseHelper parse = new ParseHelper();
		
		ProtoDefSymbol defId;
		Resolvable init = null;
		Resolvable specialization = null;
		
		parsing: {
			defId = parse.storeResult(parseDefId());
			if(parse.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.COLON)) {
				specialization = nullTypeOrExpToMissing(parseTypeOrExpression(InfixOpType.CONDITIONAL, true).node);
			}
			if(tryConsume(DeeTokens.ASSIGN)) {
				init = nullTypeOrExpToMissing(parseTypeOrAssignExpression(true).node);
			}
		}
		
		return parse.conclude(new TemplateAliasParam(defId, specialization, init));
	}
	
	protected final DeeParser_RuleParameters parseParameters(ParseHelper parse) {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.AMBIG).parse(parse);
	}
	
	protected final DeeParser_RuleParameters parseFunctionParameters(ParseHelper parse) {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parse(parse);
	}
	
	protected final DeeParser_RuleParameters isFunctionParameters(ParseHelper parse) {
		// TODO: optimize unnecessary processing and object creation when in this decider mode
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parseDeciderMode(parse);
	}
	
	protected final DeeParser_RuleParameters parseTemplateParameters(ParseHelper parse) {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.TPL).parse(parse);
	}
	
	public IFunctionParameter parseFunctionParameter() {
		return (IFunctionParameter) new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parseParameter();
	}
	
	public TemplateParameter parseTemplateParameter() {
		return (TemplateParameter) new DeeParser_RuleParameters(thisParser(), TplOrFnMode.TPL).parseParameter();
	}
	
	protected ArrayView<FunctionAttributes> parseFunctionAttributes() {
		ArrayList<FunctionAttributes> attributes = null;
		
		while(true) {
			FunctionAttributes attrib = FunctionAttributes.fromToken(lookAhead());
			if(attrib != null) {
				consumeLookAhead();
			} else {
				if(lookAhead() == DeeTokens.AT && lookAhead(1) == DeeTokens.IDENTIFIER) {
					attrib = FunctionAttributes.fromCustomAttribId(lookAheadElement(1).token.source);
					if(attrib != null) {
						consumeLookAhead();
						consumeLookAhead();
					}
				}
				if(attrib == null)
					break;
			}
			attributes = NewUtils.lazyInitArrayList(attributes);
			attributes.add(attrib);
		}
		return arrayViewG(attributes);
	}
	
	public Expression parseTemplateConstraint(ParseHelper parse) {
		if(!tryConsume(DeeTokens.KW_IF)) {
			return null;
		}
		return parseExpressionAroundParentheses(parse, true);
	}
	
	protected NodeResult<? extends IFunctionBody> parseFunctionBody() {
		NodeResult<BlockStatement> blockResult = parseBlockStatement(false, false);
		if(blockResult.node != null)
			return blockResult;
		
		ParseHelper parse = new ParseHelper(-1);
		if(lookAhead() == DeeTokens.KW_IN || lookAhead() == DeeTokens.KW_OUT || lookAhead() == DeeTokens.KW_BODY) {
			parse.nodeStart = lookAheadElement().getStartPos();
		} else {
			parse.nodeStart = getLexPosition(); // It will be missing element
		}
		
		boolean isOutIn = false;
		BlockStatement inBlock = null;
		FunctionBodyOutBlock outBlock = null;
		BlockStatement bodyBlock = null;
		
		parsing: {
			if(tryConsume(DeeTokens.KW_IN)) {
				inBlock = parse.storeResult(parseBlockStatement_toMissing(false));
				if(parse.ruleBroken) break parsing;
				
				if(lookAhead() == DeeTokens.KW_OUT) {
					outBlock = parse.storeResult(parseOutBlock());
					if(parse.ruleBroken) break parsing;
				}
			} else if(lookAhead() == DeeTokens.KW_OUT) {
				isOutIn = true;
				
				outBlock = parse.storeResult(parseOutBlock());
				if(parse.ruleBroken) break parsing;
				
				if(tryConsume(DeeTokens.KW_IN)) {
					inBlock = parse.storeResult(parseBlockStatement_toMissing(false));
					if(parse.ruleBroken) break parsing;
				}
			}
			
			if(tryConsume(DeeTokens.KW_BODY)) {
				bodyBlock = parse.storeResult(parseBlockStatement_toMissing(false));
			}
			if(bodyBlock == null) {
				parse.store(createErrorExpectedRule(RULE_BODY));
				parse.ruleBroken = true;
			}
		}
		
		if(inBlock == null && outBlock == null) {
			if(bodyBlock == null) {
				return nullResult().<FunctionBody>upcastParam();
			}
			return parse.resultConclude(new FunctionBody(bodyBlock));
		}
		return parse.resultConclude(new InOutFunctionBody(isOutIn, inBlock, outBlock, bodyBlock));
	}
	
	protected BlockStatement createMissingBlock(boolean reportMissingExpError, ParseRuleDescription expectedRule) {
		ParserError error = reportMissingExpError ? createErrorExpectedRule(expectedRule) : null;
		int nodeStart = getLexPosition();
		return conclude(error, srToPosition(nodeStart, new BlockStatement()));
	}
	
	public NodeResult<BlockStatement> parseBlockStatement_toMissing() {
		return parseBlockStatement(true, true);
	}
	public NodeResult<BlockStatement> parseBlockStatement_toMissing(boolean brokenIfMissing) {
		return parseBlockStatement(true, brokenIfMissing);
	}
	
	protected NodeResult<BlockStatement> parseBlockStatement(boolean createMissing, boolean brokenIfMissing) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			if(createMissing) {
				return result(brokenIfMissing, createMissingBlock(true, RULE_BLOCK));
			}
			return nullResult(); 
		}
		ParseHelper parse = new ParseHelper();
		
		ArrayView<IStatement> body = parseStatements();
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		
		return parse.resultConclude(new BlockStatement(body, true));
	}
	
	private ArrayView<IStatement> parseStatements() {
		// TODO parse statements
		return CoreUtil.blindCast(parseDeclDefs(DeeTokens.CLOSE_BRACE));
	}
	
	protected NodeResult<FunctionBodyOutBlock> parseOutBlock() {
		if(!tryConsume(DeeTokens.KW_OUT))
			return result(null);
		ParseHelper parse = new ParseHelper();
		
		Symbol id = null;
		BlockStatement block = null;
		
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS) == false)
				break parsing;
			id = parseSymbol();
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS) == false)
				break parsing;
			
			block = parse.storeResult(parseBlockStatement_toMissing(false));
		}
		
		return parse.resultConclude(new FunctionBodyOutBlock(id, block));
	}
	
	public NodeResult<DefinitionTemplate> parseTemplateDefinition() {
		ParseHelper parse = new ParseHelper(lookAheadElement().getStartPos());
		boolean isMixin = false;
		if(tryConsume(DeeTokens.KW_MIXIN, DeeTokens.KW_TEMPLATE)) {
			isMixin = true;
		} else if(!tryConsume(DeeTokens.KW_TEMPLATE)) {
			return null;
		}
		
		ProtoDefSymbol defId = null;
		ArrayView<TemplateParameter> tplParams = null;
		Expression tplConstraint = null;
		NodeList2 declBody = null;
		
		parsing: {
			defId = parse.storeResult(parseDefId());
			if(parse.ruleBroken) break parsing;
			
			tplParams = parseTemplateParameters(parse).getAsTemplateParameters();
			if(parse.ruleBroken) break parsing;
			
			tplConstraint = parseTemplateConstraint(parse);
			if(parse.ruleBroken) break parsing;
			
			declBody = parseDeclarationBlock(parse, true);
		}
		
		return parse.resultConclude(new DefinitionTemplate(isMixin, defId, tplParams, tplConstraint, declBody));
	}
	
	public NodeList2 parseDeclarationBlock(ParseHelper parse, boolean required) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			parse.store(required ? createErrorExpectedRule(RULE_BLOCK) : null);
			return null;
		}
		NodeList2 declBody = parseDeclList(DeeTokens.CLOSE_BRACE);
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		return declBody;
	}
	
	@Override
	public NodeResult<RefTypeFunction> matchRefTypeFunction_afterReturnType(Reference retType) {
		boolean isDelegate = lastLexElement().token.type == DeeTokens.KW_DELEGATE;
		
		ParseHelper parse = new ParseHelper(retType);
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		
		parsing: {
			fnParams = parseFunctionParameters(parse).getAsFunctionParameters();
			if(parse.ruleBroken) break parsing;
			
			fnAttributes = parseFunctionAttributes();
		}
		
		return parse.resultConclude(new RefTypeFunction(retType, isDelegate, fnParams, fnAttributes));
	}
	
	public NodeResult<? extends ASTNeoNode> parseAliasDefinition() {
		if(!tryConsume(DeeTokens.KW_ALIAS))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN) {
			return parseDefinitionAlias_atFragmentStart();
		}
		
		Reference ref = null;
		ProtoDefSymbol defId = null;
		
		parsing: {
			DeeParserState savedParserState = thisParser().enterBacktrackableMode();
			
			NodeResult<Reference> refResult = parseTypeReference();
			ref = refResult.node;
			if(ref == null) {
				return parseDefinitionAlias_atFragmentStart(); // Return error as if trying to parse DefinitionAlias
			}
			
			if(refResult.ruleBroken) break parsing;
			
			if(lookAhead() != DeeTokens.IDENTIFIER && couldHaveBeenParsedAsId(ref)) {
				thisParser().restoreOriginalState(savedParserState);
				return parseDefinitionAlias_atFragmentStart(); // Return error as if trying to parse DefinitionAlias
			} else {
				defId = parseDefId();
			}
		}
		defId = nullIdToMissingDefId(defId);
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DefinitionAliasDecl(defId, ref));
	}
	
	protected NodeResult<DefinitionAlias> parseDefinitionAlias_atFragmentStart() {
		ParseHelper parse = new ParseHelper();
		
		ArrayList<DefinitionAliasFragment> fragments = new ArrayList<>();
		
		while(true) {
			DefinitionAliasFragment fragment = parseAliasFragment();
			fragments.add(fragment);
			
			if(!tryConsume(DeeTokens.COMMA)) {
				break;
			}
		}
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DefinitionAlias(arrayView(fragments)));
	}
	
	public DefinitionAliasFragment parseAliasFragment() {
		ProtoDefSymbol defId = parseDefId();
		Reference ref = null;
		
		ParseHelper parse = new ParseHelper(defId.nameSourceRange.getStartPos());
		
		parsing: {
			parse.storeResult(defId);
			if(parse.ruleBroken) break parsing;
			
			if(parse.consumeRequired(DeeTokens.ASSIGN) == false) break parsing;
			
			NodeResult<Reference> refResult = parseTypeReference_ToMissing();
			ref = refResult.node;
		}
		return parse.conclude(new DefinitionAliasFragment(defId, ref));
	}
	
	protected NodeResult<DeclarationAliasThis> parseDeclarationAliasThis() {
		if(!tryConsume(DeeTokens.KW_ALIAS))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		boolean isAssignSyntax = false;
		RefIdentifier refId = null;
		
		parsing:
		if(tryConsume(DeeTokens.KW_THIS)) {
			isAssignSyntax = true;
			
			if(parse.consumeExpected(DeeTokens.ASSIGN) == false) break parsing;
			
			refId = parseRefIdentifier();
		} else {
			refId = parseRefIdentifier();
			parse.consumeExpected(DeeTokens.KW_THIS);
		}
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DeclarationAliasThis(isAssignSyntax, refId));
	}
	
	/* -------------------- Plain declarations -------------------- */
	
	public NodeResult<DeclarationImport> parseImportDeclaration() {
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
		parse.ruleBroken = false;
		return parse.resultConclude(new DeclarationImport(isStatic, arrayViewI(fragments)));
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
			fragment = conclude(srBounds(refModule, new ImportContent(refModule)));
		}
		
		if(tryConsume(DeeTokens.COLON)) {
			return parseSelectiveModuleImport(fragment);
		}
		
		return fragment;
	}
	
	public RefModule parseRefModule() {
		ArrayList<Token> packages = new ArrayList<Token>(0);
		
		ParseHelper parse = new ParseHelper(-1);
		while(true) {
			BaseLexElement id = parse.consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.getToken());
			} else {
				parse.nodeStart = packages.size() > 0 ? packages.get(0).getStartPos() : id.getStartPos();
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
		
		return parse.conclude(new ImportSelective(fragment, arrayViewI(selFragments)));
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
		SingleTokenParse parse = new SingleTokenParse(DeeTokens.IDENTIFIER);
		return parse.conclude(new RefImportSelection(idTokenToString(parse.lexToken)));
	}
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public ASTNeoNode declList;
		
		public AttribBodyParseRule parseAttribBody(ParseHelper parse, boolean acceptEmptyDecl, 
			boolean enablesAutoDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				parse.consumeRequired(DeeTokens.CLOSE_BRACE);
			} else {
				declList = parseDeclaration(acceptEmptyDecl, enablesAutoDecl).node;
				if(declList == null) {
					parse.storeBreakError(createErrorExpectedRule(RULE_DECLARATION));
				}
			}
			return this;
		}
	}
	
	protected NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		ParseHelper parse = new ParseHelper(getLexPosition());
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		consumeSubChannelTokens();
		return parse.conclude(new NodeList2(declDefs));
	}
	
	public DeclarationLinkage parseDeclarationExternLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		String linkageStr = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
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
					parse.store(createErrorOnLastToken(ParserErrorTypes.INVALID_EXTERN_ID, null));
				}
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS) == false)
					break parsing;
			}
			ab.parseAttribBody(parse, false, false);
		}
		
		return parse.conclude(new DeclarationLinkage(linkageStr, ab.bodySyntax, ab.declList));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		String alignNum = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				BaseLexElement alignNumToken = consumeExpectedContentToken(DeeTokens.INTEGER_DECIMAL);
				alignNum = alignNumToken.getSourceValue();
				parse.store(alignNumToken.getError());
				
				if(parse.consumeExpected(DeeTokens.CLOSE_PARENS) == false) 
					break parsing;
			}
			ab.parseAttribBody(parse, false, false);
		}
		
		return parse.conclude(new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Symbol pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(parse.consumeRequired(DeeTokens.OPEN_PARENS)) {
			pragmaId = parseSymbol();
			
			// TODO pragma argument list;
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS)) {
				ab.parseAttribBody(parse, true, false);
			}
		}
		
		return parse.conclude(new DeclarationPragma(pragmaId, null, ab.bodySyntax, ab.declList));
	}
	
	public Symbol parseSymbol() {
		SingleTokenParse parse = new SingleTokenParse(DeeTokens.IDENTIFIER);
		return parse.conclude(new Symbol(parse.lexToken.getSourceValue()));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		LexElement protElement = consumeInput();
		ParseHelper parse = new ParseHelper();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(protElement.token.type);
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(parse, false, true);
		return parse.conclude(new DeclarationProtection(protection, ab.bodySyntax, ab.declList));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(parse, false, true);
		return parse.conclude(new DeclarationBasicAttrib(attrib, ab.bodySyntax, ab.declList));
	}
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN))
			return null;
		ParseHelper parse = new ParseHelper();
		Expression exp = null;
		
		if(parse.consumeExpected(DeeTokens.OPEN_PARENS)) {
			exp = parseExpression_toMissing();
			parse.consumeExpected(DeeTokens.CLOSE_PARENS);
		}
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.conclude(new DeclarationMixinString(exp));
	}
	
	public ASTNeoNode parseDeclarationMixin() {
		if(!tryConsume(DeeTokens.KW_MIXIN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		NodeResult<Reference> tplInstanceResult = parseTypeReference_ToMissing(true);
		Reference tplInstance = tplInstanceResult.node;
		
		if(!tplInstanceResult.ruleBroken && lookAhead() == DeeTokens.IDENTIFIER) {
			ProtoDefSymbol defId = parseDefId();
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.conclude(new NamedMixinDeclaration(tplInstance, defId));
		} else {
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.conclude(new DeclarationMixin(tplInstance));
		}
		
	}
	
}