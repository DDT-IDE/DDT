Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ExpIdentifier【ExpReference(RefIdentifier)】

      #@BREAK《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) #RULE_BROKEN(test)● $ 】  》
#@IGNORE_REST《 #?STRUCTURE_EXPECTED!【#parser(IgnoreRest) ● $ 】  》
   #@CUT_REST《 #?STRUCTURE_EXPECTED!【#parser(CutRest) ● $ 】  》
       #@NULL《 #parser(IgnoreRest) #STRUCTURE_EXPECTED{} 》

 #@NO_ClosePARENS《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_PARENS)  /*No close parens */】 》
  #@NO_CloseBRACE《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_BRACE)  /*No close brace */】 》
#@NO_CloseBRACKET《#?STRUCTURE_EXPECTED!【#error(EXP_CLOSE_BRACKET)/*No close bracket */】 》
   #@NO_SEMICOLON《#?STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON) /*No semicolon */】 》

#@CLOSEPARENS_OR_NO【)●#error(EXP_CLOSE_PARENS) /* No ) */】
#@CLOSEPARENS_OR_CUT【#?STRUCTURE_EXPECTED!【)●】 ● #@NO_ClosePARENS #@CUT_REST #RULE_BROKEN(test)】

 #@CLOSEPARENS_OR_BREAK《#?STRUCTURE_EXPECTED!【)●】●#@NO_ClosePARENS #@BREAK》
  #@CLOSEBRACE_OR_BREAK《#?STRUCTURE_EXPECTED!【}●】●#@NO_CloseBRACE #@BREAK》
#@CLOSEBRACKET_OR_BREAK《#?STRUCTURE_EXPECTED!【]●】●#@NO_CloseBRACKET #@BREAK》
   #@SEMICOLON_OR_BREAK《#?STRUCTURE_EXPECTED!【;●】●#@NO_SEMICOLON#@BREAK》

#@ReqSemicolon《#?RULE_BROKEN【#error(-none-)¤【;】●;】》


#@NO_ID《#?STRUCTURE_EXPECTED!【/*MISSING:ID*/ #error(EXP_ID)●RefIdentifier】》
#@NO_DEFID《#?STRUCTURE_EXPECTED!【/*MISSING:DefID*/ #error(EXP_ID)●DefSymbol】》
#@DEFID《#?STRUCTURE_EXPECTED!【defId●DefSymbol】》



#@ARGS__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE, 123 + "foo"● #@EXP_ASSIGN__LITE ExpInfix(Integer String)】●
¤》
