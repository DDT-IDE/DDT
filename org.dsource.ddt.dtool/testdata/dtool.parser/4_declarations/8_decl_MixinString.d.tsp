▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases 
mixin("int foo");
mixin(123);
#STRUCTURE_EXPECTED:
DeclarationMixinString(ExpLiteralString)
DeclarationMixinString(ExpLiteralInteger)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (duplicate test case, here for clarity)
#PARSE(DECLARATION)   mixin #@NO_TYPEREF #error(EXP_SEMICOLON) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_TYPEREF)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   mixin ( #@EXP_OrNO__LITE #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_OrNO__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   mixin(#@EXP_OrNO);
#STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_OrNO)
