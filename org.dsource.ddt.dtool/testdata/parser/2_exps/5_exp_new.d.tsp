▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) new Foo(123, foo)
#STRUCTURE_EXPECTED:
ExpNew(RefIdentifier Integer #@ExpIdentifier)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@ALLOC_ARG《
  ►#?STRUCTURE_EXPECTED!【 /*No alloc args*/● 】● 
  ►#?STRUCTURE_EXPECTED!【( #@EXPARGS_ESEP ) ● #@EXPARGS_ESEP】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     #@ALLOC_ARG  #@TYPEREF_NBNE  (sample)
#STRUCTURE_EXPECTED: ExpNew( #@ALLOC_ARG  #@TYPEREF_NBNE  #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (note that ARG1 expansion gets messed up because of EXP_ASSIGN__NO_PENDING)
#PARSE(EXPRESSION)   new     (#@EXPARGS_ESEP__SAMPLE #error(EXP_CLOSE_PARENS)  #@BREAK
#STRUCTURE_EXPECTED: ExpNew(  #@EXPARGS_ESEP__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   new     (#@EXPARGS_ESEP) #@NO_TYPEREF #@BREAK (123) 
#STRUCTURE_EXPECTED: ExpNew(  #@EXPARGS_ESEP  #@NO_TYPEREF )  
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CTOR_ARG《
  ►#?STRUCTURE_EXPECTED!【 /*No ctor args*/● 】● 
  ►#?STRUCTURE_EXPECTED!【( #@EXPARGS_ESEP ) ● #@EXPARGS_ESEP】●
  ►#?STRUCTURE_EXPECTED!【( #@EXPARGS_ESEP__SAMPLE #@NO_ClosePARENS #@BREAK ● #@EXPARGS_ESEP__SAMPLE #@BREAK】●
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
