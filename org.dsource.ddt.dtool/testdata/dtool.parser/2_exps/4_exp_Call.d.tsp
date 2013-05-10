▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases 
#PARSE(EXPRESSION)   foo(123)
#STRUCTURE_EXPECTED: ExpCall( #@ExpIdentifier Integer)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   foo(123 , bar)
#STRUCTURE_EXPECTED: ExpCall( #@ExpIdentifier Integer #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【foo ● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【foo() ● ExpCall(#@ExpIdentifier)】 ●
  ►#?STRUCTURE_EXPECTED!【foo[123 .. 44] ● ExpSlice(#@ExpIdentifier Integer Integer) 】●
  ►#?STRUCTURE_EXPECTED!【foo[123, 44] ● ExpIndex(#@ExpIdentifier Integer Integer) 】●
¤》

#@ARG_OR_NO《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE , ● #@EXP_ASSIGN__LITE】● 
  ►#?STRUCTURE_EXPECTED!【#@NO_EXP , ● #@NO_EXP】● 
  ►#?STRUCTURE_EXPECTED!【/* NO EXTRA ARG */ ● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@INDEXEE ( #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpCall( #@INDEXEE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@INDEXEE (#@EXP_ASSIGN #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpCall( #@INDEXEE #@EXP_ASSIGN )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@INDEXEE        ( #@ARG_OR_NO #@ASSIGNEXP_NB , #@ASSIGNEXP_ANY #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpCall( #@INDEXEE #@ARG_OR_NO #@ASSIGNEXP_NB   #@ASSIGNEXP_ANY )

