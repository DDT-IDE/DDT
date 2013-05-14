▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(true)
#STRUCTURE_EXPECTED: ExpMixinString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpMixinString(MissingParenthesesExpression)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(#@ASSIGNEXP_ANY #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpMixinString( #@ASSIGNEXP_ANY )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(true)
#STRUCTURE_EXPECTED: ExpImportString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpImportString(MissingParenthesesExpression)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(#@ASSIGNEXP_ANY #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpImportString( #@ASSIGNEXP_ANY )
