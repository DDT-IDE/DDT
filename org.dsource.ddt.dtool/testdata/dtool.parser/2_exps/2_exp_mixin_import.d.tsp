▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(true)
#STRUCTURE_EXPECTED: ExpMixinString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpMixinString()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   mixin(#@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpMixinString( #@EXP_ASSIGN_ORNO )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(true)
#STRUCTURE_EXPECTED: ExpImportString(Bool)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpImportString()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   import(#@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED: ExpImportString( #@EXP_ASSIGN_ORNO )
