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

import static dtool.util.NewUtils.lazyInitArrayList;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.core.CoreUtil.blindCast;

import java.util.ArrayList;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.SourceRange;
import dtool.ast.definitions.CStyleVarArgsParameter;
import dtool.ast.definitions.DefUnit.ProtoDefSymbol;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.IFunctionParameter.FunctionParamAttribKinds;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.definitions.TemplateThisParam;
import dtool.ast.definitions.TemplateTupleParam;
import dtool.ast.definitions.TemplateTypeParam;
import dtool.ast.definitions.TemplateValueParam;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.parser.AbstractParserRule.AbstractDecidingParserRule;
import dtool.util.ArrayView;

/** Helper class to parse function and template parameters */
public final class DeeParser_RuleParameters extends AbstractDecidingParserRule<DeeParser_RuleParameters> {
	
	protected static enum TplOrFnMode { TPL, FN, AMBIG }
	
	public TplOrFnMode mode;
	public ArrayList<Object> params;
	
	public DeeParser_RuleParameters(DeeParser deeParser, TplOrFnMode mode) {
		super(deeParser);
		this.mode = mode;
	}
	
	public boolean isAmbiguous() {
		return mode == TplOrFnMode.AMBIG;
	}
	
	@Override
	public DeeParser_RuleParameters parse(ParseHelper parse) {
		return parse(parse, false);
	}
	
	public DeeParser_RuleParameters parse(ParseHelper parse, boolean isOptional) {
		if(parse.consume(DeeTokens.OPEN_PARENS, isOptional, true) == false)
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
		parse.consumeRequired(DeeTokens.CLOSE_PARENS);
		return this;
	}
	
	public Object parseParameter() {
		return parseParameter(false);
	}
	public Object parseParameter(boolean returnNullOnMissing) {
		ParseHelper parse = new ParseHelper(lookAheadElement());
		
		if(mode != TplOrFnMode.TPL && tryConsume(DeeTokens.TRIPLE_DOT)) {
			setMode(TplOrFnMode.FN);
			return parse.conclude(new CStyleVarArgsParameter());
		}
		
		if(mode != TplOrFnMode.FN && lookAhead() == DeeTokens.KW_ALIAS) {
			setMode(TplOrFnMode.TPL);
			return parseTemplateAliasParameter_start();
		}
		
		if(mode != TplOrFnMode.FN && tryConsume(DeeTokens.KW_THIS)) {
			setMode(TplOrFnMode.TPL);
			ProtoDefSymbol defId = parseDefId();
			return parse.conclude(new TemplateThisParam(defId));
		}
		
		ArrayList<FunctionParamAttribKinds> attribs = null;
		if(mode != TplOrFnMode.TPL) {
			while(true) {
				FunctionParamAttribKinds paramAttrib = FunctionParamAttribKinds.fromToken(lookAhead());
				if(paramAttrib == null || isTypeModifier(lookAhead()) && lookAhead(1) == DeeTokens.OPEN_PARENS)
					break;
				
				setMode(TplOrFnMode.FN);
				consumeLookAhead();
				attribs = lazyInitArrayList(attribs);
				attribs.add(paramAttrib);
			}
		}
		
		return new AmbiguousParameter().parseAmbiguousParam(returnNullOnMissing, parse.nodeStart, attribs);
	}
	
	protected class AmbiguousParameter {
		
		ArrayList<FunctionParamAttribKinds> attribs;
		
		Reference ref;
		ProtoDefSymbol defId = null;
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
				
				if(lookAhead() == DeeTokens.IDENTIFIER) {
					defId = parseDefId(); 
				} else {
					if(!couldHaveBeenParsedAsId(ref)) {
						if(mode != TplOrFnMode.TPL) {
							setMode(TplOrFnMode.FN); // Can only be NamelessParam
						} else {
							defId = parseDefId(); // will create a missing defId;
						}
					}
				}
				
				if((defId == null) || (defId != null && mode != TplOrFnMode.TPL) ) {
					if(tryConsume(DeeTokens.TRIPLE_DOT)) {
						if(defId != null) {
							setMode(TplOrFnMode.FN); //FunctionParameter
						}
						isVariadic = true;
						break parsing;
					}
				}
				
				if(mode != TplOrFnMode.FN && tryConsume(DeeTokens.COLON)) {
					setMode(TplOrFnMode.TPL); // TemplateTypeParam or TemplateValueParam
					if(defId == null) { 
						typeSpecialization = parseTypeReference_ToMissing().node;
					} else {
						valueSpecialization = parseExpression_toMissing(InfixOpType.CONDITIONAL);
					}
				}
				if(tryConsume(DeeTokens.ASSIGN)) {
					if(mode == TplOrFnMode.FN) {
						paramDefault = new TypeOrExpResult(TypeOrExpStatus.EXP, parseAssignExpression_toMissing());
					} else if(mode == TplOrFnMode.TPL) {
						if(defId == null) {
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
						} else if(paramDefault.mode == TypeOrExpStatus.EXP && defId == null) {
							setMode(TplOrFnMode.FN); //NamelessParameter
						}
					}
				}
			}
			
			assertTrue(defId == null ? valueSpecialization == null : typeSpecialization == null);
			
			sr = SourceRange.srStartToEnd(nodeStart, getLexPosition());
			switch (mode) { default: throw assertUnreachable();
			case AMBIG: return this;
			case TPL: return convertToTemplate();
			case FN: return convertToFunction();
			}
		}
		
		public IFunctionParameter convertToFunction() {
			if(defId == null) {
				return conclude(sr,
					new NamelessParameter(arrayViewG(attribs), ref, paramDefault.toExpression().node, isVariadic));
			}
			return conclude(sr,
				new FunctionParameter(arrayViewG(attribs), ref, defId, paramDefault.toExpression().node, isVariadic));
		}
		
		public TemplateParameter convertToTemplate() {
			if(defId == null && couldHaveBeenParsedAsId(ref)) {
				defId = convertRefIdToDef(ref);
				return conclude(sr, isVariadic ?  
					new TemplateTupleParam(defId) :
					new TemplateTypeParam(defId, typeSpecialization, paramDefault.toReference().node));
			} else {
				defId = defId != null ? defId : new ProtoDefSymbol("", srAt(ref.getEndPos()), null);
				return conclude(sr, 
					new TemplateValueParam(defId, ref, valueSpecialization, paramDefault.toExpression().node));
			}
		}
		
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
