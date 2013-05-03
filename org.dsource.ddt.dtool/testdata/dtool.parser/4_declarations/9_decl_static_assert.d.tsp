Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert #error(EXP_OPEN_PARENS)¤【()】 #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@EXP_ASSIGN_ORNO)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   static assert ( #@EXP_ASSIGN_ORNO__LITE, #@EXP_ASSIGN_ORNO #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationStaticAssert(#@EXP_ASSIGN_ORNO__LITE #@EXP_ASSIGN_ORNO)
