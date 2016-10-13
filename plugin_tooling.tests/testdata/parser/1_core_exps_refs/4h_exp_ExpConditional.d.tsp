Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false"
#STRUCTURE_EXPECTED: ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false" ? 1 : 2
#STRUCTURE_EXPECTED: ExpConditional(Bool String ExpConditional(String Integer Integer))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? #@EXP_ANY : #@NO_EXP #@BREAK
#STRUCTURE_EXPECTED: ExpConditional(Bool #@EXP_ANY #@NO_EXP )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_OROR #?EXP_RULE_BROKEN【#:DISCARD_CASE】 ? #@EXP_ANY #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@EXP_ANY )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@EXP_OROR #?EXP_RULE_BROKEN【#:DISCARD_CASE】 ? #@NO_EXP #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@NO_EXP )
