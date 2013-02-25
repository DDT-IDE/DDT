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
#@ARGEND《
  ►#?AST_STRUCTURE_EXPECTED!【, #@EXP_ASSIGN ● #@EXP_ASSIGN】● 
  ►#?AST_STRUCTURE_EXPECTED!【, #@NO_EXP ● #@NO_EXP】●
  ► ●
¤》
#@NEW_ARG《
  ►#?AST_STRUCTURE_EXPECTED!【( #@ARG1 #@EXP_ASSIGN #@ARGEND )● #@ARG1 #@EXP_ASSIGN #@ARGEND】● 
  ►#?AST_STRUCTURE_EXPECTED!【()● 】●
  ►#?AST_STRUCTURE_EXPECTED!【● 】●
¤》
#@END《
  ►#?AST_STRUCTURE_EXPECTED!【( #@ARG1 #@EXP_ASSIGN #@ARGEND )● #@ARG1 #@EXP_ASSIGN #@ARGEND】● 
  ►#?AST_STRUCTURE_EXPECTED!【() ● 】●
  ►#?AST_STRUCTURE_EXPECTED!【 /* Nothing after Type*/ ● 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       new     #@NEW_ARG  #@TYPE_REFS  #@END
#AST_STRUCTURE_EXPECTED: ExpNew( #@NEW_ARG  #@TYPE_REFS  #@END )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       new     #@NEW_ARG  #@TYPE_REFS  [ #@EXP_ASSIGN ]
#AST_STRUCTURE_EXPECTED: ExpNew( #@NEW_ARG  RefIndexing( #@TYPE_REFS #@EXP_ASSIGN )  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Error cases
// I'm not sure this is the best behavior though:
// Should a broken exp parsing break all other pending exp rule parsing? 
#PARSE(EXPRESSION)         new  ( #@ARG1 #@EXP_ASSIGN #@ARGEND )  #error(EXPRULE_ref)  (123) 
#AST_STRUCTURE_EXPECTED: 
ExpCall(                   ExpNew( #@ARG1 #@EXP_ASSIGN #@ARGEND  RefIdentifier )  Integer  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)         new  (#@ARG1 #@EXP_ASSIGN #error(EXP_CLOSE_PARENS)  #parser(IgnoreRest) foo (456)
#AST_STRUCTURE_EXPECTED: ExpNew( #@ARG1 #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)          new  #error(EXPRULE_ref)  [789]
#AST_STRUCTURE_EXPECTED: 
ExpIndex(                   ExpNew( RefIdentifier )  Integer  )
