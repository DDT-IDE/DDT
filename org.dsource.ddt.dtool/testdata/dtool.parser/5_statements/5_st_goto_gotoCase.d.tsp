Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@GOTO_ID《
  ►#?STRUCTURE_EXPECTED!【labelId●Symbol】●
  ►#?STRUCTURE_EXPECTED!【/*labelId*/ #error(EXP_ID) ●Symbol】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    goto #@GOTO_ID #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED: StatementGoto(#@GOTO_ID)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    goto default #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED: StatementGotoDefault()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    goto case #@EXP_Opt #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED: StatementGotoCase(#@EXP_Opt)
