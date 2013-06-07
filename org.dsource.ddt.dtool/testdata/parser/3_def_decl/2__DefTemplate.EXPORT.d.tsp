Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template sample params

#@TPL_PARAMS_SAMPLE1《#?STRUCTURE_EXPECTED!【
  ►T : int = bar, ambigA = ambigB, int R : 10 = 1, alias A1 : 12 + 2 = foo, alias A2 : int = 2, TUPLE ..., this V●
  
  TemplateTypeParam(DefSymbol RefPrimitive RefIdentifier)
  TemplateTypeParam(DefSymbol RefIdentifier)
  TemplateValueParam(RefPrimitive DefSymbol Integer Integer)
  TemplateAliasParam(DefSymbol ExpInfix(? ?) RefIdentifier)
  TemplateAliasParam(DefSymbol RefPrimitive Integer)
  TemplateTupleParam(DefSymbol)
  TemplateThisParam(DefSymbol)
】》

#@TPL_PARAMS__SAMPLE《#?STRUCTURE_EXPECTED!【
  ►int R = 1, T = bar, alias A1 : 1+2●
  
  TemplateValueParam(RefPrimitive DefSymbol Integer)
  TemplateTypeParam(DefSymbol RefIdentifier)
  TemplateAliasParam(DefSymbol ExpInfix(? ?))
】》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template Params tester

#@_TYPE_OR_EXP《#@TYPE_REFS__NO_BROKEN ● #@EXP_COND__NO_POSSIBLE_REFS ● #@NO_TOE》
#@_TYPE_OR_EXP_SAMPLE《#@TYPE_REFS__NO_BROKEN__LITE ● #@EXP_COND__LITE ● #@NO_TOE》

#@TPLPARAMS_SINGLE《
  ►#?STRUCTURE_EXPECTED!【foo●TemplateTypeParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo = #@TYPEREF_NB #?CANT_FULLY_EXP_PARSE【#TPL_ONLY(flag)】 ●TemplateTypeParam(DefSymbol #@TYPEREF_NB)】●
  ►#?STRUCTURE_EXPECTED!【foo #TPL_ONLY(flag) : #@TYPEREF_NB ●TemplateTypeParam(DefSymbol #@TYPEREF_NB)】●
  ►#?STRUCTURE_EXPECTED!【foo #TPL_ONLY(flag) : #@TYPEREF_NB__LITE = #@TYPEREF_NB●TemplateTypeParam(DefSymbol #@TYPEREF_NB__LITE #@TYPEREF_NB)】●

  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__NO_BROKEN foo●TemplateValueParam(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【const shared int foo●TemplateValueParam(RefTypeModifier(RefTypeModifier(RefPrimitive)) DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【const foo #@NO_DEFID #FN_ONLY(flag)●TemplateValueParam(RefTypeModifier(RefIdentifier) #@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【foo[ #error(EXP_CLOSE_BRACKET)●TemplateValueParam(RefTypeDynArray(?) DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo[] #@NO_DEFID #FN_ONLY(flag) ●TemplateValueParam(RefTypeDynArray(?) #@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo = #@CONDEXP_NB●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NB)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo = #@_EXP_REFS_BREAK!《#@PARENS_BREAK》 #@EXP_REFS ●TemplateValueParam(RefIdentifier DefSymbol #@EXP_REFS)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo #TPL_ONLY(flag) : #@CONDEXP_NB ●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NB)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo #TPL_ONLY(flag) : #@CONDEXP_NB__LITE = #@CONDEXP_NB ●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NB__LITE #@CONDEXP_NB)】●

  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) alias foo●TemplateAliasParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) alias #@NO_DEFID●TemplateAliasParam(#@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) alias foo : #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) alias foo = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) alias foo : #@_TYPE_OR_EXP_SAMPLE = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP_SAMPLE #@_TYPE_OR_EXP)】●
  
  ►#?STRUCTURE_EXPECTED!【TUPLE ...●TemplateTupleParam(DefSymbol)】●
  
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) this THIS●TemplateThisParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【#TPL_ONLY(flag) this #@NO_ID●TemplateThisParam(DefSymbol)】●
¤》

#@TPLPARAMS《
  ►#@TPLPARAMS_SINGLE●
  
  ►#?STRUCTURE_EXPECTED!【#@NO_TPLPARAM  , #@TPL_PARAMS_SAMPLE1● #@NO_TPLPARAM #@TPL_PARAMS_SAMPLE1】●
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS__SAMPLE, #@TPLPARAMS_SINGLE   ● #@TPL_PARAMS__SAMPLE #@TPLPARAMS_SINGLE】●
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS__SAMPLE, #@NO_TPLPARAM● #@TPL_PARAMS__SAMPLE #@NO_TPLPARAM】●
¤》
#@TPLPARAMS__LITE《
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS__SAMPLE● #@TPL_PARAMS__SAMPLE】●
¤》

#@TPLPARAMS_NB《
  ►#@TPLPARAMS #?RULE_BROKEN【#:DISCARD_CASE】●
¤》

#@TPLPARAMS__SIMPLE_TESTER《
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS_SAMPLE1● #@TPL_PARAMS_SAMPLE1】●
  ►#?STRUCTURE_EXPECTED!【const int R = 1● TemplateValueParam(RefTypeModifier(RefPrimitive) DefSymbol Integer)】●
  ►#?STRUCTURE_EXPECTED!【T = bar●TemplateTypeParam(DefSymbol RefIdentifier)】● 
  ►#?STRUCTURE_EXPECTED!【int R = 1, #@NO_TPLPARAM● TemplateValueParam(RefPrimitive DefSymbol Integer) #@NO_TPLPARAM】●
¤》

#@NO_TPLPARAM《
  ►#?STRUCTURE_EXPECTED!【#@NO_TYPEREF ● TemplateTypeParam(DefSymbol) 】●
¤》


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template constraint
#@TPL_CONSTRAINT__SAMPLES《
  ►#?STRUCTURE_EXPECTED!【if #error(EXP_OPEN_PARENS)【】● MissingParenthesesExpression】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY__LITE)● #@EXP_ANY__LITE】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_NE__LITE #error(EXP_CLOSE_PARENS) #@BREAK● #@EXP_NE__LITE #@BREAK】●
¤》

#@TPL_CONSTRAINT《
  ►#@TPL_CONSTRAINT__SAMPLES●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY)● #@EXP_ANY 】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TPL_AGGREGATE_TESTER《
  ►#?STRUCTURE_EXPECTED!【(#@TPL_PARAMS__SAMPLE) ● #@TPL_PARAMS__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【(#@TPL_PARAMS__SAMPLE) #@TPL_CONSTRAINT__SAMPLES●#@TPL_PARAMS__SAMPLE #@TPL_CONSTRAINT__SAMPLES】●
  ►#?STRUCTURE_EXPECTED!【#error(EXPRULE_AggregateBody) #@BREAK if(#@NO_EXP)● #@BREAK #@NO_EXP】●
  ►#?STRUCTURE_EXPECTED!【(T : int = bar #@NO_ClosePARENS #@BREAK●
    TemplateTypeParam(DefSymbol RefPrimitive RefIdentifier) #@BREAK】●
¤》
