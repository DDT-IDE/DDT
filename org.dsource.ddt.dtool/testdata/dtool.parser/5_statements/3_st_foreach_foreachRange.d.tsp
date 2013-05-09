Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FOREACH_KEYWD《foreach●foreach_reverse》

#@REF_OPT《 ►#?STRUCTURE_EXPECTED!【ref● 】 ● 》

#@SIMPLE_VAR_PAT《
 ►#?STRUCTURE_EXPECTED!【#@DEFID_OrNO ● #@DEFID_OrNO 】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NB #@DEFID ● #@TYPEREF_NB #@DEFID】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_NB #?TYPEREF_CanParseAsId{#:DISCARD_CASE} #@NO_DEFID ● #@TYPEREF_NB #@NO_DEFID】●
 ►#?STRUCTURE_EXPECTED!【#@TYPEREF_BROKEN ● #@TYPEREF_BROKEN #@NO_DEFID】●
¤》
#@FOREACH_SINGLE_VAR《#@REF_OPT #NO_BROKEN_PARENS(flag) #@SIMPLE_VAR_PAT》
#@FOREACH_VARS《
 ►#?STRUCTURE_EXPECTED!【#@FOREACH_SINGLE_VAR● ForeachVariableDef(#@FOREACH_SINGLE_VAR)】●
 ►#?STRUCTURE_EXPECTED!【#@REF_OPT defId, #@FOREACH_SINGLE_VAR● ForeachVariableDef(?) ForeachVariableDef(#@FOREACH_SINGLE_VAR)】●
 ►#?STRUCTURE_EXPECTED!【defA, defB, #@REF_OPT #@DEFID_OrNO● * ForeachVariableDef(?) ForeachVariableDef(#@DEFID_OrNO)】●
¤》

#@ITERABLE__SAMPLE《
 ►#?STRUCTURE_EXPECTED!【 #error(EXP_SEMICOLON) ● 】●
 ►#?STRUCTURE_EXPECTED!【 ; #@EXP_ANY_ORNO__LITE ● #@EXP_ANY_ORNO__LITE 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    foreach #error(EXP_OPEN_PARENS)¤【(;)】 #@BREAK  #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementForeach( #@BREAK  #@ST__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    foreach         ( #@FOREACH_VARS #@ITERABLE__SAMPLE #@CLOSEPARENS_OR_CUT #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementForeach( #@FOREACH_VARS #@ITERABLE__SAMPLE #@CLOSEPARENS_OR_CUT #@ST__SAMPLE)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ITERABLE《
 ►#?STRUCTURE_EXPECTED!【#error(EXP_SEMICOLON) ● 】●
 ►#?STRUCTURE_EXPECTED!【; #@EXP_ANY_ORNO ● #@EXP_ANY_ORNO 】●
 ►#?STRUCTURE_EXPECTED!【; #@EXP_ANY_ORNO__LITE .. #@EXP_ANY_ORNO●ForeachRangeExpression(#@EXP_ANY_ORNO__LITE #@EXP_ANY_ORNO)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    foreach ( foo #@ITERABLE ) #@ST__SAMPLE
#STRUCTURE_EXPECTED: StatementForeach( ForeachVariableDef(DefSymbol) #@ITERABLE #@ST__SAMPLE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(STATEMENT)    #@FOREACH_KEYWD ( foo ; #@EXP_ANY__LITE ) #@ST__ANY
#STRUCTURE_EXPECTED: StatementForeach( ForeachVariableDef(DefSymbol) #@EXP_ANY__LITE #@ST__ANY )

