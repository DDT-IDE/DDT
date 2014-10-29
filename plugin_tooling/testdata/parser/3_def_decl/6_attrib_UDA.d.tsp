Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Note: most of pragma testing is done in base declAttrib file

#@INDEXEE__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【@foo ! #@TPL_ARGLIST__SAMPLE ● RefTemplateInstance(RefIdentifier #@TPL_ARGLIST__SAMPLE ) 】● 
  ►#?STRUCTURE_EXPECTED!【@foo ● RefIdentifier 】●
¤》
#@INDEXEE《
  ►#@INDEXEE__SAMPLE●
  ►#?STRUCTURE_EXPECTED!【@ #NOREF(flag)● 】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE #?NOREF【#error(EXPRULE_IdOrExpArgList)¤【】● 】  :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib(AttribCustom( #@INDEXEE ) DeclList)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE (#@ASSIGNEXP_NBNE__LITE #@CLOSEPARENS_OR_BREAK :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib(AttribCustom( #@INDEXEE  #@ASSIGNEXP_NBNE__LITE #@CLOSEPARENS_OR_BREAK) DeclList)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@INDEXEE__SAMPLE (#@EXPARGS_ESEP_ParensClose :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib(AttribCustom( #@INDEXEE__SAMPLE  #@EXPARGS_ESEP_ParensClose ) DeclList)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  @foo ! #error(EXPRULE_TplSingleArg) :
#STRUCTURE_EXPECTED: 
  DeclarationAttrib(AttribCustom(  RefTemplateInstance(RefIdentifier #@NO_EXP )  ) DeclList )
  