▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) new Foo(123, foo)
#STRUCTURE_EXPECTED:
ExpNew(RefIdentifier Integer #@ExpIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ARG1《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN , ● #@EXP_ASSIGN】● 
  ►#?STRUCTURE_EXPECTED!【#@NO_EXP , ● #@NO_EXP】●
  ► ●
¤》
#@ARGLIST《
  ►#?STRUCTURE_EXPECTED!【( #@ARG1 #@EXP_ASSIGN )● #@ARG1 #@EXP_ASSIGN 】● 
  ►#?STRUCTURE_EXPECTED!【( #@EXP_ASSIGN__LITE , #@NO_EXP )● #@EXP_ASSIGN__LITE #@NO_EXP】● 
  ►#?STRUCTURE_EXPECTED!【( #@NO_EXP , #@NO_EXP )● #@NO_EXP #@NO_EXP】● 
  
  ►#?STRUCTURE_EXPECTED!【( #@NO_EXP , #@EXP_ASSIGN__LITE , #@NO_EXP )● #@NO_EXP #@EXP_ASSIGN__LITE #@NO_EXP】● 
  ►#?STRUCTURE_EXPECTED!【()● 】●
¤》
#@ALLOC_ARG《#@ARGLIST●►#?STRUCTURE_EXPECTED!【 /*No alloc args*/● 】》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@ALLOC_ARG  #@TYPEREFS_NB  (sample)
#STRUCTURE_EXPECTED: ExpNew( #@ALLOC_ARG  #@TYPEREFS_NB  #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (note that ARG1 expansion gets messed up because of EXP_ASSIGN__NO_PENDING)
#PARSE(EXPRESSION)   new     (#@ARG1 #@EXP_ASSIGN__NO_PENDING #error(EXP_CLOSE_PARENS)  #@BREAK foo (456)
#STRUCTURE_EXPECTED: ExpNew( #@《*●#@NO_EXP● 》(ARG1) #@EXP_ASSIGN__NO_PENDING )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@ARGLIST  #@NO_REF  (123) 
#STRUCTURE_EXPECTED: ExpCall( ExpNew( #@ARGLIST  #@NO_REF ) Integer)  
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CTOR_ARG《
  ►#?STRUCTURE_EXPECTED!【 /*No ctor args*/● 】● 
  ►#@ARGLIST● 
  ►#?STRUCTURE_EXPECTED!【( #@NO_ClosePARENS #@BREAK ● 】●
  ►#?STRUCTURE_EXPECTED!【( #@EXP_ASSIGN__LITE , #@NO_EXP #@NO_ClosePARENS #@BREAK ● #@EXP_ASSIGN__LITE #@NO_EXP】● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new   ( #@EXP_ASSIGN__LITE ) #@TYPEREFS_NB  #@CTOR_ARG
#STRUCTURE_EXPECTED: ExpNew( #@EXP_ASSIGN__LITE   #@TYPEREFS_NB  #@CTOR_ARG )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new   ( #@EXP_ASSIGN__LITE ) #@TYPE_REFS__NO_BROKEN  [ #@EXP_ASSIGN__LITE ]
#STRUCTURE_EXPECTED: ExpNew( #@EXP_ASSIGN__LITE  RefIndexing( #@TYPE_REFS__NO_BROKEN #@EXP_ASSIGN__LITE )  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@NO_REF   [789]
#STRUCTURE_EXPECTED: ExpIndex(ExpNew( #@NO_REF ) Integer)  



