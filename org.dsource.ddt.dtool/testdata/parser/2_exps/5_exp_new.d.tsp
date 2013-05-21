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
#PARSE(EXPRESSION)   new     #@ALLOC_ARG  #@TYPEREF_NBNE  (sample)
#STRUCTURE_EXPECTED: ExpNew( #@ALLOC_ARG  #@TYPEREF_NBNE  #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (note that ARG1 expansion gets messed up because of EXP_ASSIGN__NO_PENDING)
#PARSE(EXPRESSION)   new     (#@ARG1 #@EXP_ASSIGN__NO_PENDING #error(EXP_CLOSE_PARENS)  #@BREAK foo (456)
#STRUCTURE_EXPECTED: ExpNew( #@《*●#@NO_EXP● 》(ARG1) #@EXP_ASSIGN__NO_PENDING )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@ARGLIST  #@NO_TYPEREF #@BREAK (123) 
#STRUCTURE_EXPECTED: ExpNew( #@ARGLIST  #@NO_TYPEREF )  
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CTOR_ARG《
  ►#?STRUCTURE_EXPECTED!【 /*No ctor args*/● 】● 
  ►#@ARGLIST● 
  ►#?STRUCTURE_EXPECTED!【( #@NO_ClosePARENS #@BREAK ● 】●
  ►#?STRUCTURE_EXPECTED!【( #@EXP_ASSIGN__LITE , #@NO_EXP #@NO_ClosePARENS #@BREAK ● #@EXP_ASSIGN__LITE #@NO_EXP】● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new   ( #@EXP_ASSIGN__LITE ) #@TYPEREF_NBNE  #@CTOR_ARG
#STRUCTURE_EXPECTED: ExpNew( #@EXP_ASSIGN__LITE   #@TYPEREF_NBNE  #@CTOR_ARG )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new   ( #@EXP_ASSIGN__LITE )            #@TYPEREFS__NOBROKEN_APPENDABLE [ #@EXP_ASSIGN__LITE ]
#STRUCTURE_EXPECTED: ExpNew( #@EXP_ASSIGN__LITE  RefIndexing(#@TYPEREFS__NOBROKEN_APPENDABLE #@EXP_ASSIGN__LITE )  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@NO_TYPEREF  #@BREAK [789]
#STRUCTURE_EXPECTED: ExpNew( #@NO_TYPEREF )  



