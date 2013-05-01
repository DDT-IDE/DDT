▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases 
mixin("int foo");
mixin(123);
#STRUCTURE_EXPECTED:
DeclarationMixinString(ExpLiteralString)
DeclarationMixinString(ExpLiteralInteger)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (duplicate test case, here for clarity)
#PARSE(DECLARATION)   mixin #@NO_REF #error(EXP_SEMICOLON) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_REF)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   mixin ( #@EXP_ANY_ORNO__LITE #@CLOSEPARENS_OR_NO #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_ANY_ORNO__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   mixin(#@EXP_ANY_ORNO);
#STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_ANY_ORNO)
