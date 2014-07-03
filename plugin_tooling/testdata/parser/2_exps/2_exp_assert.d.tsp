▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(true)
#STRUCTURE_EXPECTED: ExpAssert(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert( #@ASSIGNEXP_ANY #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert(#@ASSIGNEXP_ANY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(    #@ASSIGNEXP_NB__LITE, #@ASSIGNEXP_ANY #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert( #@ASSIGNEXP_NB__LITE  #@ASSIGNEXP_ANY )
