Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       true ? "true" : "false"
#AST_STRUCTURE_EXPECTED: ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       true ? "true" : "false" ? 1 : 2
#AST_STRUCTURE_EXPECTED: ExpConditional(Bool String ExpConditional(String Integer Integer))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       true ? #@EXP_ANY : #@NO_EXP #@BREAK
#AST_STRUCTURE_EXPECTED: ExpConditional(Bool #@EXP_ANY #@NO_EXP )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@EXP_ANY #error(EXP_COLON) #@BREAK
#AST_STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@EXP_ANY )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)       #EXP__NO_BREAKING_EXPS(flag) #@EXP_OROR ? #@NO_EXP #error(EXP_COLON) #@BREAK
#AST_STRUCTURE_EXPECTED: ExpConditional( #@EXP_OROR #@NO_EXP )
