▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin("int foo");
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin("");
mixin(123);
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(ExpLiteralString)
DeclarationMixinString(ExpLiteralInteger)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

mixin(#@SP_EXP);

#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(#@SPSE_EXP(SP_EXP))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin #error:EXP_OPEN_PARENS #error:EXP_SEMICOLON
mixin #error:EXP_OPEN_PARENS ;
mixin #error:EXP_OPEN_PARENS #error:EXP_SEMICOLON #error:SE_decl ) ;
#AST_SOURCE_EXPECTED:
mixin();
mixin();
mixin(); );
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString()
DeclarationMixinString()
DeclarationMixinString() InvalidSyntaxElement() DeclarationEmpty

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin ( #error:EXPRULE_exp );
mixin ( #error:EXPRULE_exp #error:EXP_CLOSE_PARENS ; import foo;
mixin ( #error:EXPRULE_exp ) #error:EXP_SEMICOLON
mixin ( #error:EXPRULE_exp #error:EXP_CLOSE_PARENS #error:EXP_SEMICOLON
#AST_SOURCE_EXPECTED:
mixin();
mixin(); import foo;
mixin();
mixin();

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin(#@SP_EXP #error:EXP_CLOSE_PARENS ;
mixin(#@SP_EXP #error:EXP_CLOSE_PARENS #error:EXP_SEMICOLON
mixin(#@SP_EXP ) #error:EXP_SEMICOLON
#AST_SOURCE_EXPECTED:
mixin(#@SP_EXP);
mixin(#@SP_EXP);
mixin(#@SP_EXP);

#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(#@SPSE_EXP(SP_EXP))
DeclarationMixinString(#@SPSE_EXP(SP_EXP))
DeclarationMixinString(#@SPSE_EXP(SP_EXP))