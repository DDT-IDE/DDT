▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin(true)
#STRUCTURE_EXPECTED:
ExpMixinString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin(#@EXP_ASSIGN)
#STRUCTURE_EXPECTED:
ExpMixinString( #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin( #@EXP1《#@EXP_ASSIGN●#error(EXPRULE_exp)》 #@PaCLOSE_OR_NO
#STRUCTURE_EXPECTED:
ExpMixinString( #@《#@EXP_ASSIGN●MissingExpression》(EXP1)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin #error(EXP_OPEN_PARENS)【】
#STRUCTURE_EXPECTED:
ExpMixinString()

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import(true)
#STRUCTURE_EXPECTED:
ExpImportString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import(#@EXP_ASSIGN)
#STRUCTURE_EXPECTED:
ExpImportString( #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import( #@EXP1《#@EXP_ASSIGN●#error(EXPRULE_exp)》  #@PaCLOSE_OR_NO
#STRUCTURE_EXPECTED:
ExpImportString( #@《#@EXP_ASSIGN●MissingExpression》(EXP1)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import #error(EXP_OPEN_PARENS)【】
#STRUCTURE_EXPECTED:
ExpImportString()