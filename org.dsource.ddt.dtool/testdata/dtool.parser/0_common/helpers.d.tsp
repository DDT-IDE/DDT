Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ExpIdentifier【ExpReference(RefIdentifier)】

#@PaCLOSE_OR_NO【)●#error(EXP_CLOSE_PARENS) /* No ) */】
#@BkCLOSE_OR_NO【]●#error(EXP_CLOSE_BRACKET) /* No ] */】
#@BrCLOSE_OR_NO【}●#error(EXP_CLOSE_BRACE) /* No } */】
#@SEMICOLON_OR_NO【;●#error(EXP_SEMICOLON) /* No ; */】

 #@NO_ClosePARENS《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS)  /*No close brace */】 》
  #@NO_CloseBRACE《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_BRACE)  /*No close bracket*/】 》
#@NO_CloseBRACKET《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_BRACKET)/*No close parens */】 》
   #@NO_SEMICOLON《#?STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON) /*No semicolon */】 》

#@PaCLOSE_OR_BREAK《#?STRUCTURE_EXPECTED!【)●】●#@NO_ClosePARENS #@BREAK》
#@BrCLOSE_OR_BREAK《#?STRUCTURE_EXPECTED!【}●】●#@NO_CloseBRACE #@BREAK》
#@BkCLOSE_OR_BREAK《#?STRUCTURE_EXPECTED!【]●】●#@NO_CloseBRACKET #@BREAK》
#@SEMICOLON_OR_BREAK《#?STRUCTURE_EXPECTED!【;●】●#@NO_SEMICOLON#@BREAK》
#@PARENS_OR_BREAK《#@PaCLOSE_OR_BREAK》
#@BRACE_OR_BREAK《#@BrCLOSE_OR_BREAK》
#@BRACKET_OR_BREAK《#@BkCLOSE_OR_BREAK》

#@BREAK《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) #RULE_BROKEN(test)● $ 】  》
#@IGNORE_REST《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) ● $ 】  》

#@NO_ID《#?STRUCTURE_EXPECTED!【/*MISSING:ID*/ #error(EXP_ID)●RefIdentifier】》
#@NO_DEFID《#?STRUCTURE_EXPECTED!【/*MISSING:DefID*/ #error(EXP_ID)●DefSymbol】》
#@DEFID《#?STRUCTURE_EXPECTED!【defId●DefSymbol】》



#@ARGS__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE, 123 + "foo"● #@EXP_ASSIGN__LITE ExpInfix(Integer String)】●
¤》
