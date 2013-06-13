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

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ special case for opCall
#PARSE(EXPRESSION)    typeof(foo) (123 )
#STRUCTURE_EXPECTED:  ExpCall( ExpReference(RefTypeof(#@ExpIdentifier)) Integer )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ special case for opCall
#PARSE(EXPRESSION)    const Bar (123 )
#STRUCTURE_EXPECTED:  ExpCall( ExpReference(RefTypeModifier(RefIdentifier)) Integer )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ special case for opCall
#PARSE(EXPRESSION)    #error(TYPE_AS_EXP_VALUE)【const int】 (123 )
#STRUCTURE_EXPECTED:  ExpCall( ExpReference(RefTypeModifier(RefPrimitive)) Integer )

