▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(true)
#STRUCTURE_EXPECTED: ExpMixinString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpMixinString()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(#@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpMixinString( #@ASSIGNEXP_OrNO )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(true)
#STRUCTURE_EXPECTED: ExpImportString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpImportString()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(#@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpImportString( #@ASSIGNEXP_OrNO )
