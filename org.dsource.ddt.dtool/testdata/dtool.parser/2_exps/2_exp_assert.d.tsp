▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(true)
#STRUCTURE_EXPECTED: ExpAssert(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert( #@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert(#@EXP_ASSIGN_ORNO)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   assert(    #@EXP_ASSIGN_ORNO__LITE, #@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpAssert( #@EXP_ASSIGN_ORNO__LITE  #@EXP_ASSIGN_ORNO )
