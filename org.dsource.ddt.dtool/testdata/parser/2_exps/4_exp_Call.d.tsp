▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases 
#PARSE(EXPRESSION)   foo(123)
#STRUCTURE_EXPECTED: ExpCall( #@ExpIdentifier Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   foo(123 , bar)
#STRUCTURE_EXPECTED: ExpCall( #@ExpIdentifier Integer #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【foo ● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【foo(123) ● ExpCall(#@ExpIdentifier Integer)】 ●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)            #@EXP_POSTFIX (#@ASSIGNEXP_NBNE #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpCall( #@EXP_POSTFIX  #@ASSIGNEXP_NBNE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)            #@INDEXEE (#@EXPARGS_ESEP #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpCall( #@INDEXEE  #@EXPARGS_ESEP )

