▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(true)
#STRUCTURE_EXPECTED: ExpAssert(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert( #@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert(#@ASSIGNEXP_OrNO)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(    #@ASSIGNEXP_OrNO__LITE, #@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert( #@ASSIGNEXP_OrNO__LITE  #@ASSIGNEXP_OrNO )
