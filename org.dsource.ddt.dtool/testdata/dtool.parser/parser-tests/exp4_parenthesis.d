▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        (  #@CONTENT《#@EXPS__NO_REFS●#@NO_EXP》  #@PaCLOSE_OR_NO
#AST_STRUCTURE_EXPECTED:  ExpParentheses( #@CONTENT )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        (  #error(TYPE_AS_EXP_VALUE){#@REFS_UNAMBIG}  )
#AST_STRUCTURE_EXPECTED:  ExpParentheses( #@REFS_UNAMBIG )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        (  #@REFS_UNAMBIG  #error(EXP_CLOSE_PARENS)
#AST_STRUCTURE_EXPECTED:  ExpParentheses( #@REFS_UNAMBIG )