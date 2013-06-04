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
import static dtool.util.NewUtils.lazyInitArrayList;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNode;
import dtool.ast.NodeListView;
import dtool.ast.SourceRange;
import dtool.ast.declarations.AbstractConditionalDeclaration.VersionSymbol;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.DeclList;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAlign;
import dtool.ast.declarations.DeclarationAllocatorFunction;
import dtool.ast.declarations.DeclarationAtAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationBasicAttrib;
import dtool.ast.declarations.DeclarationBasicAttrib.AttributeKinds;
import dtool.ast.declarations.DeclarationDebugVersion;
import dtool.ast.declarations.DeclarationDebugVersionSpec;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationImport.IImportFragment;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationLinkage;
import dtool.ast.declarations.DeclarationLinkage.Linkage;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.declarations.DeclarationPostBlit;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationProtection;
import dtool.ast.declarations.DeclarationProtection.Protection;
import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.declarations.DeclarationSpecialFunction.SpecialFunctionKind;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.declarations.ImportAlias;
import dtool.ast.declarations.ImportContent;
import dtool.ast.declarations.ImportSelective;
import dtool.ast.declarations.ImportSelective.IImportSelectiveSelection;
import dtool.ast.declarations.ImportSelectiveAlias;
import dtool.ast.declarations.IncompleteDeclaration;
import dtool.ast.declarations.InvalidSyntaxElement;
import dtool.ast.declarations.MissingDeclaration;
import dtool.ast.definitions.AbstractFunctionDefinition;
import dtool.ast.definitions.DeclarationEnum;
import dtool.ast.definitions.DeclarationMixin;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAggregate.IAggregateBody;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl.AliasVarDeclFragment;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionNamedMixin;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.DefinitionVariable.DefinitionAutoVariable;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.TemplateAliasParam;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.IInitializer;
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
import dtool.ast.statements.EmptyStatement;
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
	
	public AbstractParser.NodeResult<Module> parseModule(String defaultModuleName) {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNode> members = parseDeclDefs(null, true);
		assertTrue(lookAhead() == DeeTokens.EOF);
		consumeSubChannelTokens(); // Ensure pending whitespace is consumed as well
		assertTrue(getLexPosition() == lookAheadElement().getStartPos());
		//assertTrue(getLexPosition() == getSource().length()); // This is not true if explicit EOF token is present. 
		
		SourceRange modRange = new SourceRange(0, getLexPosition());
		
		if(md != null) {
			return result(false, conclude(modRange, new Module(md.getModuleSymbol(), null, md, members)));
		} else {
			return result(false, conclude(modRange, Module.createModuleNoModuleDecl(defaultModuleName, members)));
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
	
	public ArrayView<ASTNode> parseDeclDefs(DeeTokens nodeListTerminator, boolean consumeCloseBrackets) {
		ArrayList<ASTNode> declarations = new ArrayList<>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			ASTNode decl = parseDeclaration().node;
			if(decl == null) { 
				if(lookAhead() == DeeTokens.EOF || (!consumeCloseBrackets && isCloseBracketChar(lookAhead()))) {
					break;
				}
				decl = parseInvalidElement(RULE_DECLARATION, false);
			}
			declarations.add(decl);
		}
		
		return arrayView(declarations);
	}
	
	/* --------------------- DECLARATIONS --------------------- */
	
	public static final ParseRuleDescription RULE_DECLARATION = new ParseRuleDescription("Declaration");
	
	public InvalidSyntaxElement parseInvalidElement(ParseRuleDescription expectedRule, 
		boolean inStatementList) {
		Token badToken = consumeLookAhead().token;
		ParseHelper parse = new ParseHelper();
		parse.storeBreakError(createSyntaxError(expectedRule));
		return parse.conclude(new InvalidSyntaxElement(inStatementList, badToken));
	}
	
	public NodeResult<? extends IDeclaration> parseDeclaration() {
		return parseDeclaration(false, false);
	}
	public NodeResult<? extends IDeclaration> parseDeclaration(boolean precedingIsSTCAttrib) {
		return parseDeclaration(precedingIsSTCAttrib, false);
	}
	public NodeResult<? extends IDeclaration> parseDeclaration(boolean precedingIsSTCAttrib, boolean statementsOnly) {
		DeeTokens laGrouped = assertNotNull_(lookAheadGrouped());
		
		if(laGrouped == DeeTokens.EOF) {
			return declarationNullResult();
		}
		DeeTokens la = lookAhead();
		
		if(!statementsOnly && (la == DeeTokens.CONCAT || la == DeeTokens.KW_STATIC || la == DeeTokens.KW_SHARED)) {
			NodeResult<DeclarationSpecialFunction> declSpecialFunction = parseDeclarationSpecialFunction();
			if(declSpecialFunction != null)
				return declSpecialFunction;
		}
		
		switch (laGrouped) {
		case KW_IMPORT: return parseDeclarationImport();
		
		case KW_STRUCT:
			return parseDefinitionStruct();
		case KW_UNION:
			return parseDefinitionUnion();
		case KW_CLASS:
			return parseDefinitionClass();
		case KW_INTERFACE:
			return parseDefinitionInterface();
		case KW_TEMPLATE: 
			return parseTemplateDefinition();
			
		case KW_ENUM:
			if((lookAhead(1) == DeeTokens.COLON || lookAhead(1) == DeeTokens.OPEN_BRACE))
				return parseDeclarationEnum_start();
			if( (lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.COLON) ||
				(lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.OPEN_BRACE) ||
				(lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.SEMICOLON) || 
				(lookAhead(1) == DeeTokens.SEMICOLON))
				return parseDefinitionEnum_start();
			return parseDeclarationBasicAttrib();
			
		case KW_ALIAS:
			if(lookAhead(1) == DeeTokens.KW_THIS ||  
				(lookAhead(1) == DeeTokens.IDENTIFIER && lookAhead(2) == DeeTokens.KW_THIS)) {
				return parseDeclarationAliasThis();
			}
			return parseAliasDefinition();
		case KW_MIXIN: 
			if(lookAhead(1) == DeeTokens.KW_TEMPLATE) {
				return parseTemplateDefinition();
			}
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return parseDeclarationMixinString();
			}
			return parseDeclarationMixin();
		case KW_ALIGN: 
			if(statementsOnly) return declarationNullResult();
			return parseDeclarationAlign();
		case KW_PRAGMA: 
			return parseDeclarationPragma(statementsOnly);
		case PROTECTION_KW: 
			if(statementsOnly) return declarationNullResult();
			return parseDeclarationProtection();
		case KW_EXTERN: 
			if(statementsOnly) return declarationNullResult();
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return parseDeclarationExternLinkage();
			}
			return parseDeclarationBasicAttrib();
		case AT:
			if(lookAhead(1) == DeeTokens.IDENTIFIER) {
				return parseDeclarationAtAttrib();
			}
			break;
		case KW_AUTO:
			return parseDeclarationBasicAttrib();
		case ATTRIBUTE_KW:
			if(lookAhead() == DeeTokens.KW_STATIC) { 
				if(lookAhead(1) == DeeTokens.KW_IMPORT) { 
					return parseDeclarationImport();
				}
				if(lookAhead(1) == DeeTokens.KW_ASSERT) { 
					return parseDeclarationStaticAssert();
				}
				if(lookAhead(1) == DeeTokens.KW_IF) { 
					return parseDeclarationStaticIf(statementsOnly);
				}
			}
			
			if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
				break; // this will be parsed as a type modifier reference
			}
			
			return parseDeclarationBasicAttrib();
			
		case KW_DEBUG:
			if(!statementsOnly && lookAhead(1) == DeeTokens.ASSIGN) {
				return parseDeclarationDebugVersionSpec();
			}
			return parseDeclarationDebugVersion(statementsOnly);
		case KW_VERSION:
			if(!statementsOnly && lookAhead(1) == DeeTokens.ASSIGN) {
				return parseDeclarationDebugVersionSpec();
			}
			return parseDeclarationDebugVersion(statementsOnly);
		case KW_INVARIANT: 
			if(statementsOnly) return declarationNullResult();
			return parseDeclarationInvariant_start();
		case KW_UNITTEST: 
			if(statementsOnly) return declarationNullResult();
			return parseDeclarationUnitTest_start();
		case KW_NEW:
		case KW_DELETE: 
			if(statementsOnly) return declarationNullResult();
			return parseDeclarationAllocatorFunctions();
		case KW_THIS: 
			if(statementsOnly) return declarationNullResult();
			if(lookAhead(1) == DeeTokens.OPEN_PARENS && lookAhead(2) == DeeTokens.KW_THIS)
				return parseDeclarationPostBlit_start();
			return parseDefinitionConstructor();
		case SEMICOLON:
			return resultConclude(false, srOf(consumeLookAhead(), new DeclarationEmpty()));
		default:
			break;
		}
		
		return parseDeclaration_varOrFunction(precedingIsSTCAttrib);
	}
	
	public NodeResult<? extends IDeclaration> parseDeclaration_varOrFunction(boolean precedingIsSTCAttrib) {
		// This parses (BasicType + BasicType2) of spec:
		NodeResult<Reference> startRef = parseTypeReference_do(false);
		if(startRef.node == null) {
			return declarationNullResult();
		} 
		
		Reference ref = startRef.node;
		
		if(startRef.ruleBroken) {
			return resultConclude(true, srToPosition(ref, new IncompleteDeclaration(ref)));
		}
		
		ProtoDefSymbol defId = null;
		if(lookAhead() == DeeTokens.IDENTIFIER) {
			defId = defSymbol(consumeLookAhead());
		} else if(precedingIsSTCAttrib && couldHaveBeenParsedAsId(ref)) {
			// Parse as auto declaration instead
			defId = convertRefIdToDef(ref);
			ref = null;
		}
		
		if(defId != null) {
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionFunction_afterIdentifier(ref, defId);
			}
			return parseDefinitionVariable_afterIdentifier(ref, defId);
		} else {
			ParseHelper parse = new ParseHelper(ref);
			parse.consumeExpected(DeeTokens.IDENTIFIER);
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.resultConclude(new IncompleteDeclaration(ref));
		}
	}
	
	public static NodeResult<? extends IDeclaration> declarationNullResult() {
		return AbstractParser.<MissingDeclaration>result(false, null);
	}
	
	/* ----------------------------------------- */
	
	
	protected NodeResult<? extends DefinitionVariable> parseDefinitionVariable_afterIdentifier(
		Reference ref, ProtoDefSymbol defId) 
	{
		ArrayList<DefVarFragment> fragments = null;
		IInitializer init = null;
		Reference cstyleSuffix = null;
		
		final boolean isAutoRef = ref == null;
		ParseHelper parse = new ParseHelper(isAutoRef ? defId.getStartPos() : ref.getStartPos());
		
		parsing: {
			if(!isAutoRef) {
				cstyleSuffix = parseCStyleSuffix(parse);
				if(parse.checkRuleBroken()) break parsing;
			}
			
			if(parse.consumeOptional(DeeTokens.ASSIGN)){
				init = parseInitializer().node;
			} else if(isAutoRef) {
				parse.store(createExpectedTokenError(DeeTokens.ASSIGN));
			}
			
			while(parse.consumeOptional(DeeTokens.COMMA)) {
				DefVarFragment defVarFragment = parseVarFragment(isAutoRef);
				fragments = lazyInitArrayList(fragments);
				fragments.add(defVarFragment);
			}
		}
		parse.clearRuleBroken().consumeRequired(DeeTokens.SEMICOLON);
		
		if(isAutoRef) {
			return parse.resultConclude(new DefinitionAutoVariable(defId, init, arrayView(fragments)));
		}
		return parse.resultConclude(new DefinitionVariable(defId, ref, cstyleSuffix, init, arrayView(fragments)));
	}
	
	protected DefVarFragment parseVarFragment(boolean isAutoRef) {
		ProtoDefSymbol fragId = parseDefId();
		ParseHelper parse = new ParseHelper(fragId.getStartPos());
		IInitializer init = null;
		
		if(!fragId.isMissing()) {
			if(tryConsume(DeeTokens.ASSIGN)){ 
				init = parseInitializer().node;
			} else if(isAutoRef) {
				parse.store(createExpectedTokenError(DeeTokens.ASSIGN));
			}
		}
		return parse.conclude(new DefVarFragment(fragId, init));
	}
	
	public static final ParseRuleDescription RULE_INITIALIZER = new ParseRuleDescription("Initializer");
	
	public NodeResult<? extends IInitializer> parseInitializer() {
		if(tryConsume(DeeTokens.KW_VOID)) {
			return resultConclude(false, srOf(lastLexElement(), new InitializerVoid()));
		}
		
		return parseNonVoidInitializer(true);
	}
	
	public NodeResult<? extends IInitializer> parseNonVoidInitializer(boolean createMissing) {
		if(lookAhead() == DeeTokens.OPEN_BRACKET) {
			NodeResult<InitializerArray> arrayInitResult = parseArrayInitializer();
			if(arrayInitResult.ruleBroken) {
				return arrayInitResult;
			}
			InitializerArray arrayInitExp = arrayInitResult.node;
			Expression initExp = parseExpression_fromUnary(InfixOpType.ASSIGN, arrayInitExp);
			if(initExp == arrayInitExp) {
				return arrayInitResult;
			}
			ParserError error = null;
			if(!arrayInitializerCouldParseAsArrayLiteral(arrayInitExp)) {
				error = createError(ParserErrorTypes.INIT_USED_IN_EXP, arrayInitExp.getSourceRange(), null); 
			} else {
				// Even if initializer can be parsed as array literal, we place it in exp without any node conversion
				// (this might change in future)
			}
			InitializerExp initializerExp = conclude(error, srOf(initExp, new InitializerExp(initExp)));
			return result(false, initializerExp);
		}
		if(lookAhead() == DeeTokens.OPEN_BRACE) {
			DeeParserState savedParserState = thisParser().enterBacktrackableMode();
			NodeResult<InitializerStruct> structInitResult = parseStructInitializer();
			
			if(structInitResult.ruleBroken) {
				thisParser().restoreOriginalState(savedParserState);
				return parseExpInitializer(createMissing);
			}
			return structInitResult;
			
		}
		return parseExpInitializer(createMissing);
	}
	
	public boolean arrayInitializerCouldParseAsArrayLiteral(InitializerArray arrayInit) {
		if(arrayInit.entries.size() < 1) {
			return true;
		}
		boolean mustBeMapEntries = arrayInit.entries.get(0).index != null;
		for (ArrayInitEntry entry : arrayInit.entries) {
			if(entry.value instanceof InitializerArray) {
				InitializerArray initArraySubEntry = (InitializerArray) entry.value;
				if(!arrayInitializerCouldParseAsArrayLiteral(initArraySubEntry)) {
					return false;
				}
				return true;
			} else if(!(entry.value instanceof InitializerExp)) {
				return false;
			}
			boolean isMapEntry = entry.index != null;
			if(isMapEntry != mustBeMapEntries)
				return false;
		}
		return true;
	}

	public NodeResult<InitializerExp> parseExpInitializer(boolean createMissing) {
		NodeResult<Expression> expResult = createMissing ? 
			parseAssignExpression_toMissing(true, RULE_INITIALIZER) : parseAssignExpression();
		Expression exp = expResult.node;
		if(exp == null) {
			return AbstractParser.<InitializerExp>nullResult();
		}
		return resultConclude(expResult.ruleBroken, srOf(exp, new InitializerExp(exp)));
	}
	
	public NodeResult<InitializerArray> parseArrayInitializer() {
		ParseArrayInitEntry listParse = new ParseArrayInitEntry();
		listParse.parseList(DeeTokens.OPEN_BRACKET, DeeTokens.COMMA, DeeTokens.CLOSE_BRACKET);
		if(listParse.members == null)
			return nullResult();
		
		return listParse.resultConclude(new InitializerArray(listParse.members));
	}
	
	public class ParseArrayInitEntry extends ElementListParseHelper<ArrayInitEntry> {
		@Override
		protected ArrayInitEntry parseElement(boolean createMissing) {
			Expression index = null;
			IInitializer initializer = null;
			
			if(lookAhead() == DeeTokens.COLON) {
				index = parseAssignExpression_toMissing();
				consumeLookAhead(DeeTokens.COLON);
				initializer = parseNonVoidInitializer(true).node;
			} else {
				initializer = parseNonVoidInitializer(createMissing).node;
				
				if(initializer == null)
					return null;
				
				if(initializer instanceof InitializerExp && lookAhead() == DeeTokens.COLON) {
					index = ((InitializerExp) initializer).exp;
					index.detachFromParent();
					consumeLookAhead(DeeTokens.COLON);
					initializer = parseNonVoidInitializer(true).node;
				}
			}
			
			ASTNode startNode = index != null ? index : initializer.asNode();
			return concludeNode(srToPosition(startNode, new ArrayInitEntry(index, initializer)));
		}
	}
	
	public NodeResult<InitializerStruct> parseStructInitializer() {
		ParseStructInitEntry listParse = new ParseStructInitEntry();
		listParse.parseList(DeeTokens.OPEN_BRACE, DeeTokens.COMMA, DeeTokens.CLOSE_BRACE);
		if(listParse.members == null)
			return nullResult();
		
		return listParse.resultConclude(new InitializerStruct(listParse.members));
	}
	
	public class ParseStructInitEntry extends ElementListParseHelper<StructInitEntry> {
		@Override
		protected StructInitEntry parseElement(boolean createMissing) {
			RefIdentifier member = null;
			if(lookAhead() == DeeTokens.COLON || 
				(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.COLON)) {
				member = parseRefIdentifier();
				consumeLookAhead(DeeTokens.COLON);
			}
			IInitializer init = parseNonVoidInitializer(createMissing || member != null).node;
			if(init == null)
				return null;
			
			ASTNode startNode = member != null ? member : init.asNode();
			return concludeNode(srToPosition(startNode, new StructInitEntry(member, init)));
		}
	}
	
	protected NodeResult<DefinitionConstructor> parseDefinitionConstructor() {
		if(tryConsume(DeeTokens.KW_THIS) == false) {
			return null;
		}
		ParseHelper parse = new ParseHelper();
		ProtoDefSymbol defId = defSymbol(lastLexElement()); // TODO: mark this as special DefSymbol
		return parse_FunctionLike(true, null, defId, parse).upcastTypeParam();
	}
	
	/**
	 * Parse a function from this point:
	 * http://dlang.org/declaration.html#DeclaratorSuffix
	 */
	protected NodeResult<DefinitionFunction> parseDefinitionFunction_afterIdentifier(
		Reference retType, ProtoDefSymbol defId) {
		assertTrue(defId.isMissing() == false);
		
		ParseHelper parse = new ParseHelper(retType != null ? retType.getStartPos() : defId.getStartPos());
		
		return parse_FunctionLike(false, retType, defId, parse).upcastTypeParam();
	}
	
	protected NodeResult<? extends AbstractFunctionDefinition> parse_FunctionLike(boolean isConstrutor, 
		Reference retType, ProtoDefSymbol defId, ParseHelper parse) {
		
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
				tplParams = firstParams.toTemplateParameters();
			}
			
			if(tplParams != null) {
				fnParams = parseFunctionParameters(parse);
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
			
			fnBody = parse.requiredResult(parseFunctionBody(), RULE_FN_BODY);
		}
		
		if(isConstrutor) {
			return parse.resultConclude(new DefinitionConstructor(
				defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody));
		}
		
		return parse.resultConclude(new DefinitionFunction(
			retType, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody));
	}
	
	protected ASTNode parseTemplateAliasParameter_start() {
		consumeLookAhead(DeeTokens.KW_ALIAS);
		ParseHelper parse = new ParseHelper();
		
		ProtoDefSymbol defId;
		Resolvable init = null;
		Resolvable specialization = null;
		
		parsing: {
			defId = parse.checkResult(parseDefId());
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
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.AMBIG).parse(parse, false);
	}
	
	protected final DeeParser_RuleParameters isFunctionParameters(ParseHelper parse) {
		// TODO: optimize unnecessary processing and object creation when in this decider mode
		return new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN).parseDeciderMode(parse);
	}
	protected final ArrayView<IFunctionParameter> parseFunctionParameters(ParseHelper parse) {
		return parseFunctionParameters(parse, false);
	}
	protected ArrayView<IFunctionParameter> parseFunctionParameters(ParseHelper parse, boolean isOptional) {
		DeeParser_RuleParameters fnParametersParse = new DeeParser_RuleParameters(thisParser(), TplOrFnMode.FN);
		return fnParametersParse.parse(parse, isOptional).getAsFunctionParameters();
	}
	
	protected final ArrayView<TemplateParameter> parseTemplateParameters(ParseHelper parse, boolean isOptional) {
		DeeParser_RuleParameters tplParametersParse = new DeeParser_RuleParameters(thisParser(), TplOrFnMode.TPL);
		return tplParametersParse.parse(parse, isOptional).getAsTemplateParameters();
	}
	
	protected final ArrayView<TemplateParameter> parseTemplateParametersList() {
		DeeParser_RuleParameters tplParametersParse = new DeeParser_RuleParameters(thisParser(), TplOrFnMode.TPL);
		tplParametersParse.parseParameterList(false);
		return tplParametersParse.getAsTemplateParameters();
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
		return parseExpressionAroundParentheses(parse, true, false);
	}
	
	public static final ParseRuleDescription RULE_FN_BODY = new ParseRuleDescription("FnBody");
	
	protected NodeResult<? extends IFunctionBody> parseFunctionBody() {
		if(tryConsume(DeeTokens.SEMICOLON)) { 
			return resultConclude(false, srOf(lastLexElement(), new EmptyStatement()));
		}
		NodeResult<BlockStatement> blockResult = thisParser().parseBlockStatement(false, false);
		if(blockResult.node != null)
			return blockResult;
		
		ParseHelper parse = new ParseHelper(-1);
		if(lookAhead() == DeeTokens.KW_IN || lookAhead() == DeeTokens.KW_OUT || lookAhead() == DeeTokens.KW_BODY) {
			parse.setStartPosition(lookAheadElement().getStartPos());
		} else {
			parse.setStartPosition(getLexPosition()); // It will be missing element
		}
		
		boolean isOutIn = false;
		BlockStatement inBlock = null;
		FunctionBodyOutBlock outBlock = null;
		BlockStatement bodyBlock = null;
		
		parsing: {
			if(tryConsume(DeeTokens.KW_IN)) {
				inBlock = parse.checkResult(parseBlockStatement_toMissing(false));
				if(parse.ruleBroken) break parsing;
				
				if(lookAhead() == DeeTokens.KW_OUT) {
					outBlock = parse.checkResult(parseOutBlock());
					if(parse.ruleBroken) break parsing;
				}
			} else if(lookAhead() == DeeTokens.KW_OUT) {
				isOutIn = true;
				
				outBlock = parse.checkResult(parseOutBlock());
				if(parse.ruleBroken) break parsing;
				
				if(tryConsume(DeeTokens.KW_IN)) {
					inBlock = parse.checkResult(parseBlockStatement_toMissing(false));
					if(parse.ruleBroken) break parsing;
				}
			}
			
			if(tryConsume(DeeTokens.KW_BODY)) {
				bodyBlock = parse.checkResult(parseBlockStatement_toMissing(false));
			} else {
				if(inBlock == null && outBlock == null) {
					return nullResult().<FunctionBody>upcastTypeParam();
				}
				parse.storeBreakError(createErrorExpectedRule(RULE_FN_BODY));
			}
		}
		
		if(inBlock == null && outBlock == null) {
			return parse.resultConclude(new FunctionBody(bodyBlock));
		}
		return parse.resultConclude(new InOutFunctionBody(isOutIn, inBlock, outBlock, bodyBlock));
	}
	
	public NodeResult<BlockStatement> parseBlockStatement_toMissing() {
		return thisParser().parseBlockStatement(true, true);
	}
	public NodeResult<BlockStatement> parseBlockStatement_toMissing(boolean brokenIfMissing) {
		return thisParser().parseBlockStatement(true, brokenIfMissing);
	}
	
	protected NodeResult<FunctionBodyOutBlock> parseOutBlock() {
		if(!tryConsume(DeeTokens.KW_OUT))
			return nullResult();
		ParseHelper parse = new ParseHelper();
		
		Symbol id = null;
		BlockStatement block = null;
		
		parsing: {
			if(parse.consumeOptional(DeeTokens.OPEN_PARENS)) {
				id = parseIdSymbol();
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
			
			block = parse.checkResult(parseBlockStatement_toMissing(false));
		}
		
		return parse.resultConclude(new FunctionBodyOutBlock(id, block));
	}
	
	public NodeResult<DefinitionTemplate> parseTemplateDefinition() {
		AggregateDefinitionParse adp = new AggregateDefinitionParse();
		adp.nodeStart = lookAheadElement().getStartPos();
		
		boolean isMixin = false;
		if(tryConsume(DeeTokens.KW_MIXIN, DeeTokens.KW_TEMPLATE)) {
			isMixin = true;
		} else if(!tryConsume(DeeTokens.KW_TEMPLATE)) {
			return null;
		}
		adp.parseAggregate(false);
		adp.parseDeclarationBlockBody();
		
		return adp.resultConclude(
			new DefinitionTemplate(isMixin, adp.defId, adp.tplParams, adp.tplConstraint, adp.getDeclBlock()));
	}
	
	public static final ParseRuleDescription RULE_AGGR_BODY = new ParseRuleDescription("AggregateBody");
	
	public class AggregateDefinitionParse extends ParseHelper {

		protected ProtoDefSymbol defId = null;
		protected ArrayView<TemplateParameter> tplParams = null;
		protected Expression tplConstraint = null;
		protected IAggregateBody declBody = null;
		
		public void parseAggregate(boolean tplParamsIsOptional) {
			ParseHelper parse = this;
			
			parsing: {
				defId = parse.checkResult(parseDefId());
				if(parse.ruleBroken) break parsing;
				
				tplParams = parseTemplateParameters(parse, tplParamsIsOptional);
				if(parse.ruleBroken) break parsing;
				
				if(tplParams != null) {
					tplConstraint = parseTemplateConstraint(parse);
				}
			}
		}
		
		public final DeclBlock getDeclBlock() {
			return (DeclBlock) declBody;
		}
		
		public void parseDeclarationBlockBody() {
			ParseHelper parse = this;
			if(parse.ruleBroken == false) {
				declBody = parse.requiredResult(parseDeclarationBlock(), RULE_DECLARATION_BLOCK);
			}
		}
		
		public void parseAggregateBody() {
			ParseHelper parse = this;
			if(!parse.ruleBroken) {
				if(tryConsume(DeeTokens.SEMICOLON)) {
					declBody = concludeNode(srOf(lastLexElement(), new DeclarationEmpty()));
				} else {
					declBody = parse.requiredResult(parseDeclarationBlock(), RULE_AGGR_BODY);
				}
			}
		}
		
	}
	
	public static final ParseRuleDescription RULE_DECLARATION_BLOCK = new ParseRuleDescription("DeclarationBlock");
	
	public NodeResult<DeclBlock> parseDeclarationBlock() {
		if(tryConsume(DeeTokens.OPEN_BRACE) == false) {
			return nullResult();
		}
		ParseHelper parse = new ParseHelper();
		
		ArrayView<ASTNode> declDefs = parseDeclDefs(DeeTokens.CLOSE_BRACE, false);
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		return parse.resultConclude(new DeclBlock(declDefs));
	}
	
	public final NodeResult<RefTypeFunction> parseRefTypeFunction_afterReturnType(Reference retType) {
		boolean isDelegate = lastLexElement().token.type == DeeTokens.KW_DELEGATE;
		
		ParseHelper parse = new ParseHelper(retType);
		ArrayView<IFunctionParameter> fnParams = null;
		ArrayView<FunctionAttributes> fnAttributes = null;
		
		parsing: {
			fnParams = parseFunctionParameters(parse);
			if(parse.ruleBroken) break parsing;
			
			fnAttributes = parseFunctionAttributes();
		}
		
		return parse.resultConclude(new RefTypeFunction(retType, isDelegate, fnParams, fnAttributes));
	}
	
	public NodeResult<? extends IDeclaration> parseAliasDefinition() {
		if(!tryConsume(DeeTokens.KW_ALIAS))
			return null;
		ParseHelper parse = new ParseHelper();
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN) {
			return parseDefinitionAlias_atFragmentStart();
		}
		
		// Note that there are heavy similarites between this code and var/function declaration parsing
		Reference ref = null;
		ProtoDefSymbol defId = null;
		Reference cstyleSuffix = null;
		ArrayList<AliasVarDeclFragment> fragments = null;
		
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
			
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionAliasFunctionDecl(parse, ref, defId);
			}
			
			cstyleSuffix = parseCStyleSuffix(parse);
			if(parse.checkRuleBroken()) break parsing;
			
			while(parse.consumeOptional(DeeTokens.COMMA)) {
				fragments = lazyInitArrayList(fragments);
				fragments.add(parseAliasVarDeclFragment());
			}
		}
		defId = nullIdToMissingDefId(defId);
		
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DefinitionAliasVarDecl(ref, defId, cstyleSuffix, arrayView(fragments)));
	}
	
	public NodeResult<? extends IDeclaration> parseDefinitionAliasFunctionDecl(ParseHelper parse, Reference ref,
		ProtoDefSymbol defId) {
		
		ArrayView<IFunctionParameter> fnParams = parseFunctionParameters(parse, true);
		ArrayView<FunctionAttributes> fnAttributes = null;
		if(!parse.ruleBroken) {
			fnAttributes = parseFunctionAttributes();
		} else {
			parse.clearRuleBroken();
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		return parse.resultConclude(new DefinitionAliasFunctionDecl(ref, defId, fnParams, fnAttributes));
	}
	
	protected AliasVarDeclFragment parseAliasVarDeclFragment() {
		ProtoDefSymbol fragId = parseDefId();
		return conclude(fragId.nameSourceRange, new AliasVarDeclFragment(fragId));
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
			parse.checkResult(defId);
			if(parse.ruleBroken) break parsing;
			
			if(parse.consumeRequired(DeeTokens.ASSIGN).ruleBroken) break parsing;
			
			NodeResult<Reference> refResult = parseTypeReference_ToMissing();
			ref = refResult.node;
		}
		return parse.conclude(new DefinitionAliasFragment(defId, ref));
	}
	
	protected NodeResult<DeclarationAliasThis> parseDeclarationAliasThis() {
		if(!tryConsume(DeeTokens.KW_ALIAS))
			return null;
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
	
	protected NodeResult<DefinitionEnum> parseDefinitionEnum_start() {
		consumeLookAhead(DeeTokens.KW_ENUM);
		ParseHelper parse = new ParseHelper();
		
		ProtoDefSymbol defId = parseDefId();
		Reference type = null;
		EnumBody body = null;
		
		parsing : {
			if(tryConsume(DeeTokens.COLON)) {
				type = parse.checkResult(parseTypeReference_ToMissing());
				if(parse.ruleBroken) break parsing;
			}
			if(tryConsume(DeeTokens.SEMICOLON)) {
				body = concludeNode(srOf(lastLexElement(), new DefinitionEnum.NoEnumBody()));
			} else {
				body = parse.requiredResult(parseEnumBody(), RULE_ENUM_BODY);
			}
		}
		return parse.resultConclude(new DefinitionEnum(defId, type, body));
	}
	
	protected NodeResult<DeclarationEnum> parseDeclarationEnum_start() {
		consumeLookAhead(DeeTokens.KW_ENUM);
		ParseHelper parse = new ParseHelper();
		
		Reference type = null;
		EnumBody body = null;
		
		parsing : {
			if(tryConsume(DeeTokens.COLON)) {
				type = parse.checkResult(parseTypeReference_ToMissing());
				if(parse.ruleBroken) break parsing;
			}
			body = parse.requiredResult(parseEnumBody(), RULE_ENUM_BODY);
		}
		return parse.resultConclude(new DeclarationEnum(type, body));
	}
	
	public static final ParseRuleDescription RULE_ENUM_BODY = new ParseRuleDescription("EnumBody");
	
	public NodeResult<EnumBody> parseEnumBody() {
		ParseEnumMember parse = new ParseEnumMember();
		parse.parseList(DeeTokens.OPEN_BRACE, DeeTokens.COMMA, DeeTokens.CLOSE_BRACE);
		if(parse.members == null) {
			return nullResult();
		}
		
		return parse.resultConclude(new EnumBody(parse.members));
	}
	
	public class ParseEnumMember extends ElementListParseHelper<EnumMember> {

		@Override
		protected EnumMember parseElement(boolean createMissing) {
			ParseHelper parse = new ParseHelper(-1);
			
			TypeId_or_Id_RuleFragment typeRef_defId = new TypeId_or_Id_RuleFragment();
			Expression value = null;
			
			typeRef_defId.parseRuleFragment(parse, createMissing);
			if(typeRef_defId.defId == null)
				return null;
			
			if(tryConsume(DeeTokens.ASSIGN)) {
				value = parseAssignExpression_toMissing();
			}
			
			return parse.conclude(new EnumMember(typeRef_defId.type, typeRef_defId.defId, value));
		}
		
	}
	
	public NodeResult<DefinitionStruct> parseDefinitionStruct() {
		return parseDefinition_StructOrUnion().upcastTypeParam();
	}
	public NodeResult<DefinitionUnion> parseDefinitionUnion() {
		return parseDefinition_StructOrUnion().upcastTypeParam();
	}
	
	protected NodeResult<? extends DefinitionAggregate> parseDefinition_StructOrUnion() {
		if(!(tryConsume(DeeTokens.KW_STRUCT) || tryConsume(DeeTokens.KW_UNION)))
			return null;
		
		boolean isStruct = lastLexElement().token.type == DeeTokens.KW_STRUCT;
		AggregateDefinitionParse adp = new AggregateDefinitionParse();
		if(lookAhead() != DeeTokens.IDENTIFIER) {
			adp.defId = nullIdToMissingDefId(null);
			adp.parseDeclarationBlockBody();
		} else {
			adp.parseAggregate(true);
			adp.parseAggregateBody();
		}
		
		return adp.resultConclude(isStruct ?
			new DefinitionStruct(adp.defId, adp.tplParams, adp.tplConstraint, adp.declBody) :
			new DefinitionUnion(adp.defId, adp.tplParams, adp.tplConstraint, adp.declBody));
	}
	
	public NodeResult<DefinitionClass> parseDefinitionClass() {
		return parseDefinition_ClassOrInterface().upcastTypeParam();
	}
	public NodeResult<DefinitionInterface> parseDefinitionInterface() {
		return parseDefinition_ClassOrInterface().upcastTypeParam();
	}
	
	protected NodeResult<? extends DefinitionClass> parseDefinition_ClassOrInterface() {
		if(!(tryConsume(DeeTokens.KW_CLASS) || tryConsume(DeeTokens.KW_INTERFACE)))
			return null;
		
		boolean isClass = lastLexElement().token.type == DeeTokens.KW_CLASS;
		AggregateDefinitionParse adp = new AggregateDefinitionParse();
		
		SimpleListParseHelper<Reference> baseClasses = new TypeReferenceSimpleListParse();
		parsing: {
			adp.parseAggregate(true);
			if(adp.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.COLON)) {
				baseClasses.parseSimpleList(DeeTokens.COMMA, false, false);
			}
			
			adp.parseAggregateBody();
		}
		
		return adp.resultConclude(isClass ?
			new DefinitionClass(adp.defId, adp.tplParams, adp.tplConstraint, baseClasses.members, adp.declBody) :
			new DefinitionInterface(adp.defId, adp.tplParams, adp.tplConstraint, baseClasses.members, adp.declBody)
		);
	}
	
	public class TypeReferenceListParse extends ElementListParseHelper<Reference> {
		@Override
		protected Reference parseElement(boolean createMissing) {
			return parseTypeReference(createMissing, true).node;
		}
	}
	
	public class TypeReferenceSimpleListParse extends SimpleListParseHelper<Reference> {
		@Override
		protected Reference parseElement(boolean createMissing) {
			return parseTypeReference(createMissing, true).node;
		}
	}
	
	/* -------------------- Plain declarations -------------------- */
	
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
		ArrayList<Token> packages = new ArrayList<Token>(0);
		
		ParseHelper parse = new ParseHelper(-1);
		while(true) {
			BaseLexElement id = parse.consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.getToken());
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
		SingleTokenParse parse = new SingleTokenParse(DeeTokens.IDENTIFIER);
		return parse.conclude(new RefImportSelection(idTokenToString(parse.lexToken)));
	}
	
	public static final ParseRuleDescription RULE_DECLBODY = new ParseRuleDescription("DeclOrBlock");
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public ASTNode declList;
		
		public AttribBodyParseRule parseAttribBody(ParseHelper parse, boolean acceptEmptyDecl, 
			boolean enablesAutoDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else {
				parseDeclBlockOrSingle(parse, acceptEmptyDecl, enablesAutoDecl);
			}
			return this;
		}
		
		public AttribBodyParseRule parseDeclBlockOrSingle(ParseHelper parse, boolean acceptEmptyDecl, 
			boolean enablesAutoDecl) {
			if(lookAhead() == DeeTokens.OPEN_BRACE) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parse.checkResult(parseDeclarationBlock());
			} else {
				declList = parse.checkResult(parseDeclaration(enablesAutoDecl));
				if(declList == null) {
					declList = concludeNode(srOf(consumeSubChannelTokens(), new MissingDeclaration()));
					parse.storeBreakError(createErrorExpectedRule(RULE_DECLBODY));
				} else if(declList instanceof DeclarationEmpty && !acceptEmptyDecl) {
					parse.store(createSyntaxError(RULE_DECLARATION));
				}
			}
			return this;
		}
	}
	
	protected DeclList parseDeclList(DeeTokens bodyListTerminator) {
		ParseHelper parse = new ParseHelper(getLexPosition());
		
		ArrayView<ASTNode> declDefs = parseDeclDefs(bodyListTerminator, false);
		consumeSubChannelTokens();
		return parse.conclude(new DeclList(declDefs));
	}
	
	public NodeResult<DeclarationLinkage> parseDeclarationExternLinkage() {
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
				
				if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			}
			ab.parseAttribBody(parse, false, false);
		}
		
		return parse.resultConclude(new DeclarationLinkage(linkageStr, ab.bodySyntax, ab.declList));
	}
	
	public NodeResult<DeclarationAlign> parseDeclarationAlign() {
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
		
		return parse.resultConclude(new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList));
	}
	
	public NodeResult<DeclarationPragma> parseDeclarationPragma(boolean isStatement) {
		if(!tryConsume(DeeTokens.KW_PRAGMA))
			return null;
		ParseHelper parse = new ParseHelper();
		
		Symbol pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		IStatement bodySt = null;
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
			
			if(isStatement) {
				bodySt = parse.checkResult(thisParser().parseUnscopedStatement_toMissing()); 
			} else {
				ab.parseAttribBody(parse, true, false);
			}
		}
		
		if(isStatement) {
			return parse.resultConclude(new DeclarationPragma(pragmaId, expList, bodySt));
		}
		return parse.resultConclude(new DeclarationPragma(pragmaId, expList, ab.bodySyntax, ab.declList));
	}
	
	public NodeResult<DeclarationProtection> parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		LexElement protElement = consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(protElement.token.type);
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(parse, false, true);
		return parse.resultConclude(new DeclarationProtection(protection, ab.bodySyntax, ab.declList));
	}
	
	public NodeResult<DeclarationBasicAttrib> parseDeclarationBasicAttrib() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null)
			return null;
		
		consumeLookAhead();
		ParseHelper parse = new ParseHelper();
		if(attrib == AttributeKinds.DEPRECATED) {
			parseExpressionAroundParentheses(parse, false, false);
			// TODO: tests for this, confirm spec
		}
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(parse, false, true);
		return parse.resultConclude(new DeclarationBasicAttrib(attrib, ab.bodySyntax, ab.declList));
	}
	
	public NodeResult<DeclarationAtAttrib> parseDeclarationAtAttrib() {
		if(!tryConsume(DeeTokens.AT)) 
			return null;
		
		ParseHelper parse = new ParseHelper();
		Symbol attribIdentifier = parseAttribId();
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(parse, false, true);
		return parse.resultConclude(new DeclarationAtAttrib(attribIdentifier, ab.bodySyntax, ab.declList));
	}
	
	public Symbol parseAttribId() {
		BaseLexElement traitsId = consumeExpectedContentToken(DeeTokens.IDENTIFIER);
		ParserError error = DeeTokenSemantics.checkAttribId(traitsId);
		return conclude(error, srOf(traitsId, new Symbol(traitsId.getSourceValue())));
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
		boolean isDebug = lastLexElement().token.type == DeeTokens.KW_DEBUG;
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
				parseAttribBody(parse, false, false);
				if(parse.ruleBroken) return;
				
				if(bodySyntax != AttribBodySyntax.COLON) {
					if(tryConsume(DeeTokens.KW_ELSE)) {
						elseBody = new AttribBodyParseRule().parseDeclBlockOrSingle(parse, false, false).declList ;
					}
				}
			}
		}
		
	}
	
	/* ----------------------------------------- */
	
	public static final ParseRuleDescription RULE_DEBUG_ARG = new ParseRuleDescription("DebugArg");
	public static final ParseRuleDescription RULE_VERSION_ARG = new ParseRuleDescription("VersionArg");
	
	public NodeResult<DeclarationDebugVersionSpec> parseDeclarationDebugVersionSpec() {
		if(!(tryConsume(DeeTokens.KW_DEBUG) || tryConsume(DeeTokens.KW_VERSION)))
			return null;
		boolean isDebug = lastLexElement().token.type == DeeTokens.KW_DEBUG;
		ParseHelper parse = new ParseHelper();
		
		VersionSymbol value = null;
		if(parse.consumeExpected(DeeTokens.ASSIGN)) {
			value = parseConditionalValue(isDebug, parse);
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		
		return parse.resultConclude(new DeclarationDebugVersionSpec(isDebug, value));
	}
	
	protected VersionSymbol parseConditionalValue(boolean isDebug, ParseHelper parse) {
		if(lookAhead() == DeeTokens.IDENTIFIER || lookAheadGrouped() == DeeTokens.INTEGER) {
			return createVersionSymbol(consumeLookAhead());
		} else { 
			parse.store(createErrorExpectedRule(isDebug ? RULE_DEBUG_ARG : RULE_VERSION_ARG));
			return createVersionSymbol(consumeSubChannelTokens());
		}
	}
	
	public VersionSymbol createVersionSymbol(BaseLexElement token) {
		return conclude(token.getError(), srOf(token, new VersionSymbol(token.getSourceValue())));
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
	
	public NodeResult<? extends IDeclaration> parseDeclarationMixin() {
		if(!tryConsume(DeeTokens.KW_MIXIN))
			return null;
		ParseHelper parse = new ParseHelper();
		
		NodeResult<Reference> tplInstanceResult = parseTypeReference_ToMissing(true);
		Reference tplInstance = tplInstanceResult.node;
		
		if(!tplInstanceResult.ruleBroken && lookAhead() == DeeTokens.IDENTIFIER) {
			ProtoDefSymbol defId = parseDefId();
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.resultConclude(new DefinitionNamedMixin(tplInstance, defId));
		} else {
			parse.consumeRequired(DeeTokens.SEMICOLON);
			return parse.resultConclude(new DeclarationMixin(tplInstance));
		}
	}
	
	public NodeResult<DeclarationInvariant> parseDeclarationInvariant_start() {
		consumeLookAhead(DeeTokens.KW_INVARIANT);
		ParseHelper parse = new ParseHelper();
		
		BlockStatement body = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			body = parse.checkResult(parseBlockStatement_toMissing(false));
		}
		
		return parse.resultConclude(new DeclarationInvariant(body));
	}
	
	public NodeResult<DeclarationUnitTest> parseDeclarationUnitTest_start() {
		consumeLookAhead(DeeTokens.KW_UNITTEST);
		ParseHelper parse = new ParseHelper();
		
		BlockStatement body = parse.checkResult(parseBlockStatement_toMissing(false));
		
		return parse.resultConclude(new DeclarationUnitTest(body));
	}
	
	public NodeResult<DeclarationPostBlit> parseDeclarationPostBlit_start() {
		consumeLookAhead(DeeTokens.KW_THIS);
		ParseHelper parse = new ParseHelper();
		
		IFunctionBody fnBody = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			parse.consumeExpected(DeeTokens.KW_THIS);
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			fnBody = parse.requiredResult(parseFunctionBody(), RULE_FN_BODY);
		}
		
		return parse.resultConclude(new DeclarationPostBlit(fnBody));
	}
	
	public NodeResult<DeclarationSpecialFunction> parseDeclarationSpecialFunction() {
		ParseHelper parse = new ParseHelper(lookAheadElement().getStartPos());
		
		SpecialFunctionKind kind = null;
		if(tryConsume(DeeTokens.CONCAT, DeeTokens.KW_THIS)) {
			kind = SpecialFunctionKind.DESTRUCTOR;
		} else if(tryConsume(DeeTokens.KW_STATIC, DeeTokens.KW_THIS)) {
			kind = SpecialFunctionKind.STATIC_CONSTRUCTOR;
		} else if(tryConsume(DeeTokens.KW_STATIC, DeeTokens.CONCAT, DeeTokens.KW_THIS)) {
			kind = SpecialFunctionKind.STATIC_DESTRUCTOR;
		} else if(tryConsume(DeeTokens.KW_SHARED, DeeTokens.KW_STATIC, DeeTokens.KW_THIS)) {
			kind = SpecialFunctionKind.SHARED_STATIC_CONSTRUCTOR;
		} else if(tryConsume(DeeTokens.KW_SHARED, DeeTokens.KW_STATIC, DeeTokens.CONCAT)) {
			parse.consumeExpected(DeeTokens.KW_THIS);
			kind = SpecialFunctionKind.SHARED_STATIC_DESTRUCTOR;
		}
		if(kind == null)
			return null;
		
		IFunctionBody fnBody = null;
		parsing: {
			if(parse.consumeRequired(DeeTokens.OPEN_PARENS).ruleBroken) break parsing;
			if(parse.consumeRequired(DeeTokens.CLOSE_PARENS).ruleBroken) break parsing;
			
			fnBody = parse.requiredResult(parseFunctionBody(), RULE_FN_BODY);
		}
		
		return parse.resultConclude(new DeclarationSpecialFunction(kind, fnBody));
	}
	
	public NodeResult<DeclarationAllocatorFunction> parseDeclarationAllocatorFunctions() {
		if((tryConsume(DeeTokens.KW_NEW) || tryConsume(DeeTokens.KW_DELETE)) == false)
			return null;
		ParseHelper parse = new ParseHelper();
		
		boolean isNew = lastLexElement().token.type == DeeTokens.KW_NEW;
		ArrayView<IFunctionParameter> params = null;
		IFunctionBody fnBody = null;
		
		parsing: {
			params = parseFunctionParameters(parse);
			if(parse.ruleBroken) break parsing;
			
			fnBody = parse.requiredResult(parseFunctionBody(), RULE_FN_BODY);
		}
		
		return parse.resultConclude(new DeclarationAllocatorFunction(isNew, params, fnBody));
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
	
}