Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false"
#STRUCTURE_EXPECTED: ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false" ? 1 : 2
#STRUCTURE_EXPECTED: ExpConditional(Bool String ExpConditional(String Integer Integer))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? #@EXP_OrNO #?MISSING_BRACKET【#:DISCARD_CASE】 : #@NO_EXP #@BREAK
#STRUCTURE_EXPECTED: ExpConditional(Bool #@EXP_OrNO #@NO_EXP )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_OROR #?EXP_RULE_BROKEN【#:DISCARD_CASE】 ? #@EXP_OrNO #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@EXP_OrNO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_OROR #?EXP_RULE_BROKEN【#:DISCARD_CASE】 ? #@NO_EXP #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@NO_EXP )
