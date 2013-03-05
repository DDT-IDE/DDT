▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin(true)
#AST_STRUCTURE_EXPECTED:
ExpMixinString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin(#@EXP_ASSIGN)
#AST_STRUCTURE_EXPECTED:
ExpMixinString( #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin( #@EXP1《#@EXP_ASSIGN●#error(EXPRULE_exp)》 #@PaCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:
ExpMixinString( #@《#@EXP_ASSIGN●MissingExpression》(EXP1)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
mixin #error(EXP_OPEN_PARENS)
#AST_SOURCE_EXPECTED:
mixin
#AST_STRUCTURE_EXPECTED:
ExpMixinString()

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import(true)
#AST_STRUCTURE_EXPECTED:
ExpImportString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import(#@EXP_ASSIGN)
#AST_STRUCTURE_EXPECTED:
ExpImportString( #@EXP_ASSIGN )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import( #@EXP1《#@EXP_ASSIGN●#error(EXPRULE_exp)》  #@PaCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:
ExpImportString( #@《#@EXP_ASSIGN●MissingExpression》(EXP1)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
import #error(EXP_OPEN_PARENS)
#AST_SOURCE_EXPECTED:
import
#AST_STRUCTURE_EXPECTED:
ExpImportString()