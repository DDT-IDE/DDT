Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert #error(EXP_OPEN_PARENS)¤【()】 #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@ASSIGNEXP_OrNO)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@ASSIGNEXP_OrNO__LITE, #@ASSIGNEXP_OrNO #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@ASSIGNEXP_OrNO__LITE #@ASSIGNEXP_OrNO)
