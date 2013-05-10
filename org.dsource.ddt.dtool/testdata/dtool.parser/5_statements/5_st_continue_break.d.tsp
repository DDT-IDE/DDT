Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ID_Opt《#?STRUCTURE_EXPECTED!【id●Symbol】●#?STRUCTURE_EXPECTED!【/*id opt*/●】》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    continue #@ID_Opt #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED: StatementContinue(#@ID_Opt)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    break #@ID_Opt #@SEMICOLON_OR_BREAK 
#STRUCTURE_EXPECTED: StatementBreak(#@ID_Opt)
