▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases 
mixin("int foo");
mixin(123);
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(ExpLiteralString)
DeclarationMixinString(ExpLiteralInteger)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ (duplicate test case, here for clarity)
#PARSE(DECLARATION)       mixin #@NO_REF #error(EXP_SEMICOLON)
#AST_STRUCTURE_EXPECTED:  DeclarationMixin(#@NO_REF)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin ( #@NO_EXP #@PaCLOSE_OR_NO #@SEMICOLON_OR_NO
#AST_STRUCTURE_EXPECTED:  DeclarationMixinString(#@NO_EXP)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin ( #@EXP_ANY__LITE #@PaCLOSE_OR_NO #@SEMICOLON_OR_NO
#AST_STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_ANY__LITE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)       mixin(#@EXP_ANY);
#AST_STRUCTURE_EXPECTED:  DeclarationMixinString(#@EXP_ANY)
