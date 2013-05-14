Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CONDITION《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_OPEN_PARENS)¤【】 ● MissingParenthesesExpression 】●
 ►#?STRUCTURE_EXPECTED!【 ( #@EXP_ANY__LITE #@NO_ClosePARENS #@BREAK ● #@EXP_ANY__LITE #@BREAK】●
 ►#?STRUCTURE_EXPECTED!【 ( #@EXP_ANY ) ● #@EXP_ANY 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    while #@CONDITION  #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementWhile( #@CONDITION  #@ST__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    while ( #@EXP_ANY__LITE ) #@ST__ANY
#STRUCTURE_EXPECTED: StatementWhile( #@EXP_ANY__LITE #@ST__ANY )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    do #@NO_STOrBLOCK #error(EXP_KW_WHILE)【】 #@BREAK
#STRUCTURE_EXPECTED: StatementDoWhile( #@NO_STOrBLOCK  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    do #@ST__BROKEN 
#STRUCTURE_EXPECTED: StatementDoWhile( #@ST__BROKEN  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    do #@ST__NB_NE  while ( #@EXP_ANY__LITE );
#STRUCTURE_EXPECTED: StatementDoWhile( #@ST__NB_NE  #@EXP_ANY__LITE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@DOWHILE_BODY《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_KW_WHILE)【】 #@BREAK #@ST__ANY ● #@BREAK #@ST__ANY 】●
 ►#?STRUCTURE_EXPECTED!【 while #@CONDITION #@ReqSemicolon● #@CONDITION 】●
 ►#?STRUCTURE_EXPECTED!【 while ( #@EXP_ANY__LITE ) #@SEMICOLON_OR_BREAK● #@EXP_ANY__LITE #@SEMICOLON_OR_BREAK】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    do #@ST__SAMPLE  #@DOWHILE_BODY
#STRUCTURE_EXPECTED: StatementDoWhile( #@ST__SAMPLE  #@DOWHILE_BODY )
