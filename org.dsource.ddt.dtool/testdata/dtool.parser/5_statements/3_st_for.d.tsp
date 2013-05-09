Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

#@FOR_PARAMS《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_OPEN_PARENS)¤【(;)】 #@BREAK ● #@BREAK 】●
 
 ►#?STRUCTURE_EXPECTED!【 ( #@NO_ST #error(EXP_SEMICOLON) #@CLOSEPARENS_OR_CUT ● #@NO_ST #@CLOSEPARENS_OR_CUT】●
 ►#?STRUCTURE_EXPECTED!【 ( #@ST__BROKEN #@CUT_REST #error(-none-)【;)】 ● #@ST__BROKEN #@CUT_REST #@CLOSEPARENS_OR_CUT】●
 ►#?STRUCTURE_EXPECTED!【 ( #@ST__NB_NE #@EXP_Opt__LITE #@INCR #@CLOSEPARENS_OR_CUT ● 
                          #@ST__NB_NE #@EXP_Opt__LITE #@INCR #@CLOSEPARENS_OR_CUT】●
¤》
#@INCR《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_SEMICOLON) ● 】●
 ►#?STRUCTURE_EXPECTED!【 ; #@EXP_Opt__LITE ● #@EXP_Opt__LITE 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    for #@FOR_PARAMS  #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementFor( #@FOR_PARAMS  #@ST__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    for ( #@ST__SAMPLE #@EXP_ANY__LITE ; ix++ ) #@ST__ANY
#STRUCTURE_EXPECTED: StatementFor( #@ST__SAMPLE #@EXP_ANY__LITE ExpPostfixOp(*) #@ST__ANY )

