Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template sample args

#@TPL_PARAMS_SAMPLE1《#?AST_STRUCTURE_EXPECTED!【
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

#@TYPE_REFS_ORNO《#@TYPE_REFS__NO_BROKEN●#@NO_REF》
#@TYPE_REFS_ORNO_SAMPLE《#@TYPE_REFS__NO_BROKEN__LITE●#@NO_REF》

#@EXP_COND_ORNO《#@EXP_COND●#@NO_EXP》
#@EXP_COND_ORNO_SAMPLE《#@EXP_COND__LITE●#@NO_EXP》

#@TYPE_OR_EXP《#@TYPE_REFS__NO_BROKEN ● #@EXP_COND__NO_POSSIBLE_REFS ● #@NO_ROE》
#@TYPE_OR_EXP_SAMPLE《#@TYPE_REFS__NO_BROKEN__LITE ● #@EXP_COND__LITE ● #@NO_ROE》


#@TPLARG_1《
  ►#?AST_STRUCTURE_EXPECTED!【foo●TemplateTypeParam(DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo : #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo = #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo : #@TYPE_REFS_ORNO_SAMPLE = #@TYPE_REFS_ORNO●TemplateTypeParam(DefSymbol #@TYPE_REFS_ORNO_SAMPLE #@TYPE_REFS_ORNO)】●

  ►#?AST_STRUCTURE_EXPECTED!【#@TYPE_REFS__NO_BROKEN foo●TemplateValueParam(#@TYPE_REFS__NO_BROKEN DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【foo[ #error(EXP_CLOSE_BRACKET)●TemplateValueParam(RefTypeDynArray(?) DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO)】●
  ►#?AST_STRUCTURE_EXPECTED!【Foo foo : #@EXP_COND_ORNO_SAMPLE = #@EXP_COND_ORNO●TemplateValueParam(RefIdentifier DefSymbol #@EXP_COND_ORNO_SAMPLE #@EXP_COND_ORNO)】●

  ►#?AST_STRUCTURE_EXPECTED!【alias foo●TemplateAliasParam(DefSymbol)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo : #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo = #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP)】●
  ►#?AST_STRUCTURE_EXPECTED!【alias foo : #@TYPE_OR_EXP_SAMPLE = #@TYPE_OR_EXP●TemplateAliasParam(DefSymbol #@TYPE_OR_EXP_SAMPLE #@TYPE_OR_EXP)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【TUPLE ...●TemplateTupleParam(DefSymbol)】●
  
  ►#?AST_STRUCTURE_EXPECTED!【this THIS●TemplateThisParam(DefSymbol)】●
¤》

#@TPLARGS《
  ►#@TPLARG_1●
  
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_REF  , #@TPL_PARAMS_SAMPLE1● TemplateTypeParam(DefSymbol) #@TPL_PARAMS_SAMPLE1】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TPL_PARAMS_SAMPLE1, #@TPLARG_1● #@TPL_PARAMS_SAMPLE1 #@TPLARG_1】●
  ►#?AST_STRUCTURE_EXPECTED!【#@TPLARG_1, #@TPL_PARAMS_SAMPLE1● #@TPLARG_1 #@TPL_PARAMS_SAMPLE1】●
¤》

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Template constraint
#@TPL_CONSTRAINT《
  ►#?AST_STRUCTURE_EXPECTED!【if #error(EXP_OPEN_PARENS)【】● MissingParenthesesExpression】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@NO_EXP)● #@NO_EXP】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@EXP_ANY)● #@EXP_ANY 】●
  ►#?AST_STRUCTURE_EXPECTED!【if(#@EXP_ANY__LITE #error(EXP_CLOSE_PARENS) #@BREAK● #@EXP_ANY__LITE #@BREAK】●
¤》
