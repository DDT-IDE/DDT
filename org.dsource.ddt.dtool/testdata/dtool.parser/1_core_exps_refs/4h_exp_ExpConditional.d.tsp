Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false"
#STRUCTURE_EXPECTED: ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false" ? 1 : 2
#STRUCTURE_EXPECTED: ExpConditional(Bool String ExpConditional(String Integer Integer))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? #@EXP_ANY_ORNO : #@NO_EXP #@BREAK
#STRUCTURE_EXPECTED: ExpConditional(Bool #@EXP_ANY_ORNO #@NO_EXP )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@EXP_ANY_ORNO #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@EXP_ANY_ORNO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@NO_EXP #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@NO_EXP )
