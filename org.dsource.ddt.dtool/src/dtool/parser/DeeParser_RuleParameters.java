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
import dtool.ast.definitions.DefUnit.DefUnitTuple;
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
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

/** Helper class to parse function and template parameters */
public final class DeeParser_RuleParameters extends AbstractParserRule {
	
	protected static enum TplOrFnMode { TPL, FN, AMBIG }
	
	public TplOrFnMode mode;
	public ArrayList<Object> params;
	public boolean properlyTerminated;
	
	public DeeParser_RuleParameters(DeeParser deeParser, TplOrFnMode mode) {
		super(deeParser);
		this.mode = mode;
	}
	
	public boolean isAmbiguous() {
		return mode == TplOrFnMode.AMBIG;
	}
	
	protected DeeParser_RuleParameters doParse() {
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
			return connect(sr(lastLexElement(), new CStyleVarArgsParameter()));
		}
		
		if(mode != TplOrFnMode.FN && lookAhead() == DeeTokens.KW_ALIAS) {
			setMode(TplOrFnMode.TPL);
			return matchTemplateAliasParameter();
		}
		
		if(mode != TplOrFnMode.FN && tryConsume(DeeTokens.KW_THIS)) {
			setMode(TplOrFnMode.TPL);
			BaseLexElement id = consumeExpectedIdentifier();
			return connect(srToPosition(nodeStart, new TemplateThisParam(defUnitTuple(id, null))));
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
		BaseLexElement id = null;
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
			
			sr = SourceRange.srStartToEnd(nodeStart, getLexPosition());
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
	
	public static boolean couldHaveBeenParsedAsId(Reference ref) {
		return ref instanceof RefIdentifier;
	}
	
	public static DefUnitTuple convertRefIdToDef(Reference ref) {
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
