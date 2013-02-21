Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@EXP_OR_NO《#@EXP_ASSIGN●#@NO_EXP》

#@INDEXEE《
  ►#?AST_STRUCTURE_EXPECTED!【foo ● #@ExpIdentifier】● 
  ►#?AST_STRUCTURE_EXPECTED!【foo() ● ExpCall(#@ExpIdentifier)】 ●
  ►#?AST_STRUCTURE_EXPECTED!【foo[123 .. 44] ● ExpSlice(#@ExpIdentifier Integer Integer) 】●
  ►#?AST_STRUCTURE_EXPECTED!【foo[123, 44] ● ExpIndex(#@ExpIdentifier Integer Integer) 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo[]
#AST_STRUCTURE_EXPECTED:
ExpSlice( #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo[ #error(EXP_CLOSE_BRACKET)
#AST_STRUCTURE_EXPECTED:
ExpSlice( #@ExpIdentifier  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) foo[123]
#AST_STRUCTURE_EXPECTED:
ExpIndex( #@ExpIdentifier Integer )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) this[123 , bar]
#AST_STRUCTURE_EXPECTED:
ExpIndex( ExpThis Integer #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@INDEXEE [ #@ARG_OR_NO #@EXP_OR_NO , #@EXP_OR_NO #@BkCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:
ExpIndex( #@INDEXEE #@ARG_OR_NO #@EXP_OR_NO #@EXP_OR_NO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@INDEXEE [#@EXP_ASSIGN #@BkCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:
ExpIndex( #@INDEXEE #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@INDEXEE [123 .. bar]
#AST_STRUCTURE_EXPECTED:
ExpSlice( #@INDEXEE Integer #@ExpIdentifier )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  TODO
#PARSE(EXPRESSION) #@INDEXEE [123 .. bar #error(EXP_CLOSE_BRACKET) #parser(IgnoreRest) , 123 .. bar]
#AST_STRUCTURE_EXPECTED:
ExpSlice( #@INDEXEE Integer #@ExpIdentifier )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@INDEXEE [#@EXP_OR_NO .. #@EXP_OR_NO #@BkCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:
ExpSlice( #@INDEXEE #@EXP_OR_NO #@EXP_OR_NO )

