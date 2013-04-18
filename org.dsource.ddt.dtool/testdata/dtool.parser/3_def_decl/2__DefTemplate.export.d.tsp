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

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template args

#@_TYPE_OR_EXP《#@TYPE_REFS__NO_BROKEN ● #@EXP_COND__NO_POSSIBLE_REFS ● #@NO_ROE》
#@_TYPE_OR_EXP_SAMPLE《#@TYPE_REFS__NO_BROKEN__LITE ● #@EXP_COND__LITE ● #@NO_ROE》

#@TPLARG_1《
  ►#?STRUCTURE_EXPECTED!【foo●TemplateTypeParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo : #@TYPEREFS_NB_ANY●TemplateTypeParam(DefSymbol #@TYPEREFS_NB_ANY)】●
  ►#?STRUCTURE_EXPECTED!【foo = #@TYPEREFS_NB_ANY●TemplateTypeParam(DefSymbol #@TYPEREFS_NB_ANY)】●
  ►#?STRUCTURE_EXPECTED!【foo : #@TYPEREFS_NB_ANY__LITE = #@TYPEREFS_NB_ANY●TemplateTypeParam(DefSymbol #@TYPEREFS_NB_ANY__LITE #@TYPEREFS_NB_ANY)】●

  ►#?STRUCTURE_EXPECTED!【#@TYPE_REFS__NO_BROKEN foo●TemplateValueParam(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【foo[ #error(EXP_CLOSE_BRACKET)●TemplateValueParam(RefTypeDynArray(?) DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO__LITE = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO__LITE #@EXP_COND_ORNO)】●

  ►#?STRUCTURE_EXPECTED!【alias foo●TemplateAliasParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【alias foo : #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【alias foo = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP)】●
  ►#?STRUCTURE_EXPECTED!【alias foo : #@_TYPE_OR_EXP_SAMPLE = #@_TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@_TYPE_OR_EXP_SAMPLE #@_TYPE_OR_EXP)】●
  
  ►#?STRUCTURE_EXPECTED!【TUPLE ...●TemplateTupleParam(DefSymbol)】●
  
  ►#?STRUCTURE_EXPECTED!【this THIS●TemplateThisParam(DefSymbol)】●
  ►#?STRUCTURE_EXPECTED!【this #@NO_ID●TemplateThisParam(DefSymbol)】●
¤》

#@TPLARGS《
  ►#@TPLARG_1●
  
  ►#?STRUCTURE_EXPECTED!【#@NO_REF  , #@TPL_PARAMS_SAMPLE1● TemplateTypeParam(DefSymbol) #@TPL_PARAMS_SAMPLE1】●
  ►#?STRUCTURE_EXPECTED!【#@TPL_PARAMS_SAMPLE1, #@TPLARG_1● #@TPL_PARAMS_SAMPLE1 #@TPLARG_1】●
  ►#?STRUCTURE_EXPECTED!【#@TPLARG_1, #@TPL_PARAMS_SAMPLE1● #@TPLARG_1 #@TPL_PARAMS_SAMPLE1】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template constraint
#@TPL_CONSTRAINT《
  ►#?STRUCTURE_EXPECTED!【if #error(EXP_OPEN_PARENS)【】● MissingParenthesesExpression】●
  ►#?STRUCTURE_EXPECTED!【if(#@NO_EXP)● #@NO_EXP】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY)● #@EXP_ANY 】●
  ►#?STRUCTURE_EXPECTED!【if(#@EXP_ANY__LITE #error(EXP_CLOSE_PARENS) #@BREAK● #@EXP_ANY__LITE #@BREAK】●
¤》
