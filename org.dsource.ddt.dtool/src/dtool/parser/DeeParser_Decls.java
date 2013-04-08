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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.core.CoreUtil.blindCast;

import java.util.ArrayList;

import melnorme.utilbox.core.CoreUtil;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
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
import dtool.ast.definitions.CStyleVarArgsParameter;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionFunction.FunctionAttributes;
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.DefinitionVariable.DefinitionAutoVariable;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.IFunctionParameter.FunctionParamAttribKinds;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.definitions.TemplateAliasParam;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.definitions.TemplateThisParam;
import dtool.ast.definitions.TemplateTupleParam;
import dtool.ast.definitions.TemplateTypeParam;
import dtool.ast.definitions.TemplateValueParam;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.EmptyBodyStatement;
import dtool.ast.statements.FunctionBody;
import dtool.ast.statements.FunctionBodyOutBlock;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.InOutFunctionBody;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;
import dtool.util.NewUtils;

public abstract class DeeParser_Decls extends DeeParser_RefOrExp {
	
	/* ----------------------------------------------------------------- */
	
	// TODO: comments
	public DefUnitTuple defUnitNoComments(LexElement id) {
		return defUnitTuple(id, null);
	}
	
	public DefUnitTuple defUnitTuple(LexElement id, Comment[] comments) {
		return new DefUnitTuple(comments, id.getSourceValue(), id.getSourceRange(), null);
	}
	
	
	/* ----------------------------------------------------------------- */
	
	public AbstractParser.NodeResult<Module> parseModule() {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNeoNode> members = parseDeclDefs(null);
		assertTrue(lookAhead() == DeeTokens.EOF);
		consumeIgnoreTokens(); // Ensure pending whitespace is consumed as well
		assertTrue(lookAheadElement().getFullRangeStartPos() == getSource().length());
		
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
		LexElement moduleId;
		
		while(true) {
			LexElement id = consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.token);
				id = null;
			} else {
				consumeExpectedToken(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		SourceRange modDeclRange = srToCursor(nodeStart);
		return connect(new DeclarationModule(arrayViewG(packagesList), moduleId.token, modDeclRange));
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
	
	
	public NodeResult<DeclarationImport> parseImportDeclaration() {
		boolean isStatic = false;
		int nodeStart = lookAheadElement().getStartPos();
		
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
		SourceRange sr = srToCursor(nodeStart);
		
		return connectResult(false, new DeclarationImport(isStatic, arrayViewI(fragments), sr));
	}
	
	public IImportFragment parseImportFragment() {
		LexElement aliasId = null;
		ArrayList<Token> packages = new ArrayList<Token>(0);
		int refModuleStartPos = -1;
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.ASSIGN
			|| lookAhead() == DeeTokens.ASSIGN) {
			aliasId = consumeExpectedIdentifier();
			consumeLookAhead(DeeTokens.ASSIGN);
		}
		
		while(true) {
			LexElement id = consumeExpectedIdentifier();
			refModuleStartPos = refModuleStartPos == -1 ? id.getStartPos() : refModuleStartPos;
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packages.add(id.token);
			} else {
				RefModule refModule = 
					connect(new RefModule(arrayViewG(packages), id.token.source, srToCursor(refModuleStartPos))); 
				
				IImportFragment fragment = connect( (aliasId == null) ? 
					new ImportContent(refModule) : 
					new ImportAlias(defUnitNoComments(aliasId), refModule, srToCursor(aliasId.getStartPos()))
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
		
		SourceRange sr = srToCursor(fragment.getStartPos());
		return connect(new ImportSelective(fragment, arrayViewI(selFragments), sr));
	}
	
	public IImportSelectiveSelection parseImportSelectiveSelection() {
		LexElement aliasId = null;
		LexElement id = consumeExpectedIdentifier();
		
		if(tryConsume(DeeTokens.ASSIGN)){
			aliasId = id;
			id = consumeExpectedIdentifier();
		}
		
		RefImportSelection refImportSelection = connect(new RefImportSelection(idTokenToString(id), sr(id.token)));
		
		if(aliasId == null) {
			return refImportSelection;
		} else {
			DefUnitTuple aliasIdDefUnit = defUnitNoComments(aliasId);
			return connect(new ImportSelectiveAlias(aliasIdDefUnit, refImportSelection, srToCursor(aliasId)));
		}
	}
	
	/* --------------------- DECLARATION --------------------- */
	
	public static ParseRuleDescription RULE_DECLARATION = new ParseRuleDescription("Declaration");
	
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
		
		case KW_MIXIN: return ruleResult(parseDeclarationMixinString());
		
		case KW_ALIGN: return ruleResult(parseDeclarationAlign());
		case KW_PRAGMA: return ruleResult(parseDeclarationPragma());
		case PROTECTION_KW: return ruleResult(parseDeclarationProtection());
		case KW_EXTERN:
			if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
				return ruleResult(parseDeclarationExternLinkage());
			}
			return ruleResult(parseDeclarationBasicAttrib());
		case ATTRIBUTE_KW: 
			if(lookAhead() == DeeTokens.KW_STATIC && lookAhead(1) == DeeTokens.KW_IMPORT) { 
				return parseImportDeclaration();
			}
			if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
				break; // go to parseReference
			}
			return ruleResult(parseDeclarationBasicAttrib());
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
		case KW_ENUM:
			return ruleResult(parseDeclarationBasicAttrib());
			
		default:
			break;
		}
		
		NodeResult<Reference> startRef = parseTypeReference_do(false); // This parses (BasicType + BasicType2) of spec
		if(startRef.node != null) {
			Reference ref = startRef.node;
			
			if(startRef.ruleBroken) {
				return connectResult(true, new InvalidDeclaration(ref, false, srToCursor(ref.getStartPos())));
			}
			
			if(precedingIsSTCAttrib &&
				ref.getNodeType() == ASTNodeTypes.REF_IDENTIFIER && lookAhead(0) != DeeTokens.IDENTIFIER) {
				LexElement id = lastLexElement(); // Parse as auto declaration instead
				return ruleResult(parseDefinitionVariable_Reference_Identifier(null, id));
			}
			
			return parseDeclaration_referenceStart(ref);
		}
		
		if(tryConsume(DeeTokens.SEMICOLON)) {
			if(!acceptEmptyDecl) {
				reportSyntaxError(RULE_DECLARATION);
			}
			return connectResult(false, new DeclarationEmpty(srToCursor(lastLexElement())));
		} else {
			Token badToken = consumeLookAhead();
			reportSyntaxError(RULE_DECLARATION);
			return connectResult(false, new InvalidSyntaxElement(badToken));
		}
	}
	
	/* ----------------------------------------- */
	
	protected NodeResult<? extends ASTNeoNode> parseDeclaration_referenceStart(Reference ref) {
		boolean consumedSemiColon = false;
		
		if(lookAhead() == DeeTokens.IDENTIFIER) {
			LexElement defId = consumeInput();
			
			if(lookAhead() == DeeTokens.OPEN_PARENS) {
				return parseDefinitionFunction_Reference_Identifier(ref, defId);
			}
			
			return ruleResult(parseDefinitionVariable_Reference_Identifier(ref, defId));
		} else {
			addExpectedTokenError(DeeTokens.IDENTIFIER);
			if(tryConsume(DeeTokens.SEMICOLON)) {
				consumedSemiColon = true;
			}
			return connectResult(false, new InvalidDeclaration(ref, consumedSemiColon, srToCursor(ref.getStartPos())));
		}
	}
	
	protected DefinitionVariable parseDefinitionVariable_Reference_Identifier(Reference ref, LexElement defId) {
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
		consumeExpectedToken(DeeTokens.SEMICOLON);
		
		if(ref == null) {
			SourceRange sr = srToCursor(defId.getStartPos());
			return connect(new DefinitionAutoVariable(defUnitNoComments(defId), init, arrayView(fragments), sr));
		}
		SourceRange sr = srToCursor(ref.getStartPos());
		return connect(new DefinitionVariable(defUnitNoComments(defId), ref, init, arrayView(fragments), sr));
	}
	
	protected DefinitionVarFragment parseVarFragment(boolean isAutoRef) {
		Initializer init = null;
		LexElement fragId = consumeExpectedIdentifier();
		if(!fragId.isMissingElement()) {
			if(attemptConsume(DeeTokens.ASSIGN, isAutoRef)){ 
				init = parseInitializer();
			}
		}
		return connect(new DefinitionVarFragment(defUnitNoComments(fragId), init, srToCursor(fragId)));
	}
	
	public static final ParseRuleDescription RULE_INITIALIZER = new ParseRuleDescription("Initializer");
	
	public Initializer parseInitializer() {
		Expression exp = parseAssignExpression().node;
		if(exp == null) {
			reportErrorExpectedRule(RULE_INITIALIZER);
			int elemStart = getParserPosition();
			// Advance parser position, mark the advanced range as missing element:
			consumeIgnoreTokens(DeeTokens.INTEGER);
			exp = connect(new MissingExpression(srToCursor(elemStart)));
		}
		return connect(new InitializerExp(exp, exp.getSourceRange()));
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
			ParseRule_Parameters firstParams = parseParameters();
			
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
				ParseRule_Parameters secondParams = parseFunctionParameters();
				fnParams = secondParams.getAsFunctionParameters();
				if(secondParams.properlyTerminated == false) break parsing;
			} else if(firstParams.isAmbiguous()) {
				fnParams = firstParams.toFunctionParameters();
			}
			
			// Function attributes
			fnAttributes = parseFunctionAttributes();
			
			if(tplParams != null && tryConsume(DeeTokens.KW_IF)) {
				ParseRule_ExpressionAroundParentheses parseParensExp = new ParseRule_ExpressionAroundParentheses(true);
				tplConstraint = parseParensExp.exp;
				if(parseParensExp.parseBroken)
					break parsing;
			}
			
			if(tryConsume(DeeTokens.SEMICOLON)) { 
				fnBody = connect(new EmptyBodyStatement(sr(lastLexElement().token)));
				parseBroken = false;
			} else {
				NodeResult<? extends IFunctionBody> resultFunctionBody = parseFunctionBody();
				fnBody = resultFunctionBody.node;
				parseBroken = resultFunctionBody.node == null || resultFunctionBody.ruleBroken;
			}
		}
		
		return connectResult(parseBroken, init(srToCursor(retType), new DefinitionFunction(defUnitNoComments(defId), 
			tplParams, retType, fnParams, fnAttributes, tplConstraint, fnBody)));
	}
	
	protected static enum TplOrFnMode { TPL, FN, AMBIG }
	
	/** Helper class to parse function and template parameters */
	public class ParseRule_Parameters {
		
		public TplOrFnMode mode;
		public ArrayList<Object> params;
		public boolean properlyTerminated;
		
		public ParseRule_Parameters(TplOrFnMode mode) {
			this.mode = mode;
		}
		
		public boolean isAmbiguous() {
			return mode == TplOrFnMode.AMBIG;
		}
		
		protected ParseRule_Parameters doParse() {
			if(consumeExpectedToken(DeeTokens.OPEN_PARENS) == null)
				return this;
			params = new ArrayList<Object>();
			
			boolean first = true;
			while(true) {
				Object param = parseParameter(first && lookAhead() != DeeTokens.COMMA);
				
				if(param == null) {
					break;
				}
				params.add(param);
				first = false;
				
				if(tryConsume(DeeTokens.COMMA)) {
					continue;
				}
				break;
			}
			properlyTerminated = consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null;
			return this;
		}
		
		public Object parseParameter() {
			return parseParameter(false);
		}
		public Object parseParameter(boolean returnNullOnMissing) {
			int nodeStart = lookAheadElement().getStartPos();
			
			if(mode != TplOrFnMode.TPL && tryConsume(DeeTokens.TRIPLE_DOT)) {
				setMode(TplOrFnMode.FN);
				return connect(new CStyleVarArgsParameter(sr(lastLexElement().token)));
			}
			
			if(mode != TplOrFnMode.FN && lookAhead() == DeeTokens.KW_ALIAS) {
				setMode(TplOrFnMode.TPL);
				return parseTemplateAliasParameter();
			}
			
			if(mode != TplOrFnMode.FN && tryConsume(DeeTokens.KW_THIS)) {
				setMode(TplOrFnMode.TPL);
				LexElement id = consumeExpectedIdentifier();
				return connect(srToCursor(nodeStart), new TemplateThisParam(defUnitTuple(id, null)));
			}
			
			ArrayList<FunctionParamAttribKinds> attribs = null;
			if(mode != TplOrFnMode.TPL) {
				while(true) {
					FunctionParamAttribKinds paramAttrib = FunctionParamAttribKinds.fromToken(lookAhead());
					if(paramAttrib == null || isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS)
						break;
					
					setMode(TplOrFnMode.FN);
					consumeInput();
					attribs = lazyInitArrayList(attribs);
					attribs.add(paramAttrib);
				}
			}
			
			return new AmbiguousParameter().parseAmbiguousParam(returnNullOnMissing, nodeStart, attribs);
		}
		
		protected class AmbiguousParameter {
			
			ArrayList<FunctionParamAttribKinds> attribs;
			
			Reference ref;
			LexElement id = null;
			Reference typeSpecialization = null;
			Expression valueSpecialization = null;
			TypeOrExpResult paramDefault = new TypeOrExpResult(null, null);
			boolean isVariadic = false;
			
			SourceRange sr;
			
			public Object parseAmbiguousParam(boolean returnNullOnMissing, int nodeStart,
				ArrayList<FunctionParamAttribKinds> attribs) {
				this.attribs = attribs;
				
				// Possible outcomes from this point
				// NamelessParam or TemplateTypeParam
				// NamelessParam(variadic) or TemplateTupleParam
				// FunctionParameter or TemplateValueParam (isValueParam = true) 
				
				parsing: {
					NodeResult<Reference> refResult = parseTypeReference();
					ref = refResult.node;
					if(refResult.ruleBroken) {
						break parsing;
					}
					if(ref == null) {
						if(attribs == null && returnNullOnMissing) { // No Parameter at all
							return null;
						}
						ref = createMissingTypeReference(true);
						break parsing;
					}
					
					id = consumeElementIf(DeeTokens.IDENTIFIER);
					if(id == null) {
						if(!couldHaveBeenParsedAsId(ref)) {
							if(mode != TplOrFnMode.TPL) {
								setMode(TplOrFnMode.FN); // Can only be NamelessParam
							} else {
								id = consumeExpectedToken(DeeTokens.IDENTIFIER, true);
							}
						}
					}
					
					if((id == null) || (id != null && mode != TplOrFnMode.TPL) ) {
						if(tryConsume(DeeTokens.TRIPLE_DOT)) {
							if(id != null) {
								setMode(TplOrFnMode.FN); //FunctionParameter
							}
							isVariadic = true;
							break parsing;
						}
					}
					
					if(mode != TplOrFnMode.FN && tryConsume(DeeTokens.COLON)) {
						setMode(TplOrFnMode.TPL); // TemplateTypeParam or TemplateValueParam
						if(id == null) { 
							typeSpecialization = parseTypeReference_ToMissing().node;
						} else {
							valueSpecialization = parseExpression_toMissing(InfixOpType.CONDITIONAL);
						}
					}
					if(tryConsume(DeeTokens.ASSIGN)) {
						if(mode == TplOrFnMode.FN) {
							paramDefault = new TypeOrExpResult(TypeOrExpStatus.EXP, parseAssignExpression_toMissing());
						} else if(mode == TplOrFnMode.TPL) {
							if(id == null) {
								paramDefault = new TypeOrExpResult(TypeOrExpStatus.TYPE, 
									wrapReferenceForTypeOrExpParse(parseTypeReference_ToMissing().node));
							} else {
								paramDefault = new TypeOrExpResult(TypeOrExpStatus.EXP, 
									parseAssignExpression_toMissing());
							}
						} else {
							paramDefault = parseTypeOrExpression(InfixOpType.ASSIGN);
							if(paramDefault.isNull()) {
								TypeOrExpStatus toeMode = TypeOrExpStatus.TYPE_OR_EXP;
								paramDefault = new TypeOrExpResult(toeMode, createTypeOrExpMissingExp(toeMode, true));
							} else if(paramDefault.mode == TypeOrExpStatus.EXP && id == null) {
								setMode(TplOrFnMode.FN); //NamelessParameter
							}
						}
					}
				}
				
				assertTrue(id == null ? valueSpecialization == null : typeSpecialization == null);
				
				sr = srToCursor(nodeStart);
				switch (mode) { default: throw assertUnreachable();
				case AMBIG: return this;
				case TPL: return convertToTemplate();
				case FN: return convertToFunction();
				}
			}
			
			public IFunctionParameter convertToFunction() {
				if(id == null) {
					return connect(sr, 
						new NamelessParameter(arrayViewG(attribs), ref, paramDefault.toExpression().node, isVariadic));
				} else {
					return connect(sr,
						new FunctionParameter(arrayViewG(attribs), ref, defUnitNoComments(id), 
							paramDefault.toExpression().node, isVariadic));
				}
			}
			
			public TemplateParameter convertToTemplate() {
				if(id == null && couldHaveBeenParsedAsId(ref)) {
					return connect(sr, isVariadic ?  
						new TemplateTupleParam(convertRefIdToDef(ref)) :
						new TemplateTypeParam(convertRefIdToDef(ref), typeSpecialization, 
							paramDefault.toReference().node));
				} else {
					DefUnitTuple defUnitNoComments = id != null ? defUnitNoComments(id) 
						: new DefUnitTuple(null, "", srAt(ref.getEndPos()), null);
					return connect(sr,
						new TemplateValueParam(defUnitNoComments, ref, valueSpecialization,
							paramDefault.toExpression().node));
				}
			}
			
		}
		
		public boolean couldHaveBeenParsedAsId(Reference ref) {
			return ref instanceof RefIdentifier;
		}
		
		public DefUnitTuple convertRefIdToDef(Reference ref) {
			assertTrue(couldHaveBeenParsedAsId(ref));
			RefIdentifier refId = (RefIdentifier) ref;
			return new DefUnitTuple(null, refId.name == null ? "" : refId.name, ref.getSourceRange(), null);
		}
		
		protected void setMode(TplOrFnMode newMode) {
			if(mode == newMode)
				return;
			assertTrue(mode == TplOrFnMode.AMBIG);
			
			mode = newMode;
			if(params == null)
				return;
			
			ArrayList<AmbiguousParameter> oldParams = blindCast(params);
			params = new ArrayList<Object>();
			for (AmbiguousParameter param : oldParams) {
				params.add(mode == TplOrFnMode.FN ? param.convertToFunction() : param.convertToTemplate());
			} 
		}
		
		public final ArrayView<IFunctionParameter> getAsFunctionParameters() {
			assertTrue(mode == TplOrFnMode.FN);
			return arrayViewI(CoreUtil.<ArrayList<IFunctionParameter>>blindCast(params));
		}
		
		public final ArrayView<IFunctionParameter> toFunctionParameters() {
			assertTrue(isAmbiguous());
			setMode(TplOrFnMode.FN);
			return getAsFunctionParameters();
		}
		
		public final ArrayView<TemplateParameter> getAsTemplateParameters() {
			assertTrue(mode == TplOrFnMode.TPL);
			return arrayView(CoreUtil.<ArrayList<TemplateParameter>>blindCast(params));
		}
		
		public final ArrayView<TemplateParameter> toTemplateParameters() {
			assertTrue(isAmbiguous());
			setMode(TplOrFnMode.TPL);
			return getAsTemplateParameters();
		}
	}
	
	protected ASTNeoNode parseTemplateAliasParameter() {
		int nodeStart = lookAheadElement().getStartPos();
		consumeLookAhead(DeeTokens.KW_ALIAS);
		
		LexElement id = null;
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
		
		return connect(srToCursor(nodeStart), 
			new TemplateAliasParam(defUnitTuple(id, null), specialization, init));
	}
	
	protected final ParseRule_Parameters parseParameters() {
		return new ParseRule_Parameters(TplOrFnMode.AMBIG).doParse();
	}
	
	protected final ParseRule_Parameters parseFunctionParameters() {
		return new ParseRule_Parameters(TplOrFnMode.FN).doParse();
	}
	
	protected final ParseRule_Parameters parseTemplateParameters() {
		return new ParseRule_Parameters(TplOrFnMode.TPL).doParse();
	}
	
	public IFunctionParameter parseFunctionParameter() {
		return (IFunctionParameter) new ParseRule_Parameters(TplOrFnMode.FN).parseParameter();
	}
	
	public TemplateParameter parseTemplateParameter() {
		return (TemplateParameter) new ParseRule_Parameters(TplOrFnMode.TPL).parseParameter();
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
	
	protected NodeResult<? extends IFunctionBody> parseFunctionBody() {
		NodeResult<BlockStatement> blockResult = parseBlockStatement_do();
		if(blockResult.node != null)
			return blockResult;
		
		int nodeStart;
		if(lookAhead() == DeeTokens.KW_IN || lookAhead() == DeeTokens.KW_OUT || lookAhead() == DeeTokens.KW_BODY) {
			nodeStart = lookAheadElement().getStartPos();
		} else {
			nodeStart = lastLexElement().getEndPos(); // It will be missing element
		}
		
		boolean isOutIn = false;
		BlockStatement inBlock = null;
		FunctionBodyOutBlock outBlock = null;
		BlockStatement bodyBlock = null;
		
		boolean parseBroken = true;
		parsing: {
			if(tryConsume(DeeTokens.KW_IN)) {
				blockResult = parseBlockStatement_toMissing();
				inBlock = blockResult.node;
				if(blockResult.ruleBroken) 
					break parsing;
				
				if(lookAhead() == DeeTokens.KW_OUT) {
					NodeResult<FunctionBodyOutBlock> outBlockResult = parseOutBlock();
					outBlock = outBlockResult.node;
					if(outBlockResult.ruleBroken)
						break parsing;
				}
			} else if(lookAhead() == DeeTokens.KW_OUT) {
				isOutIn = true;
				
				NodeResult<FunctionBodyOutBlock> outBlockResult = parseOutBlock();
				outBlock = outBlockResult.node;
				if(outBlockResult.ruleBroken)
					break parsing;
				
				if(tryConsume(DeeTokens.KW_IN)) {
					blockResult = parseBlockStatement_toMissing();
					inBlock = blockResult.node;
					if(blockResult.ruleBroken) 
						break parsing;
				}
			}
			
			if(tryConsume(DeeTokens.KW_BODY)) {
				NodeResult<BlockStatement> blockStatementResult = parseBlockStatement_toMissing();
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
				return AbstractParser.<FunctionBody>ruleNullResult();
			}
			return connectResult(parseBroken, new FunctionBody(bodyBlock, srToCursor(nodeStart)));
		}
		return connectResult(parseBroken, new InOutFunctionBody(isOutIn, inBlock, outBlock, bodyBlock, 
			srToCursor(nodeStart)));
	}
	
	protected BlockStatement createMissingBlock(boolean reportMissingExpError, ParseRuleDescription expectedRule) {
		if(reportMissingExpError) {
			reportErrorExpectedRule(expectedRule);
		}
		int nodeStart = lastLexElement().getEndPos();
		return connect(new BlockStatement(srToCursor(nodeStart)));
	}
	
	public NodeResult<BlockStatement> parseBlockStatement_toMissing() {
		return parseBlockStatement_toMissing(false);
	}
	
	public NodeResult<BlockStatement> parseBlockStatement_required() {
		return parseBlockStatement_toMissing(true);
	}
	
	public NodeResult<BlockStatement> parseBlockStatement_toMissing(boolean breakIfMissing) {
		NodeResult<BlockStatement> block = parseBlockStatement_do();
		if(block.node == null) {
			return NodeResult.create(breakIfMissing, createMissingBlock(true, RULE_BLOCK));
		}
		return block;
	}
	
	protected NodeResult<BlockStatement> parseBlockStatement_do() {
		if(!tryConsume(DeeTokens.OPEN_BRACE))
			return ruleNullResult(); 
		int nodeStart = lastLexElement().getStartPos();
		
		ArrayView<IStatement> body = parseStatements();
		boolean parseBroken = consumeExpectedToken(DeeTokens.CLOSE_BRACE) == null; 
		return connectResult(parseBroken, new BlockStatement(body, true, srToCursor(nodeStart)));
	}
	
	private ArrayView<IStatement> parseStatements() {
		// TODO parse statements
		return CoreUtil.blindCast(parseDeclDefs(DeeTokens.CLOSE_BRACE));
	}
	
	protected NodeResult<FunctionBodyOutBlock> parseOutBlock() {
		if(!tryConsume(DeeTokens.KW_OUT))
			return ruleResult(null);
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
			
			NodeResult<BlockStatement> blockResult = parseBlockStatement_toMissing();
			block = blockResult.node;
			parseBroken = blockResult.ruleBroken;
		}
		
		return connectResult(parseBroken, new FunctionBodyOutBlock(id, block, srToCursor(nodeStart)));
	}
	
	/* ----------------------------------------- */
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public NodeList2 declList;
		
		public AttribBodyParseRule parseAttribBody(boolean acceptEmptyDecl, boolean enablesAutoDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				consumeExpectedToken(DeeTokens.CLOSE_BRACE);
			} else {
				ASTNeoNode decl = getResult(parseDeclaration(acceptEmptyDecl, enablesAutoDecl));
				if(decl == null) {
					reportErrorExpectedRule(RULE_DECLARATION);
				} else {
					declList = connect(
						new NodeList2(ArrayView.create(new ASTNeoNode[] {decl}), decl.getSourceRange()));
				}
			}
			return this;
		}
	}
	
	protected NodeList2 parseDeclList(DeeTokens bodyListTerminator) {
		int nodeListStart = getParserPosition();
		
		ArrayView<ASTNeoNode> declDefs = parseDeclDefs(bodyListTerminator);
		consumeIgnoreTokens();
		return connect(new NodeList2(declDefs, srToCursor(nodeListStart)));
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
		
		return connect(new DeclarationLinkage(linkageStr, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		
		Token alignNum = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		parsing: {
			if(tryConsume(DeeTokens.OPEN_PARENS)) {
				alignNum = consumeExpectedToken(DeeTokens.INTEGER_DECIMAL, true).token;
				
				if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) == null) 
					break parsing;
			}
			ab.parseAttribBody(false, false);
		}
		
		return connect(new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList, srToCursor(declStart)));
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
		
		return connect(
			new DeclarationPragma(pragmaId, null, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public Symbol parseSymbol() {
		LexElement id = consumeExpectedIdentifier();
		return connect(new Symbol(id.token.source, sr(id.token)));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement().getStartPos();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(lastLexElement().getType());
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(false, true);
		return connect(new DeclarationProtection(protection, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement().getStartPos();
		
		AttribBodyParseRule apr = new AttribBodyParseRule().parseAttribBody(false, true);
		return connect(new DeclarationBasicAttrib(attrib, apr.bodySyntax, apr.declList, srToCursor(declStart)));
	}
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastLexElement().getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseExpression().getNode_NoBrokenCheck();
			if(exp == null) {
				reportErrorExpectedRule(RULE_EXPRESSION);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(new DeclarationMixinString(exp, srToCursor(declStart)));
	}
	
}