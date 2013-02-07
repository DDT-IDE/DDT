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
#PARSE(EXPRESSION) #comment(NO_STDOUT)
true ? #@EXP_ANY : #error(EXPRULE_exp)
#AST_STRUCTURE_EXPECTED:
ExpConditional(Bool #@EXP_ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #comment(NO_STDOUT)
true ? #@EXP_ANY #error(EXP_COLON)
#AST_SOURCE_EXPECTED:
true ? #@EXP_ANY :
#AST_STRUCTURE_EXPECTED:
ExpConditional(Bool #@EXP_ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #comment(NO_STDOUT)
#@EXP_OROR ? #error(EXPRULE_exp) #error(EXP_COLON)
#AST_SOURCE_EXPECTED:
#@EXP_OROR ? :
#AST_STRUCTURE_EXPECTED:
ExpConditional( #@EXP_OROR )

