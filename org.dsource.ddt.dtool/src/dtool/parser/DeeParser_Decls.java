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
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
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
import dtool.ast.definitions.DefinitionVarFragment;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.IFunctionParameter.FunctionParamAttribKinds;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.references.RefImportSelection;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModuleQualified;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.BodyStatement;
import dtool.ast.statements.EmptyBodyStatement;
import dtool.ast.statements.IStatement;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.util.ArrayView;

public class DeeParser_Decls extends DeeParser_RefOrExp {
	
	public DeeParser_Decls(String source) {
		super(new DeeLexer(source));
	}
	
	public DeeParser_Decls(DeeLexer deeLexer) {
		super(deeLexer);
	}
	
	/* ----------------------------------------------------------------- */
	
	// TODO: comments
	public DefUnitTuple defUnitNoComments(LexElement id) {
		return defUnitTuple(id, null);
	}
	
	public DefUnitTuple defUnitTuple(LexElement id, Comment[] comments) {
		return new DefUnitTuple(null, id, comments);
	}
	
	
	/* ----------------------------------------------------------------- */
	
	public Module parseModule() {
		DeclarationModule md = parseModuleDeclaration();
		
		ArrayView<ASTNeoNode> members = parseDeclDefs(null);
		assertTrue(lookAhead() == DeeTokens.EOF);
		assertTrue(lookAheadToken().getEndPos() == lexer.source.length());
		
		SourceRange modRange = new SourceRange(0, lexer.source.length());
		
		if(md != null) {
			return connect(new Module(md.getModuleSymbol(), null, md, members, modRange));
		} else {
			return connect(Module.createModuleNoModuleDecl(modRange, "__tests_unnamed" /*BUG here*/, members));
		}
	}
	
	public DeclarationModule parseModuleDeclaration() {
		if(!tryConsume(DeeTokens.KW_MODULE)) {
			return null;
		}
		int nodeStart = lastLexElement.getStartPos();
		
		ArrayList<String> packagesList = new ArrayList<String>(0);
		LexElement moduleId;
		
		while(true) {
			LexElement id = consumeExpectedIdentifier();
			
			if(!id.isMissingElement() && tryConsume(DeeTokens.DOT)) {
				packagesList.add(id.token.source);
				id = null;
			} else {
				consumeExpectedToken(DeeTokens.SEMICOLON);
				moduleId = id;
				break;
			}
		}
		assertNotNull(moduleId);
		
		String[] packages = ArrayUtil.createFrom(packagesList, String.class);
		SourceRange modDeclRange = srToCursor(nodeStart);
		return connect(new DeclarationModule(packages, moduleId.token, modDeclRange));
	}
	
	
	public ArrayView<ASTNeoNode> parseDeclDefs(DeeTokens nodeListTerminator) {
		ArrayList<ASTNeoNode> declarations = new ArrayList<ASTNeoNode>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			ASTNeoNode decl = parseDeclaration();
			if(decl == null) { 
				break;
			}
			declarations.add(decl);
		}
		
		return arrayView(declarations);
	}
	
	
	public DeclarationImport parseImportDeclaration() {
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
		
		return connect(new DeclarationImport(isStatic, arrayViewI(fragments), sr));
	}
	
	public IImportFragment parseImportFragment() {
		LexElement aliasId = null;
		ArrayList<String> packages = new ArrayList<String>(0);
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
				packages.add(id.token.source);
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
	
	public ASTNeoNode parseDeclaration() {
		return parseDeclaration(true);
	}
	
	/** This rule always returns a node, except only on EOF where it returns null. */
	public ASTNeoNode parseDeclaration(boolean acceptEmptyDecl) {
		while(true) {
			DeeTokens la = assertNotNull_(lookAheadGrouped());
			
			if(la == DeeTokens.EOF) {
				return null;
			}
			
			switch (la) {
			case KW_IMPORT: return parseImportDeclaration();
			
			case KW_MIXIN: return parseDeclarationMixinString();
			
			case KW_ALIGN: return parseDeclarationAlign();
			case KW_PRAGMA: return parseDeclarationPragma();
			case PROTECTION_KW: return parseDeclarationProtection();
			case KW_EXTERN:
				if(lookAhead(1) == DeeTokens.OPEN_PARENS) {
					return parseDeclarationExternLinkage();
				}
				// else fall-through to attrib
			case ATTRIBUTE_KW: 
				if(lookAhead() == DeeTokens.KW_STATIC && lookAhead(1) == DeeTokens.KW_IMPORT) { 
					return parseImportDeclaration();
				}
				if(isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS) {
					break; // go to parseReference
				}
				return parseDeclarationBasicAttrib();
			// @disable keyword?
			case KW_AUTO: // TODO:
				break;
			
			default:
				break;
			}
			
			RefParseResult startRef = parseReference_begin(false); // BasicType + BasicType2 in spec
			if(startRef.ref != null) {
				return parseDeclaration_ReferenceStart(startRef);
			}
			
			if(acceptEmptyDecl && tryConsume(DeeTokens.SEMICOLON)) {
				return connect(new DeclarationEmpty(srToCursor(lastLexElement)));
			} else {
				Token badToken = consumeLookAhead();
				reportSyntaxError(RULE_DECLARATION);
				return connect(new InvalidSyntaxElement(badToken));
			}
		}
	}
	
	
	
	/* ----------------------------------------- */
	
	protected ASTNeoNode parseDeclaration_ReferenceStart(RefParseResult refParse) {
		Reference ref = refParse.ref;
		boolean consumedSemiColon = false;
		
		if(!refParse.balanceBroken) {
			// XXX: WTF: ?
			if(ref instanceof RefModuleQualified) {
				if(((RefModuleQualified) ref).qualifiedName.name == null) {
					return connect(new InvalidDeclaration(ref, false, srToCursor(ref.getStartPos())));
				}
			}
			
			if(lookAhead() == DeeTokens.IDENTIFIER) {
				LexElement defId = consumeInput();
				
				if(lookAhead() == DeeTokens.OPEN_PARENS) {
					return parseDefinitionFunction_Reference_Identifier(ref, defId);
				}
				
				return parseDefinitionVariable_Reference_Identifier(ref, defId);
			}  else {
				reportErrorExpectedToken(DeeTokens.IDENTIFIER);
				if(tryConsume(DeeTokens.SEMICOLON)) {
					consumedSemiColon = true;
				}
				return connect(new InvalidDeclaration(ref, consumedSemiColon, srToCursor(ref.getStartPos())));
			}
			
		}
		// else: Balance is broken
		return connect(new InvalidDeclaration(ref, consumedSemiColon, srToCursor(ref.getStartPos())));
	}
	
	protected DefinitionVariable parseDefinitionVariable_Reference_Identifier(Reference ref, LexElement defId) {
		ArrayList<DefinitionVarFragment> fragments = new ArrayList<DefinitionVarFragment>();
		Initializer init = null;
		
		if(tryConsume(DeeTokens.ASSIGN)){ 
			init = parseInitializer();
		}
		
		while(tryConsume(DeeTokens.COMMA)) {
			DefinitionVarFragment defVarFragment = parseVarFragment();
			fragments.add(defVarFragment);
		}
		consumeExpectedToken(DeeTokens.SEMICOLON);
		
		SourceRange sr = srToCursor(ref.getStartPos());
		return connect(new DefinitionVariable(defUnitNoComments(defId), ref, init, arrayView(fragments), sr));
	}
	
	public DefinitionVarFragment parseVarFragment() {
		Initializer init = null;
		LexElement fragId = consumeExpectedIdentifier();
		if(!fragId.isMissingElement()) {
			if(tryConsume(DeeTokens.ASSIGN)){ 
				init = parseInitializer();
			}
		}
		return connect(new DefinitionVarFragment(defUnitNoComments(fragId), init, srToCursor(fragId)));
	}
	
	public static final ParseRuleDescription RULE_INITIALIZER = new ParseRuleDescription("Initializer");
	
	public Initializer parseInitializer() {
		Expression exp = parseAssignExpression();
		if(exp == null) {
			reportErrorExpectedRule(RULE_INITIALIZER);
			int elemStart = getParserPosition();
			// Advance parser position, mark the advanced range as missing element:
			consumeIgnoreTokens(DeeTokens.INTEGER, true);
			exp = connect(new MissingExpression(srToCursor(elemStart)));
		}
		return connect(new InitializerExp(exp, exp.getSourceRange()));
	}
	
	public static final ParseRuleDescription RULE_BODY = new ParseRuleDescription("Body");
	
	protected DefinitionFunction parseDefinitionFunction_Reference_Identifier(Reference retType, LexElement defId) {
		consumeLookAhead(DeeTokens.OPEN_PARENS);
		
		ArgumentListParseResult<IFunctionParameter> params = null;
		IStatement frequire = null;
		BodyStatement fbody = null;
		IStatement fensure = null;
		
		parsing: {
			params = parseFunctionParams();
			if(params.properlyTerminated == false) {
				break parsing;
			}
			
			if(tryConsume(DeeTokens.SEMICOLON)) { 
				fbody = connect(new EmptyBodyStatement(sr(lastLexElement.token)));
			} else if(lookAhead() == DeeTokens.OPEN_BRACE){
				fbody = assertNotNull_(parseBlockStatement());
			} else {
				reportErrorExpectedRule(RULE_BODY);
			}
		}
		
		return connect(new DefinitionFunction(defUnitNoComments(defId), null,retType, arrayViewI(params.list), 
			frequire, fensure, fbody, srToCursor(retType)));
	}
	
	protected ArgumentListParseResult<IFunctionParameter> parseFunctionParams() {
		ArrayList<IFunctionParameter> params = new ArrayList<IFunctionParameter>();
		
		boolean first = true;
		while(true) {
			IFunctionParameter arg = parseFunctionParameter();
			
			if(arg == null) {
				if(first && lookAhead() != DeeTokens.COMMA) {
					break;
				}
				Reference ref = createMissingReference(true);
				arg = connect(new NamelessParameter(null, ref, null, false, srToCursor(ref)));
			}
			params.add(arg);
			first = false;

			if(arg.isVariadic()) {
				break;
			}
			if(tryConsume(DeeTokens.COMMA)) {
				continue;
			}
			break;
		}
		boolean properlyTerminated = consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null;
		return new ArgumentListParseResult<IFunctionParameter>(params, properlyTerminated);
	}
	
	public IFunctionParameter parseFunctionParameter() {
		if(tryConsume(DeeTokens.TRIPLE_DOT)) {
			return connect(new CStyleVarArgsParameter(sr(lastLexElement.token)));
		}
		int nodeStart = lookAheadElement().getStartPos();
		
		ArrayList<FunctionParamAttribKinds> attribs = null;
		while(true) {
			FunctionParamAttribKinds paramAttrib = FunctionParamAttribKinds.fromToken(lookAhead());
			if(paramAttrib == null || isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS)
				break;
			consumeInput();
			attribs = lazyInitArrayList(attribs);
			attribs.add(paramAttrib);
		}
		
		Reference ref;
		LexElement id = null;
		Expression init = null;
		boolean isVariadic = false;
		
		parsing: {
			RefParseResult refResult = parseReference_begin();
			ref = refResult.ref;
			if(refResult.balanceBroken)
				break parsing;
			
			if(ref == null) {
				if(attribs == null)
					return null;
				ref = createMissingReference(true);
				break parsing;
			}
			
			id = consumeElementIf(DeeTokens.IDENTIFIER);
			
			if(tryConsume(DeeTokens.ASSIGN)) {
				init = parseAssignExpression_toMissing(true);
			} else if(tryConsume(DeeTokens.TRIPLE_DOT)) {
				isVariadic = true;
			}
		}
		
		if(id == null) {
			return connect(new NamelessParameter(arrayViewG(attribs), ref, init, isVariadic, srToCursor(nodeStart)));
		} else {
			return connect(new FunctionParameter(arrayViewG(attribs), ref, defUnitNoComments(id), init, isVariadic, 
				srToCursor(nodeStart)));
		}
	}
	
	public BodyStatement parseBlockStatement() {
		if(!tryConsume(DeeTokens.OPEN_BRACE))
			return null;
		int nodeStart = lastLexElement.getStartPos();
		
		ArrayList<IStatement> body = new ArrayList<IStatement>();
		consumeExpectedToken(DeeTokens.CLOSE_BRACE); 
		return connect(new BlockStatement(arrayViewI(body), true, srToCursor(nodeStart)));
	}
	
	/* ----------------------------------------- */
	
	protected class AttribBodyParseRule {
		public AttribBodySyntax bodySyntax = AttribBodySyntax.SINGLE_DECL;
		public NodeList2 declList;
		
		public AttribBodyParseRule parseAttribBody(boolean acceptEmptyDecl) {
			if(tryConsume(DeeTokens.COLON)) {
				bodySyntax = AttribBodySyntax.COLON;
				declList = parseDeclList(null);
			} else if(tryConsume(DeeTokens.OPEN_BRACE)) {
				bodySyntax = AttribBodySyntax.BRACE_BLOCK;
				declList = parseDeclList(DeeTokens.CLOSE_BRACE);
				consumeExpectedToken(DeeTokens.CLOSE_BRACE);
			} else {
				ASTNeoNode decl = parseDeclaration(acceptEmptyDecl);
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
		int declStart = lastLexElement.getStartPos();
		
		String linkageStr = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
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
				reportError(ParserErrorTypes.INVALID_EXTERN_ID, null, true);
			}
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(false);
			}
		} else {
			ab.parseAttribBody(false);
		}
		
		return connect(new DeclarationLinkage(linkageStr, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationAlign parseDeclarationAlign() {
		if(!tryConsume(DeeTokens.KW_ALIGN)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
		Token alignNum = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(tryConsume(DeeTokens.OPEN_PARENS)) {
			alignNum = consumeExpectedToken(DeeTokens.INTEGER_DECIMAL, true).token;
			
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(false);
			}
		} else {
			ab.parseAttribBody(false);
		}
		
		return connect(new DeclarationAlign(alignNum, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationPragma parseDeclarationPragma() {
		if(!tryConsume(DeeTokens.KW_PRAGMA)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		
		Symbol pragmaId = null;
		AttribBodyParseRule ab = new AttribBodyParseRule();
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			pragmaId = parseSymbol();
			
			// TODO pragma argument list;
			if(consumeExpectedToken(DeeTokens.CLOSE_PARENS) != null) {
				ab.parseAttribBody(true);
			}
		}
		
		return connect(
			new DeclarationPragma(pragmaId, null, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}

	public Symbol parseSymbol() {
		LexElement id = consumeExpectedToken(DeeTokens.IDENTIFIER, true);
		return connect(new Symbol(id.token.source, sr(id.token)));
	}
	
	public DeclarationProtection parseDeclarationProtection() {
		if(lookAheadGrouped() != DeeTokens.PROTECTION_KW) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement.getStartPos();
		Protection protection = DeeTokenSemantics.getProtectionFromToken(lastLexElement.getType());
		
		AttribBodyParseRule ab = new AttribBodyParseRule().parseAttribBody(false);
		return connect(new DeclarationProtection(protection, ab.bodySyntax, ab.declList, srToCursor(declStart)));
	}
	
	public DeclarationBasicAttrib parseDeclarationBasicAttrib() {
		AttributeKinds attrib = AttributeKinds.fromToken(lookAhead());
		if(attrib == null) {
			return null;
		}
		consumeLookAhead();
		int declStart = lastLexElement.getStartPos();
		
		AttribBodyParseRule apr = new AttribBodyParseRule().parseAttribBody(false);
		return connect(new DeclarationBasicAttrib(attrib, apr.bodySyntax, apr.declList, srToCursor(declStart)));
	}
	
	
	/* ----------------------------------------- */
	
	public DeclarationMixinString parseDeclarationMixinString() {
		if(!tryConsume(DeeTokens.KW_MIXIN)) {
			return null;
		}
		int declStart = lastLexElement.getStartPos();
		Expression exp = null;
		
		if(consumeExpectedToken(DeeTokens.OPEN_PARENS) != null) {
			exp = parseExpression();
			if(exp == null) {
				reportErrorExpectedRule(RULE_EXPRESSION);
			}
			consumeExpectedToken(DeeTokens.CLOSE_PARENS);
		}
		
		consumeExpectedToken(DeeTokens.SEMICOLON);
		return connect(new DeclarationMixinString(exp, srToCursor(declStart)));
	}
	
}