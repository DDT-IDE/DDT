Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert #error(EXP_OPEN_PARENS)¤【()】 #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@ASSIGNEXP_ANY #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@ASSIGNEXP_ANY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@ASSIGNEXP_NB__LITE, #@ASSIGNEXP_ANY #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@ASSIGNEXP_NB__LITE #@ASSIGNEXP_ANY)
