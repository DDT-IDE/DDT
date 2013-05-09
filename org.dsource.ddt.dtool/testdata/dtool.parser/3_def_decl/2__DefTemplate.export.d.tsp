Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template sample args

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
  ►(int R = 1, T = bar, alias A1 : 1+2)●
  
  TemplateValueParam(RefPrimitive DefSymbol Integer)
  TemplateTypeParam(DefSymbol RefIdentifier)
  TemplateAliasParam(DefSymbol ExpInfix(? ?))
】》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template args

#@_TYPE_OR_EXP《#@TYPE_REFS__NO_BROKEN ● #@EXP_COND__NO_POSSIBLE_REFS ● #@NO_ROE》
#@_TYPE_OR_EXP_SAMPLE《#@TYPE_REFS__NO_BROKEN__LITE ● #@EXP_COND__LITE ● #@NO_ROE》

#@TPLARG_1《
  ►#?STRUCTURE_EXPECTED!【foo●TemplateTypeParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo : #@TYPEREF_NB_ORNO●TemplateTypeParam(DefSymbol #@TYPEREF_NB_ORNO)】●
  ►#?STRUCTURE_EXPECTED!【foo = #@TYPEREF_NB_ORNO●TemplateTypeParam(DefSymbol #@TYPEREF_NB_ORNO)】●
  ►#?STRUCTURE_EXPECTED!【foo : #@TYPEREF_NB_ORNO__LITE = #@TYPEREF_NB_ORNO●TemplateTypeParam(DefSymbol #@TYPEREF_NB_ORNO__LITE #@TYPEREF_NB_ORNO)】●

  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__NO_BROKEN foo●TemplateValueParam(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo[ #error(EXP_CLOSE_BRACKET)●TemplateValueParam(RefTypeDynArray(?) DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo[] #@NO_DEFID #FN_UNAMBIG_ARG(flag) ●TemplateValueParam(RefTypeDynArray(?) #@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo : #@CONDEXP_NBOrNO●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NBOrNO)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo = #@CONDEXP_NBOrNO●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NBOrNO)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo : #@CONDEXP_NBOrNO__LITE = #@CONDEXP_NBOrNO●TemplateValueParam(RefIdentifier DefSymbol #@CONDEXP_NBOrNO__LITE #@CONDEXP_NBOrNO)】●

  ►#?STRUCTURE_EXPECTED!【alias foo●TemplateAliasParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【alias #@NO_DEFID●TemplateAliasParam(#@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【alias foo : #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【alias foo = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【alias foo : #@_TYPE_OR_EXP_SAMPLE = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP_SAMPLE #@_TYPE_OR_EXP)】●
  
  ►#?STRUCTURE_EXPECTED!【TUPLE ...●TemplateTupleParam(DefSymbol)】●
  
  ►#?STRUCTURE_EXPECTED!【this THIS●TemplateThisParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【this #@NO_ID●TemplateThisParam(DefSymbol)】●
¤》

#@TPLARGS《
  ►#@TPLARG_1●
  
  ►#?STRUCTURE_EXPECTED!【#@NO_TYPEREF  , #@TPL_PARAMS_SAMPLE1● TemplateTypeParam(DefSymbol) #@TPL_PARAMS_SAMPLE1】●
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS_SAMPLE1, #@TPLARG_1● #@TPL_PARAMS_SAMPLE1 #@TPLARG_1】●
  ►#?STRUCTURE_EXPECTED!【#@TPLARG_1, #@TPL_PARAMS_SAMPLE1● #@TPLARG_1 #@TPL_PARAMS_SAMPLE1】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template constraint
#@TPL_CONSTRAINT__SAMPLES《
  ►#?STRUCTURE_EXPECTED!【if #error(EXP_OPEN_PARENS)【】● MissingParenthesesExpression】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_OrNO__LITE)● #@EXP_OrNO__LITE】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY__LITE #error(EXP_CLOSE_PARENS) #@BREAK● #@EXP_ANY__LITE #@BREAK】●
¤》

#@TPL_CONSTRAINT《
  ►#@TPL_CONSTRAINT__SAMPLES●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY)● #@EXP_ANY 】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TPL_AGGREGATE_TESTER《
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS__SAMPLE ● #@TPL_PARAMS__SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS__SAMPLE #@TPL_CONSTRAINT__SAMPLES●#@TPL_PARAMS__SAMPLE #@TPL_CONSTRAINT__SAMPLES】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_OPEN_BRACE)【】 #@BREAK if(#@NO_EXP)● #@BREAK #@NO_EXP】●
  ►#?STRUCTURE_EXPECTED!【(T : int = bar #@NO_ClosePARENS #@BREAK●
    TemplateTypeParam(DefSymbol RefPrimitive RefIdentifier) #@BREAK】●
¤》
