▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) new Foo(123, foo)
#AST_STRUCTURE_EXPECTED:
ExpNew(RefIdentifier Integer #@ExpIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@EXP_OR_NO《#@EXP_ASSIGN●#@NO_EXP》

#@ARG1《
  ►#?AST_STRUCTURE_EXPECTED!【#@EXP_ASSIGN , ● #@EXP_ASSIGN】● 
  ►#?AST_STRUCTURE_EXPECTED!【#@NO_EXP , ● #@NO_EXP】●
  ► ●
¤》
#@ARGLIST《
  ►#?AST_STRUCTURE_EXPECTED!【( #@ARG1 #@EXP_ASSIGN )● #@ARG1 #@EXP_ASSIGN 】● 
  ►#?AST_STRUCTURE_EXPECTED!【( #@EXP_ASSIGN__LITE , #@NO_EXP )● #@EXP_ASSIGN__LITE #@NO_EXP】● 
  ►#?AST_STRUCTURE_EXPECTED!【( #@NO_EXP , #@NO_EXP )● #@NO_EXP #@NO_EXP】● 
  ►#?AST_STRUCTURE_EXPECTED!【( #@NO_EXP , #@EXP_ASSIGN__LITE , #@NO_EXP )● #@NO_EXP #@EXP_ASSIGN__LITE #@NO_EXP】● 
  ►#?AST_STRUCTURE_EXPECTED!【()● 】●
¤》
#@ALLOC_ARG《#@ARGLIST●►#?AST_STRUCTURE_EXPECTED!【 /*No alloc args*/● 】》
#@CTOR_ARG《#@ARGLIST●►#?AST_STRUCTURE_EXPECTED!【 /*No ctor args*/● 】》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       new     #@ALLOC_ARG  #@TYPE_REFS  (sample)
#AST_STRUCTURE_EXPECTED: ExpNew( #@ALLOC_ARG  #@TYPE_REFS  #@ExpIdentifier )
#comment(NO_STDOUT)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       new     ( #@EXP_ASSIGN__LITE )  #@TYPE_REFS  #@CTOR_ARG
#AST_STRUCTURE_EXPECTED: ExpNew(  #@EXP_ASSIGN__LITE  #@TYPE_REFS  #@CTOR_ARG )
#comment(NO_STDOUT)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       new     ( #@EXP_ASSIGN__LITE )  #@TYPE_REFS  [ #@EXP_ASSIGN__LITE ]
#AST_STRUCTURE_EXPECTED: ExpNew(  #@EXP_ASSIGN__LITE  RefIndexing( #@TYPE_REFS #@EXP_ASSIGN__LITE )  )
#comment(NO_STDOUT)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Error cases
#PARSE(EXPRESSION)        new     #@ARGLIST  #@MISSING_REF  (123) 
#AST_STRUCTURE_EXPECTED:  ExpCall( ExpNew( #@ARGLIST  #@MISSING_REF ) Integer)  
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        new  (#@ARG1 #@EXP_ASSIGN #error(EXP_CLOSE_PARENS)  #parser(IgnoreRest) foo (456)
#AST_STRUCTURE_EXPECTED:  ExpNew( #@ARG1 #@EXP_ASSIGN )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        new     #@MISSING_REF   [789]
#AST_STRUCTURE_EXPECTED:  ExpIndex(ExpNew( #@MISSING_REF ) Integer)  
