Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@CONDITION《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_OPEN_PARENS)¤【】 ● MissingParenthesesExpression 】●
 ►#?STRUCTURE_EXPECTED!【 ( #@EXP_ANY__LITE #@NO_ClosePARENS #@BREAK ● #@EXP_ANY__LITE #@BREAK】●
 ►#?STRUCTURE_EXPECTED!【 ( #@EXP_ANY ) ● #@EXP_ANY 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    switch #@CONDITION  #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementSwitch( #@CONDITION  #@ST__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    #@《final●》 switch ( #@EXP_ANY__LITE ) #@ST__ANY
#STRUCTURE_EXPECTED: StatementSwitch( #@EXP_ANY__LITE #@ST__ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    switch ( 123 ) { default: }
#STRUCTURE_EXPECTED: StatementSwitch( Integer BlockStatement(StatementDefault(ScopedStatementList)) )