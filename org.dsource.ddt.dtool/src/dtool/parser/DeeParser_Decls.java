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
import descent.internal.compiler.parser.Comment;
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
import dtool.ast.definitions.DefUnit.DefUnitTuple;
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
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.MissingExpression;
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
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.LexElement.MissingLexElement;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public abstract class DeeParser_Decls extends DeeParser_RefOrExp {
	
	/* ----------------------------------------------------------------- */
	
	// TODO: comments
	public DefUnitTuple defUnitNoComments(BaseLexElement id) {
		return defUnitTuple(id, null);
	}
	
	public DefUnitTuple defUnitTuple(BaseLexElement id, Comment[] comments) {
		return new DefUnitTuple(comments, id.getSourceValue(), id.getSourceRange(), null);
	}
	
	public BaseLexElement nullIdToMissingDefId(BaseLexElement id) {
		if(id == null) {
			return createExpectedToken(DeeTokens.IDENTIFIER);
		}
		return id;
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
			return connectResult(false, new Module(md.getModuleSymbol(), null, md, members, modRange));
		} else {
			return connectResult(false, Module.createModuleNoModuleDecl(modRange, "__unnamed" /*BUG here*/, members));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(!tryConsume(DeeTokens.KW_MODULE)) {
			return null;
		}
		int nodeStart = lastLexElement().getStartPos();
		
		ArrayList<Token> packagesList = new ArrayList<Token>(0);
		BaseLexElement moduleId;
		
		while(true) {
			BaseLexElement id = consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.getToken());
				id = null;
			} else {
				consumeExpectedToken(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		return connect(srToPosition(nodeStart, new DeclarationModule(arrayViewG(packagesList), moduleId)));
	}
	
	public ArrayView<ASTNeoNode> parseDeclDefs(DeeTokens nodeListTerminator) {
		ArrayList<ASTNeoNode> declarations = new ArrayList<ASTNeoNode>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			ASTNeoNode decl = getResult(parseDeclaration());
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
			return null;
		}
		
		switch (laGrouped) {
		case KW_IMPORT: return parseImportDeclaration();
		
		
		case KW_ALIGN: return nodeResult(parseDeclarationAlign());
		case KW_PRAGMA: return nodeResult(parseDeclarationPragma());
		case PROTECTION_KW: return nodeResult(parseDeclarationProtection());
		case KW_EXTERN:
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return nodeResult(parseDeclarationExternLinkage());
			}
			return nodeResult(parseDeclarationBasicAttrib());
		case ATTRIBUTE_KW: 
			if(lookAhead() == DeeTokens.KW_STATIC && lookAhead(1) == DeeTokens.KW_IMPORT) { 
				return parseImportDeclaration();
			}
			if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
				break; // go to parseReference
			}
			return nodeResult(parseDeclarationBasicAttrib());
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
			return nodeResult(parseDeclarationBasicAttrib());
			
		case KW_TEMPLATE: 
			return parseTemplateDefinition();
		case KW_MIXIN: 
			if(lookAhead(1) == DeeTokens.KW_TEMPLATE) {
				return parseTemplateDefinition();
			}
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return nodeResult(parseDeclarationMixinString());
			}
			return nodeResult(parseDeclarationMixin());
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
				return connectResult(true, srToPosition(ref, new InvalidDeclaration(ref, false)));
			}
			
			if(precedingIsSTCAttrib &&
				ref.getNodeType() == ASTNodeTypes.REF_IDENTIFIER && lookAhead(0) != DeeTokens.IDENTIFIER) {
				LexElement id = lastLexElement(); // Parse as auto declaration instead
				return parseDefinitionVariable_Reference_Identifier(null, id);
			}
			
			return parseDeclaration_referenceStart(ref);
		}
		
		if(tryConsume(DeeTokens.SEMICOLON)) {
			if(!acceptEmptyDecl) {
				reportSyntaxError(RULE_DECLARATION);
			}
			return connectResult(false, srToPosition(lastLexElement().getStartPos(), new DeclarationEmpty()));
		} else {
			Token badToken = consumeLookAhead();
			reportSyntaxError(RULE_DECLARATION);
			return connectResult(false, new InvalidSyntaxElement(badToken));
		}
	}
	
	protected NodeResult<? extends ASTNeoNode> parseDeclaration_referenceStart(Reference ref) {
		if(lookAhead() == DeeTokens.IDENTIFIER) {
			LexElement defId = consumeInput();
			
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionFunction_Reference_Identifier(ref, defId);
			}
			
			return parseDefinitionVariable_Reference_Identifier(ref, defId);
		} else {
			addExpectedTokenError(DeeTokens.IDENTIFIER);
			boolean consumedSemiColon = tryConsume(DeeTokens.SEMICOLON);
			return connectResult(!consumedSemiColon, 
				srToPosition(ref.getStartPos(), new InvalidDeclaration(ref, consumedSemiColon)));
		}
	}
	
	protected NodeResult<? extends ASTNeoNode> matchAutoReturnFunction() {
		LexElement autoToken = consumeLookAhead(DeeTokens.KW_AUTO);
		AutoReturnReference autoReturn = connect(sr(autoToken, new AutoReturnReference()));
		LexElement id = consumeLookAhead(DeeTokens.IDENTIFIER);
		return parseDefinitionFunction_Reference_Identifier(autoReturn, id);
	}
	
	/* ----------------------------------------- */
	
	
	protected NodeResult<? extends DefinitionVariable> parseDefinitionVariable_Reference_Identifier(
		Reference ref, LexElement defId) 
	{
		ArrayList<DefinitionVarFragment> fragments = new ArrayList<DefinitionVarFragment>();
		Initializer init = null;
		
		boolean isAutoRef = ref == null;
		
		if(attemptConsume(DeeTokens.ASSIGN, isAutoRef)){ 
			init = parseInitializer();
		}
		
		while(tryConsume(DeeTokens.COMMA)) {
			DefinitionVarFragment defVarFragment = parseVarFragment(isAutoRef);
			fragments.add(defVarFragment);
		}
		boolean ruleBroken = consumeExpectedToken(DeeTokens.SEMICOLON) == null;
		
		if(ref == null) {
			return connectResult(ruleBroken, srToPosition(defId.getStartPos(), 
				new DefinitionAutoVariable(defUnitNoComments(defId), init, arrayView(fragments))));
		}
		
		return connectResult(ruleBroken, srToPosition(ref, 
			new DefinitionVariable(defUnitNoComments(defId), ref, init, arrayView(fragments))));
	}
	
	protected DefinitionVarFragment parseVarFragment(boolean isAutoRef) {
		Initializer init = null;
		BaseLexElement fragId = consumeExpectedIdentifier();
		if(!fragId.isMissingElement()) {
			if(attemptConsume(DeeTokens.ASSIGN, isAutoRef)){ 
				init = parseInitializer();
			}
		}
		return connect(srToPosition(fragId, new DefinitionVarFragment(defUnitNoComments(fragId), init)));
	}
	
	public static final ParseRuleDescription RULE_INITIALIZER = new ParseRuleDescription("Initializer");
	
	public Initializer parseInitializer() {
		if(tryConsume(DeeTokens.KW_VOID)) {
			return connect(srToPosition(lastLexElement(), new InitializerVoid()));
		}
		
		Expression exp = parseAssignExpression().node;
		if(exp == null) {
			reportErrorExpectedRule(RULE_INITIALIZER);
			// Advance parser position, mark the advanced range as missing element:
			MissingLexElement missingLexElement = consumeSubChannelTokens();
			exp = connect(srEffective(missingLexElement, new MissingExpression()));
		}
		return connect(srBounds(exp, new InitializerExp(exp)));
	}
	
	public static final ParseRuleDescription RULE_BODY = new ParseRuleDescription("Body");
	public static final ParseRuleDescription RULE_BLOCK = new ParseRuleDescription("Block");
	
	/**
	 * Parse a function from this point:
	 * http://dlang.org/declaration.html#DeclaratorSuffix
	 */
	protected NodeResult<DefinitionFunction> parseDefinitionFunction_Reference_Identifier(
		Reference retType, LexElement defId) {
		
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<TemplateParameter> tplParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		Expression tplConstraint = null;
		IFunctionBody fnBody = null;
		
		boolean parseBroken = true;
		parsing: {
			DeeParser_RuleParameters firstParams = parseParameters();
			
			if(firstParams.mode == TplOrFnMode.FN) {
				fnParams = firstParams.getAsFunctionParameters();
			} else if(firstParams.mode == TplOrFnMode.TPL) {
				tplParams = firstParams.getAsTemplateParameters();
			}
			
			if(firstParams.properlyTerminated == false) {
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
				DeeParser_RuleParameters secondParams = parseFunctionParameters();
				fnParams = secondParams.getAsFunctionParameters();
				if(secondParams.properlyTerminated == false) break parsing;
			} else if(firstParams.isAmbiguous()) {
				fnParams = firstParams.toFunctionParameters();
			}
			
			// Function attributes
			fnAttributes = parseFunctionAttributes();
			
			if(tplParams != null) {
				NodeResult<Expression> tplConstraintResult = parseTemplateConstraint();
				tplConstraint = tplConstraintResult.node;
				if(tplConstraintResult.ruleBroken) break parsing;
			}
			
			if(tryConsume(DeeTokens.SEMICOLON)) { 
				fnBody = connect(sr(lastLexElement(), new EmptyBodyStatement()));
				parseBroken = false;
			} else {
				NodeResult<? extends IFunctionBody> resultFunctionBody = parseFunctionBody();
				fnBody = resultFunctionBody.node;
				parseBroken = resultFunctionBody.node == null || resultFunctionBody.ruleBroken;
			}
		}
		
		return connectResult(parseBroken, srToPosition(retType.getStartPos(), new DefinitionFunction(
			defUnitNoComments(defId), tplParams, retType, fnParams, fnAttributes, tplConstraint, fnBody)));
	}
	
	protected ASTNeoNode matchTemplateAliasParameter() {
		int nodeStart = lookAheadElement().getStartPos();
		consumeLookAhead(DeeTokens.KW_ALIAS);
		
		BaseLexElement id = null;
		Resolvable init = null;
		Resolvable specialization = null;
		
		parsing: {
			id = consumeExpectedIdentifier();
			if(id.isMissingElement()) break parsing;
			
			if(tryConsume(DeeTokens.COLON)) {
				specialization = nullTypeOrExpToMissing(parseTypeOrExpression(InfixOpType.CONDITIONAL, true).node);
			}
			if(tryConsume(DeeTokens.ASSIGN)) {
				init = nullTypeOrExpToMissing(parseTypeOrAssignExpression(true).node);
			}
		}
		
		return connect(srToPosition(nodeStart, 
			new TemplateAliasParam(defUnitTuple(id, null), specialization, init)));
	}
	
	protected final DeeParser_RuleParameters parseParameters() {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.AMBIG).parse();
	}
	
	protected final DeeParser_RuleParameters parseFunctionParameters() {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parse();
	}
	
	protected final DeeParser_RuleParameters isFunctionParameters() {
		// TODO: optimize unnecessary processing and object creation when in this decider mode
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parseDeciderMode();
	}
	
	protected final DeeParser_RuleParameters parseTemplateParameters() {
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.TPL).parse();
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
	
	public NodeResult<Expression> parseTemplateConstraint() {
		if(!tryConsume(DeeTokens.KW_IF)) {
			return nullResult();
		}
		return parseExpressionAroundParentheses(true);
	}
	
	protected NodeResult<? extends IFunctionBody> parseFunctionBody() {
		NodeResult<BlockStatement> blockResult = parseBlockStatement(false, false);
		if(blockResult.node != null)
			return blockResult;
		
		int nodeStart;
		if(lookAhead() == DeeTokens.KW_IN || lookAhead() == DeeTokens.KW_OUT || lookAhead() == DeeTokens.KW_BODY) {
			nodeStart = lookAheadElement().getStartPos();
		} else {
			nodeStart = getLexPosition(); // It will be missing element
		}
		
		boolean isOutIn = false;
		BlockStatement inBlock = null;
		FunctionBodyOutBlock outBlock = null;
		BlockStatement bodyBlock = null;
		
		boolean parseBroken = true;
		parsing: {
			if(tryConsume(DeeTokens.KW_IN)) {
				blockResult = parseBlockStatement_toMissing(false);
				inBlock = blockResult.node;
				if(blockResult.ruleBroken) break parsing;
				
				if(lookAhead() == DeeTokens.KW_OUT) {
					NodeResult<FunctionBodyOutBlock> outBlockResult = parseOutBlock();
					outBlock = outBlockResult.node;
					if(outBlockResult.ruleBroken) break parsing;
				}
			} else if(lookAhead() == DeeTokens.KW_OUT) {
				isOutIn = true;
				
				NodeResult<FunctionBodyOutBlock> outBlockResult = parseOutBlock();
				outBlock = outBlockResult.node;
				if(outBlockResult.ruleBroken) break parsing;
				
				if(tryConsume(DeeTokens.KW_IN)) {
					blockResult = parseBlockStatement_toMissing(false);
					inBlock = blockResult.node;
					if(blockResult.ruleBroken) break parsing;
				}
			}
			
			if(tryConsume(DeeTokens.KW_BODY)) {
				NodeResult<BlockStatement> blockStatementResult = parseBlockStatement_toMissing(false);
				bodyBlock = blockStatementResult.node;
				parseBroken = blockStatementResult.ruleBroken;
			}
			if(bodyBlock == null) {
				reportErrorExpectedRule(RULE_BODY);
				parseBroken = true;
			}
		}
		
		if(inBlock == null && outBlock == null) {
			if(bodyBlock == null) {
				return AbstractParser.<FunctionBody>nullResult();
			}
			return connectResult(parseBroken, srToPosition(nodeStart, new FunctionBody(bodyBlock)));
		}
		return connectResult(parseBroken, srToPosition(nodeStart, 
			new InOutFunctionBody(isOutIn, inBlock, outBlock, bodyBlock)));
	}
	
	protected BlockStatement createMissingBlock(boolean reportMissingExpError, ParseRuleDescription expectedRule) {
		if(reportMissingExpError) {
			reportErrorExpectedRule(expectedRule);
		}
		int nodeStart = getLexPosition();
		return connect(srToPosition(nodeStart, new BlockStatement()));
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
				return nodeResult(brokenIfMissing, createMissingBlock(true, RULE_BLOCK));
			}
			return nullResult(); 
		}
		int nodeStart = lastLexElement().getStartPos();
		
		ArrayView<IStatement> body = parseStatements();
		boolean parseBroken = consumeExpectedToken(DeeTokens.CLOSE_BRACE) == null; 
		return connectResult(parseBroken, srToPosition(nodeStart, new BlockStatement(body, true)));
	}
	
	private ArrayView<IStatement> parseStatements() {
		// TODO parse statements
		return CoreUtil.blindCast(parseDeclDefs(DeeTokens.CLOSE_BRACE));
	}
	
	protected NodeResult<FunctionBodyOutBlock> parseOutBlock() {
		if(!tryConsume(DeeTokens.KW_OUT))
			return nodeResult(null);
		int nodeStart = lastLexElement().getStartPos();
		
		boolean parseBroken = true;
		Symbol id = null;
		BlockStatement block = null;
		parsing: {
			if(consumeExpectedToken(DeeTokens.OPEN_PARENS) == null)
				break parsing;
			id = parseSymbol();
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null)
				break parsing;
			
			NodeResult<BlockStatement> blockResult = parseBlockStatement_toMissing(false);
			block = blockResult.node;
			parseBroken = blockResult.ruleBroken;
		}
		
		return connectResult(parseBroken, srToPosition(nodeStart, new FunctionBodyOutBlock(id, block)));
	}
	
	public NodeResult<DefinitionTemplate> parseTemplateDefinition() {
		int nodeStart = lookAheadElement().getStartPos();
		boolean isMixin = false;
		if(tryConsume(DeeTokens.KW_MIXIN, DeeTokens.KW_TEMPLATE)) {
			isMixin = true;
		} else if(!tryConsume(DeeTokens.KW_TEMPLATE)) {
			return null;
		}
		
		ArrayView<TemplateParameter> tplParams = null;
		Expression tplConstraint = null;
		NodeList2 declBody = null;
		
		BaseLexElement defId = consumeExpectedIdentifier();
		
		boolean ruleBroken = true;
		parsing: {
			if(defId.isMissingElement()) break parsing;
			
			DeeParser_RuleParameters tplParametersResult = parseTemplateParameters();
			tplParams = tplParametersResult.getAsTemplateParameters();
			if(!tplParametersResult.properlyTerminated) break parsing;
			
			NodeResult<Expression> tplConstraintResult = parseTemplateConstraint();
			tplConstraint = tplConstraintResult.node;
			if(tplConstraintResult.ruleBroken) break parsing;
			
			RuleResult<NodeList2> declBodyResult = parseDeclarationBlock(true);
			declBody = declBodyResult.result;
			ruleBroken = declBodyResult.ruleBroken;
		}
		
		return nodeResult(ruleBroken, connect(srToPosition(nodeStart,
			new DefinitionTemplate(isMixin, defUnitNoComments(defId), tplParams, tplConstraint, declBody))));
	}
	
	public RuleResult<NodeList2> parseDeclarationBlock(boolean required) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			if(required) {
				reportErrorExpectedRule(RULE_BLOCK);
			}
			return ruleResult(required, null);
		}
		NodeList2 declBody = parseDeclList(DeeTokens.CLOSE_BRACE);
		boolean ruleBroken = consumeExpectedToken(DeeTokens.CLOSE_BRACE) == null;
		return ruleResult(ruleBroken, declBody);
	}
	
	@Override
	public NodeResult<RefTypeFunction> matchRefTypeFunction_afterReturnType(Reference retType) {
		boolean isDelegate = lastLexElement().token.type == DeeTokens.KW_DELEGATE;
		
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		
		boolean ruleBroken = true;
		parsing: {
			DeeParser_RuleParameters fnParamsResult = parseFunctionParameters();
			fnParams = fnParamsResult.getAsFunctionParameters();
			if(!fnParamsResult.properlyTerminated) break parsing;
			
			fnAttributes = parseFunctionAttributes();
			ruleBroken = false;	
		}
		
		return connectResult(ruleBroken, srToPosition(retType, 
			new RefTypeFunction(retType, isDelegate, fnParams, fnAttributes)));
	}
	
	public NodeResult<? extends ASTNeoNode> parseAliasDefinition() {
		if(!tryConsume(DeeTokens.KW_ALIAS)) {
			return nullResult();
		}
		int nodeStart = lastLexElement().getStartPos();
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN) {
			return parseDefinitionAlias_atFragmentStart();
		}
		
		Reference ref = null;
		BaseLexElement id = null;
		
		parsing: {
			LexElementSource savedState = thisParser().getEnabledLexSource().saveState();
			
			NodeResult<Reference> refResult = parseTypeReference();
			ref = refResult.node;
			if(ref == null) {
				return parseDefinitionAlias_atFragmentStart(); // Return error as if trying to parse DefinitionAlias
			}
			
			if(refResult.ruleBroken) break parsing;
			
			if(lookAhead() != DeeTokens.IDENTIFIER && couldHaveBeenParsedAsId(ref)) {
				thisParser().getEnabledLexSource().resetState(savedState);
				return parseDefinitionAlias_atFragmentStart(); // Return error as if trying to parse DefinitionAlias
			} else {
				id = consumeExpectedIdentifier();
			}
		}
		id = nullIdToMissingDefId(id);
		
		boolean ruleBroken = consumeExpectedToken(DeeTokens.SEMICOLON) == null;
		return connectResult(ruleBroken, srToPosition(nodeStart, 
			new DefinitionAliasDecl(defUnitNoComments(id), ref)));
	}
	
	protected NodeResult<DefinitionAlias> parseDefinitionAlias_atFragmentStart() {
		int nodeStart = lastLexElement().getStartPos();
		
		ArrayList<DefinitionAliasFragment> fragments = new ArrayList<>();
		
		while(true) {
			DefinitionAliasFragment fragment = parseAliasFragment();
			fragments.add(fragment);
			
			if(!tryConsume(DeeTokens.COMMA)) {
				break;
			}
		}
		
		boolean ruleBroken = consumeExpectedToken(DeeTokens.SEMICOLON) == null;
		return connectResult(ruleBroken, srToPosition(nodeStart, new DefinitionAlias(arrayView(fragments))));
	}
	
	public DefinitionAliasFragment parseAliasFragment() {
		BaseLexElement id = null;
		Reference ref = null;
		
		parsing: {
			id = consumeExpectedIdentifier();
			if(id.isMissingElement()) break parsing;
			
			if(consumeExpectedToken(DeeTokens.ASSIGN) == null) break parsing;
			
			NodeResult<Reference> refResult = parseTypeReference_ToMissing();
			ref = refResult.node;
		}
		return connect(srToPosition(id, new DefinitionAliasFragment(defUnitNoComments(id), ref)));
	}
	
	protected NodeResult<DeclarationAliasThis> parseDeclarationAliasThis() {
		if(!tryConsume(DeeTokens.KW_ALIAS)) {
			return nullResult();
		}
		int nodeStart = lastLexElement().getStartPos();
		
		boolean isAssignSyntax = false;
		RefIdentifier refId = null;
		
		parsing:
		if(tryConsume(DeeTokens.KW_THIS)) {
			isAssignSyntax = true;
			
			if(consumeExpectedToken(DeeTokens.ASSIGN) == null) break parsing;
			
			refId = parseRefIdentifier();
		} else {
			refId = parseRefIdentifier();
			consumeExpectedToken(DeeTokens.KW_THIS);
		}
		
		boolean ruleBroken = consumeExpectedToken(DeeTokens.SEMICOLON) == null;
		return connectResult(ruleBroken, srToPosition(nodeStart, new DeclarationAliasThis(isAssignSyntax, refId)));
	}
	
	/* -------------------- Plain declarations -------------------- */
	
	public NodeResult<DeclarationImport> parseImportDeclaration() {
		int nodeStart = lookAheadElement().getStartPos();
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
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		
		return connectResult(false, srToPosition(nodeStart, new DeclarationImport(isStatic, arrayViewI(fragments))));
	}
	
	public IImportFragment parseImportFragment() {
		BaseLexElement aliasId = null;
		ArrayList<Token> packages = new ArrayList<Token>(0);
		int refModuleStartPos = -1;
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN
			|| lookAhead() == DeeTokens.ASSIGN) {
			aliasId = consumeExpectedIdentifier();
			consumeLookAhead(DeeTokens.ASSIGN);
		}
		
		while(true) {
			BaseLexElement id = consumeExpectedIdentifier();
			refModuleStartPos = refModuleStartPos == -1 ? id.getStartPos() : refModuleStartPos;
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.getToken());
			} else {
				RefModule refModule = 
					connect(srToPosition(refModuleStartPos, new RefModule(arrayViewG(packages), id.getSourceValue())));
				
				IImportFragment fragment = connect( (aliasId == null) ? 
					srBounds(refModule, new ImportContent(refModule)) : 
					srToPosition(aliasId, new ImportAlias(defUnitNoComments(aliasId), refModule))
				);
				
				if(tryConsume(DeeTokens.COLON)) {
					return parseSelectiveModuleImport(fragment);
				}
				
				return fragment;
			}
		}
	}
	
	public ImportSelective parseSelectiveModuleImport(IImportFragment fragment) {
		ArrayList<IImportSelectiveSelection> selFragments = new ArrayList<IImportSelectiveSelection>();
		
		do {
			IImportSelectiveSelection importSelSelection = parseImportSelectiveSelection();
			selFragments.add(importSelSelection);
			
		} while(tryConsume(DeeTokens.COMMA));
		
		return connect(srToPosition(fragment.getStartPos(), new ImportSelective(fragment, arrayViewI(selFragments))));
	}
	
	public IImportSelectiveSelection parseImportSelectiveSelection() {
		BaseLexElement aliasId = null;
		BaseLexElement id = consumeExpectedIdentifier();
		
		if(tryConsume(DeeTokens.ASSIGN)){
			aliasId = id;
			id = consumeExpectedIdentifier();
		}
		
		RefImportSelection refImportSelection = connect(srEffective(id, new RefImportSelection(idTokenToString(id))));
		
		if(aliasId == null) {
			return refImportSelection;
		} else {
			DefUnitTuple aliasIdDefUnit = defUnitNoComments(aliasId);
			return connect(srToPosition(aliasId.getStartPos(), 
				new ImportSelectiveAlias(aliasIdDefUnit, refImportSelection)));
		}
	}
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public ASTNeoNode declList;
		
		public AttribBodyParseRule parseAttribBody(boolean acceptEmptyDecl, boolean enablesAutoDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				consumeExpectedToken(DeeTokens.CLOSE_BRACE);
			} else {
				declList = getResult(parseDeclaration(acceptEmptyDecl, enablesAutoDecl));
				if(declList == null) {
					reportErrorExpectedRule(RULE_DECLARATION);
				}
			}
			return this;
		}
	}
	
	protected NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		int nodeListStart = getLexPosition();
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		consumeSubChannelTokens();
		return connect(srToPosition(nodeListStart, new NodeList2(declDefs)));
	}
	
	public DeclarationLinkage parseDeclarationExternLinkage() {
		if(!tryConsume(DeeTokens.KW_EXTERN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		
		String linkageStr = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				linkageStr = "";
				
				Token linkageToken = consumeIf(DeeTokens.IDENTIFIER);
				if(linkageToken != null ) {
					linkageStr = linkageToken.source;
					if(linkageStr.equals("C") && tryConsume(DeeTokens.INCREMENT)) {
						linkageStr = Linkage.CPP.name;
					}
				}
				
				if(Linkage.fromString(linkageStr) == null) {
					addErrorWithMissingtoken(ParserErrorTypes.INVALID_EXTERN_ID, null, true);
				}
				
				if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null)
					break parsing;
			}
			ab.parseAttribBody(false, false);
		}
		
		return connect(srToPosition(declStart, new DeclarationLinkage(linkageStr, ab.bodySyntax, ab.declList)));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		
		String alignNum = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				alignNum = consumeExpectedToken(DeeTokens.INTEGER_DECIMAL, true).getSourceValue();
				
				if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null) 
					break parsing;
			}
			ab.parseAttribBody(false, false);
		}
		
		return connect(srToPosition(declStart, new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList)));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		
		Symbol pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			pragmaId = parseSymbol();
			
			// TODO pragma argument list;
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(true, false);
			}
		}
		
		return connect(srToPosition(declStart, new DeclarationPragma(pragmaId, null, ab.bodySyntax, ab.declList)));
	}
	
	public Symbol parseSymbol() {
		BaseLexElement id = consumeExpectedIdentifier();
		return connect(sr(id, new Symbol(id.getSourceValue())));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		LexElement protElement = consumeInput();
		int declStart = lastLexElement().getStartPos();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(protElement.token.type);
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(false, true);
		return connect(srToPosition(declStart, new DeclarationProtection(protection, ab.bodySyntax, ab.declList)));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement().getStartPos();
		
		AttribBodyParseRule apr = new AttribBodyParseRule().parseAttribBody(false, true);
		return connect(srToPosition(declStart, new DeclarationBasicAttrib(attrib, apr.bodySyntax, apr.declList)));
	}
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseExpression_toMissing();
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(srToPosition(declStart, new DeclarationMixinString(exp)));
	}
	
	public ASTNeoNode parseDeclarationMixin() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		
		NodeResult<Reference> tplInstanceResult = parseTypeReference_ToMissing(true);
		Reference tplInstance = tplInstanceResult.node;
		LexElement id = null;
		if(!tplInstanceResult.ruleBroken) {
			id = consumeElementIf(DeeTokens.IDENTIFIER);	
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		if(id == null) {
			return connect(srToPosition(declStart, new DeclarationMixin(tplInstance)));
		}
		return connect(srToPosition(declStart, new NamedMixinDeclaration(tplInstance, defUnitNoComments(id))));
	}
	
}