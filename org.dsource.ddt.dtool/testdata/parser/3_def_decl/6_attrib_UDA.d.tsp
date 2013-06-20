Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Note: most of pragma testing is done in base declAttrib file

#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【@foo ● AttribCustom(RefIdentifier 】●
  ►#?STRUCTURE_EXPECTED!【@ #NOREF(flag)● AttribCustom( 】● 
¤》
#@INDEXEE__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【@foo ● AttribCustom(RefIdentifier 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE #?NOREF【#error(EXPRULE_IdOrExpArgList)¤【】●#error(EXP_OPEN_PARENS)¤【】】  :
#STRUCTURE_EXPECTED: 
   DeclarationAttrib( #@INDEXEE) DeclList)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE (#@ASSIGNEXP_NBNE__LITE #@CLOSEPARENS_OR_BREAK :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib( #@INDEXEE  #@ASSIGNEXP_NBNE__LITE #@CLOSEPARENS_OR_BREAK) DeclList)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE__SAMPLE (#@EXPARGS_ESEP_ParensClose :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib( #@INDEXEE__SAMPLE  #@EXPARGS_ESEP_ParensClose ) DeclList)