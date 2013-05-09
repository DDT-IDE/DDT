Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false"
#STRUCTURE_EXPECTED: ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? "true" : "false" ? 1 : 2
#STRUCTURE_EXPECTED: ExpConditional(Bool String ExpConditional(String Integer Integer))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   true ? #@EXP_OrNO : #@NO_EXP #@BREAK
#STRUCTURE_EXPECTED: ExpConditional(Bool #@EXP_OrNO #@NO_EXP )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@EXP_OrNO #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@EXP_OrNO )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@NO_EXP #error(EXP_COLON) #@BREAK
#STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@NO_EXP )
