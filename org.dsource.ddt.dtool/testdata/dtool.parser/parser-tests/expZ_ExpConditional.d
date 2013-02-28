Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 
true ? "true" : "false"
#AST_STRUCTURE_EXPECTED:
ExpConditional(Bool String String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 
true ? "true" : "false" ? 1 : 2
#AST_STRUCTURE_EXPECTED:
ExpConditional(Bool String ExpConditional(String Integer Integer))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 
true ? #@EXP_ANY : #error(EXPRULE_exp)
#AST_STRUCTURE_EXPECTED:
ExpConditional(Bool #@EXP_ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@EXP_OROR ? #@EXP_ANY #error(EXP_COLON)
#AST_STRUCTURE_EXPECTED:
ExpConditional( #@EXP_OROR #@EXP_ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@EXP_OROR ? #error(EXPRULE_exp) #error(EXP_COLON)
#AST_STRUCTURE_EXPECTED:
ExpConditional( #@EXP_OROR )
