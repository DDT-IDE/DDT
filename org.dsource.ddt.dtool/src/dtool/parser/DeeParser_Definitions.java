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

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTNode;
import dtool.ast.NodeData;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.declarations.DeclarationAliasThis;
import dtool.ast.declarations.DeclarationAllocatorFunction;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationEmpty;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationPostBlit;
import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.declarations.DeclarationSpecialFunction.SpecialFunctionKind;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.declarations.IDeclaration;
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
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerStruct.StructInitEntry;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTypeFunction;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.EmptyStatement;
import dtool.ast.statements.FunctionBody;
import dtool.ast.statements.FunctionBodyOutBlock;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.InOutFunctionBody;
import dtool.parser.DeeParser.DeeParserState;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public abstract class DeeParser_Definitions extends DeeParser_Declarations {
	
	public static ArrayList<Token> readStartDDocComments(LexElement declStart, int sourcePosition) {
		if(declStart.precedingSubChannelTokens == null)
			return null;
		ArrayList<Token> docComments = null;
		for (Token token : declStart.precedingSubChannelTokens) {
			if(token.getStartPos() < sourcePosition) {
				assertTrue(token.getEndPos() <= sourcePosition);
				continue;
			}
			
			if(DeeTokenSemantics.tokenIsDocComment(token)) {
				docComments = docComments == null ? new ArrayList<Token>(2) : docComments;
				docComments.add(token);
			}
		}
		return docComments;
	}
	
	private static ArrayList<Token> END_DOCCOMMENTS_READ = new ArrayList<>(0);
	
	public class DefUnitParseHelper extends ParseHelper {
		
		protected ArrayList<Token> comments;
		
		public DefUnitParseHelper() {
			super(-1);
			
			comments = readStartDDocComments(lookAheadElement(), thisParser().getSourcePosition());
			if(comments != null) {
				nodeStart = comments.get(0).getStartPos();
			} else {
				nodeStart = lookAheadElement().getStartPos();
			}
		}
		
		public Token[] parseEndDDocComments() {
			
			parsing: {
				if(ruleBroken) break parsing;
				
				LexElement nextLexElement = lookAheadElement();
				if(nextLexElement.precedingSubChannelTokens == null)
					break parsing;
				
				for (Token token : nextLexElement.precedingSubChannelTokens) {
					if(token.type == DeeTokens.EOL)
						break;
					if(DeeTokenSemantics.tokenIsDocComment(token)) {
						if(token.type == DeeTokens.COMMENT_LINE && token.getSourceValue().startsWith("///")) {
							comments = lazyInitArrayList(comments);
							comments.add(token);
							thisParser().getEnabledLexSource().setSourcePosition(token.getEndPos());
						}
						break;
					}
				}
			}
			Token[] result = comments == null ? null : ArrayUtil.createFrom(comments, Token.class);
			discardDocComments();
			return result;
		}
		
		public final void discardDocComments() {
			comments = END_DOCCOMMENTS_READ;
		}
		
		@Override
		public <T extends ASTNode> T conclude(T node) {
			assertTrue(comments == END_DOCCOMMENTS_READ);
			return super.conclude(node);
		}
	}
	
	/* ----------------------------------------------------------------- */
	
	public AbstractParser.NodeResult<Module> parseModule(String defaultModuleName) {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNode> members = parseDeclarations(null, true);
		assertTrue(lookAhead() == DeeTokens.EOF);
		consumeSubChannelTokens(); // Ensure pending whitespace is consumed as well
		assertTrue(getSourcePosition() == lookAheadElement().getStartPos());
		//assertTrue(getSourcePosition() == getSource().length()); //This is not true if explicit EOF token is present 
		
		SourceRange modRange = new SourceRange(0, getSourcePosition());
		
		if(md != null) {
			return result(false, conclude(modRange, new Module(md.comments, md.getModuleSymbol(), md, members)));
		} else {
			return result(false, conclude(modRange, Module.createModuleNoModuleDecl(defaultModuleName, members)));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(lookAhead() != DeeTokens.KW_MODULE) {
			return null;
		}
		DefUnitParseHelper parse = new DefUnitParseHelper();
		consumeLookAhead();
		
		ArrayList<Token> packagesList = new ArrayList<Token>(0);
		BaseLexElement moduleId;
		
		while(true) {
			moduleId = parse.consumeExpectedIdentifier();
			
			if(!moduleId.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(moduleId.getToken());
				moduleId = null;
				continue;
			}
			break;
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		Token[] comments = parse.parseEndDDocComments();
		
		return parse.conclude(new DeclarationModule(comments, arrayViewG(packagesList), moduleId));
	}
	
	public ArrayView<ASTNode> parseDeclarations(DeeTokens nodeListTerminator, boolean consumeCloseBrackets) {
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
			break;
			
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
			break;
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
		
		NodeResult<? extends DeclarationAttrib> stcDeclResult = parseAttributeDeclaration(statementsOnly);
		if(stcDeclResult != null) 
			return stcDeclResult;
		
		return parseDeclaration_varOrFunction(precedingIsSTCAttrib);
	}
	
	public NodeResult<? extends DeclarationAttrib> parseAttributeDeclaration(boolean statementsOnly) {
		return parseAttributeDeclaration(statementsOnly, true);
	}
	public NodeResult<? extends DeclarationAttrib> parseAttributeDeclaration(boolean statementsOnly, 
		boolean parseBody) {
		switch (lookAheadGrouped()) {
		case KW_ALIGN: 
			if(statementsOnly) return nullResult();
			return parseDeclarationAlign(parseBody);
		case KW_PRAGMA: 
			return parseDeclarationPragma(statementsOnly, parseBody);
		case PROTECTION_KW: 
			if(statementsOnly) return nullResult();
			return parseDeclarationProtection(parseBody);
		case KW_EXTERN: 
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return parseDeclarationExternLinkage(parseBody);
			}
			return parseDeclarationBasicAttrib(parseBody);
		case AT:
			if(lookAhead(1) == DeeTokens.IDENTIFIER) {
				return parseDeclarationAtAttrib(parseBody);
			}
			break;
		case KW_AUTO:
			return parseDeclarationBasicAttrib(parseBody);
		case KW_ENUM:
			return parseDeclarationBasicAttrib(parseBody);
		case ATTRIBUTE_KW:
			if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
				break; // this will be parsed as a type modifier reference
			}
			
			return parseDeclarationBasicAttrib(parseBody);
		default:
		}
		return null;
	}
	
	protected ArrayView<DeclarationAttrib> parseDefinitionAttributes(ParseHelper parse) {
		ArrayList<DeclarationAttrib> stcList = null;
		
		while(true) {
			NodeResult<? extends DeclarationAttrib> stcResult = parseAttributeDeclaration(false, false);
			if(stcResult == null) {
				break;
			}
			assertTrue(stcResult.node != null);
			parse.checkResult(stcResult);
			
			stcList = NewUtils.lazyInitArrayList(stcList);
			stcList.add(stcResult.node);
			if(parse.ruleBroken) {
				break;
			}
		}
		parse.requireBrokenCheck();
		return arrayView(stcList);
	}
	
	/* --------------------- DEFINITIONS --------------------- */
	
	public NodeResult<? extends IDeclaration> parseDeclaration_varOrFunction(boolean precedingIsSTCAttrib) {
		if(!canParseTypeReferenceStart(lookAhead())) {
			return declarationNullResult();
		}
		
		DefUnitParseHelper parse = new DefUnitParseHelper();
		
		NodeResult<Reference> startRef = parseTypeReference(); // This parses (BasicType + BasicType2) of spec
		assertNotNull(startRef.node);
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
				return parseDefinitionFunction_afterIdentifier(parse, ref, defId);
			}
			return parseDefinitionVariable_afterIdentifier(parse, ref, defId);
		} else {
			parse.consumeExpected(DeeTokens.IDENTIFIER);
			parse.consumeRequired(DeeTokens.SEMICOLON);
			parse.discardDocComments();
			return parse.resultConclude(new IncompleteDeclaration(ref));
		}
	}
	
	public static NodeResult<? extends IDeclaration> declarationNullResult() {
		return AbstractParser.<MissingDeclaration>result(false, null);
	}
	
	/* ----------------------------------------- */
	
	
	protected NodeResult<? extends DefinitionVariable> parseDefinitionVariable_afterIdentifier(
		DefUnitParseHelper parse, Reference ref, ProtoDefSymbol defId) 
	{
		ArrayList<DefVarFragment> fragments = null;
		IInitializer init = null;
		Reference cstyleSuffix = null;
		
		final boolean isAutoRef = ref == null;
		
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
		Token[] comments = parse.parseEndDDocComments();
		
		if(isAutoRef) {
			return parse.resultConclude(new DefinitionAutoVariable(comments, defId, init, arrayView(fragments)));
		}
		return parse.resultConclude(
			new DefinitionVariable(comments, defId, ref, cstyleSuffix, init, arrayView(fragments)));
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
			InitializerArray arrayInit = arrayInitResult.node;
			assertTrue(arrayInit.getData().hasErrors() == false);
			
			Expression fullInitExp = parseExpression_fromUnary(InfixOpType.ASSIGN, arrayInit);
			if(fullInitExp == arrayInit) {
				return arrayInitResult;
			}
			if(!arrayInitializerCouldParseAsArrayLiteral(arrayInit)) {
				ParserError error = createError(ParserErrorTypes.INIT_USED_IN_EXP, arrayInit.getSourceRange(), null);
				arrayInit.removeData(NodeData.DEFAULT_PARSED_STATUS.getClass());
				conclude(error, arrayInit);
			} else {
				// Even if initializer can be parsed as array literal, we place it in exp without any node conversion
				// (this might change in future)
			}
			return result(false, fullInitExp);
		}
		if(lookAhead() == DeeTokens.OPEN_BRACE) {
			DeeParserState savedParserState = thisParser().saveParserState();
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
			} else if(!(entry.value instanceof Expression)) {
				return false;
			}
			boolean isMapEntry = entry.index != null;
			if(isMapEntry != mustBeMapEntries)
				return false;
		}
		return true;
	}

	public NodeResult<Expression> parseExpInitializer(boolean createMissing) {
		return createMissing ? 
			parseAssignExpression_toMissing(true, RULE_INITIALIZER) : 
			parseAssignExpression();
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
				
				if(lookAhead() == DeeTokens.COLON && initializerCanParseAsExp(initializer)) {
					if(initializer instanceof InitializerArray) {
						index = (InitializerArray) initializer;
					} else {
						index = (Expression) initializer;
					}
					consumeLookAhead(DeeTokens.COLON);
					initializer = parseNonVoidInitializer(true).node;
				}
			}
			
			ASTNode startNode = index != null ? index : initializer.asNode();
			return concludeNode(srToPosition(startNode, new ArrayInitEntry(index, initializer)));
		}
		
	}
	
	public static boolean initializerCanParseAsExp(IInitializer initializer) {
		return initializer instanceof Expression || initializer instanceof InitializerArray;
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
		DefUnitParseHelper parse = new DefUnitParseHelper();
		if(tryConsume(DeeTokens.KW_THIS) == false) {
			return null;
		}
		
		ProtoDefSymbol defId = defSymbol(lastLexElement()); // TODO: mark this as special DefSymbol
		return parse_FunctionLike(true, null, defId, parse).upcastTypeParam();
	}
	
	/**
	 * Parse a function from this point:
	 * http://dlang.org/declaration.html#DeclaratorSuffix
	 */
	protected NodeResult<DefinitionFunction> parseDefinitionFunction_afterIdentifier(DefUnitParseHelper parse,
		Reference retType, ProtoDefSymbol defId) {
		assertTrue(defId.isMissing() == false);
		
		return parse_FunctionLike(false, retType, defId, parse).upcastTypeParam();
	}
	
	protected NodeResult<? extends AbstractFunctionDefinition> parse_FunctionLike(boolean isConstrutor, 
		Reference retType, ProtoDefSymbol defId, DefUnitParseHelper parse) {
		
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
		
		Token[] comments = parse.parseEndDDocComments();
		
		if(isConstrutor) {
			return parse.resultConclude(new DefinitionConstructor(
				comments, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody));
		}
		
		return parse.resultConclude(new DefinitionFunction(
			comments, retType, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody));
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
				NodeResult<Resolvable> typeOrCondExp = parseTypeOrExpression(InfixOpType.CONDITIONAL, true);
				specialization = nullTypeOrExpToParseMissing(typeOrCondExp.node);
			}
			if(tryConsume(DeeTokens.ASSIGN)) {
				init = nullTypeOrExpToParseMissing(parseTypeOrAssignExpression(true).node);
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
			parse.setStartPosition(getSourcePosition()); // It will be missing element
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
		
		boolean isMixin = false;
		if(tryConsume(DeeTokens.KW_MIXIN, DeeTokens.KW_TEMPLATE)) {
			isMixin = true;
		} else if(!tryConsume(DeeTokens.KW_TEMPLATE)) {
			return null;
		}
		adp.parseAggregate(false);
		adp.parseDeclarationBlockBody();
		
		Token[] comments = adp.parseEndDDocComments();
		
		return adp.resultConclude(new DefinitionTemplate(
			comments, isMixin, adp.defId, adp.tplParams, adp.tplConstraint, adp.getDeclBlock()));
	}
	
	public static final ParseRuleDescription RULE_AGGR_BODY = new ParseRuleDescription("AggregateBody");
	
	public class AggregateDefinitionParse extends DefUnitParseHelper {

		protected ProtoDefSymbol defId = null;
		protected ArrayView<TemplateParameter> tplParams = null;
		protected Expression tplConstraint = null;
		protected IAggregateBody declBody = null;
		
		public AggregateDefinitionParse() {
			super();
		}
		
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
		
		ArrayView<ASTNode> declDefs = parseDeclarations(DeeTokens.CLOSE_BRACE, false);
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
		DefUnitParseHelper parse = new DefUnitParseHelper();
		if(!tryConsume(DeeTokens.KW_ALIAS))
			return null;
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN) {
			return parseDefinitionAlias_atFragmentStart();
		}
		
		// Note that there are heavy similarites between this code and var/function declaration parsing
		ArrayView<DeclarationAttrib> stc = null;
		Reference ref = null;
		ProtoDefSymbol defId = null;
		Reference cstyleSuffix = null;
		ArrayList<AliasVarDeclFragment> fragments = null;
		
		parsing: {
			DeeParserState savedParserState = thisParser().saveParserState();
			
			stc = parseDefinitionAttributes(parse);
			if(parse.checkRuleBroken()) break parsing;
			
			NodeResult<Reference> refResult = parseTypeReference();
			ref = refResult.node;
			if(refResult.ruleBroken) break parsing;
			if(ref == null) {
				if(stc == null) {
					return parseDefinitionAlias_atFragmentStart(); // Return error as if DefinitionAlias was parsed
				} else {
					ref = parseMissingTypeReference(true);
					break parsing;
				}
			}
			
			if(lookAhead() != DeeTokens.IDENTIFIER && couldHaveBeenParsedAsId(ref)) {
				thisParser().restoreOriginalState(savedParserState);
				return parseDefinitionAlias_atFragmentStart(); // Return error as if trying to parse DefinitionAlias
			} else {
				defId = parseDefId();
			}
			
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionAliasFunctionDecl(parse, stc, ref, defId);
			}
			
			cstyleSuffix = parseCStyleSuffix(parse);
			if(parse.checkRuleBroken()) break parsing;
			
			while(parse.consumeOptional(DeeTokens.COMMA)) {
				fragments = lazyInitArrayList(fragments);
				fragments.add(parseAliasVarDeclFragment());
			}
		}
		defId = nullIdToParseMissingDefId(defId);
		
		parse.clearRuleBroken().consumeRequired(DeeTokens.SEMICOLON);
		Token[] comments = parse.parseEndDDocComments();
		return parse.resultConclude(
			new DefinitionAliasVarDecl(comments, stc, ref, defId, cstyleSuffix, arrayView(fragments)));
	}
	
	public NodeResult<? extends IDeclaration> parseDefinitionAliasFunctionDecl(DefUnitParseHelper parse, 
		ArrayView<DeclarationAttrib> stc, Reference ref, ProtoDefSymbol defId) {
		
		ArrayView<IFunctionParameter> fnParams = parseFunctionParameters(parse, true);
		ArrayView<FunctionAttributes> fnAttributes = null;
		if(!parse.ruleBroken) {
			fnAttributes = parseFunctionAttributes();
		} else {
			parse.clearRuleBroken();
		}
		parse.consumeRequired(DeeTokens.SEMICOLON);
		Token[] comments = parse.parseEndDDocComments();
		return parse.resultConclude(
			new DefinitionAliasFunctionDecl(comments, stc, ref, defId, fnParams, fnAttributes));
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
		DefUnitParseHelper parse = new DefUnitParseHelper();
		consumeLookAhead(DeeTokens.KW_ENUM);
		
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
		Token[] comments = parse.parseEndDDocComments();
		return parse.resultConclude(new DefinitionEnum(comments, defId, type, body));
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
		AggregateDefinitionParse adp = new AggregateDefinitionParse();
		if(!(tryConsume(DeeTokens.KW_STRUCT) || tryConsume(DeeTokens.KW_UNION)))
			return null;
		
		boolean isStruct = lastLexElement().token.type == DeeTokens.KW_STRUCT;
		if(lookAhead() != DeeTokens.IDENTIFIER) {
			adp.defId = nullIdToParseMissingDefId(null);
			adp.parseDeclarationBlockBody();
		} else {
			adp.parseAggregate(true);
			adp.parseAggregateBody();
		}
		
		Token[] comments = adp.parseEndDDocComments();
		
		return adp.resultConclude(isStruct ?
			new DefinitionStruct(comments, adp.defId, adp.tplParams, adp.tplConstraint, adp.declBody) :
			new DefinitionUnion (comments, adp.defId, adp.tplParams, adp.tplConstraint, adp.declBody));
	}
	
	public NodeResult<DefinitionClass> parseDefinitionClass() {
		return parseDefinition_ClassOrInterface().upcastTypeParam();
	}
	public NodeResult<DefinitionInterface> parseDefinitionInterface() {
		return parseDefinition_ClassOrInterface().upcastTypeParam();
	}
	
	protected NodeResult<? extends DefinitionClass> parseDefinition_ClassOrInterface() {
		AggregateDefinitionParse adp = new AggregateDefinitionParse();
		if(!(tryConsume(DeeTokens.KW_CLASS) || tryConsume(DeeTokens.KW_INTERFACE)))
			return null;
		
		boolean isClass = lastLexElement().token.type == DeeTokens.KW_CLASS;
		
		SimpleListParseHelper<Reference> baseClasses = new TypeReferenceSimpleListParse();
		parsing: {
			adp.parseAggregate(true);
			if(adp.ruleBroken) break parsing;
			
			if(tryConsume(DeeTokens.COLON)) {
				baseClasses.parseSimpleList(DeeTokens.COMMA, false, false);
			}
			
			adp.parseAggregateBody();
		}
		
		Token[] comments = adp.parseEndDDocComments();
		
		return adp.resultConclude(isClass ?
			new DefinitionClass(
				comments, adp.defId, adp.tplParams, adp.tplConstraint, baseClasses.members, adp.declBody) :
			new DefinitionInterface(
				comments, adp.defId, adp.tplParams, adp.tplConstraint, baseClasses.members, adp.declBody)
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
	
	public NodeResult<? extends IDeclaration> parseDeclarationMixin() {
		DefUnitParseHelper parse = new DefUnitParseHelper();
		if(!tryConsume(DeeTokens.KW_MIXIN))
			return null;
		
		NodeResult<Reference> tplInstanceResult = parseTypeReference_ToMissing(true);
		Reference tplInstance = tplInstanceResult.node;
		
		if(!tplInstanceResult.ruleBroken && lookAhead() == DeeTokens.IDENTIFIER) {
			ProtoDefSymbol defId = parseDefId();
			parse.consumeRequired(DeeTokens.SEMICOLON);
			Token[] comments = parse.parseEndDDocComments();
			return parse.resultConclude(new DefinitionNamedMixin(comments, defId, tplInstance));
		} else {
			parse.consumeRequired(DeeTokens.SEMICOLON);
			parse.discardDocComments();
			return parse.resultConclude(new DeclarationMixin(tplInstance));
		}
	}
	
	/* -------------------- Function-like declarations -------------------- */

	
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
	
}