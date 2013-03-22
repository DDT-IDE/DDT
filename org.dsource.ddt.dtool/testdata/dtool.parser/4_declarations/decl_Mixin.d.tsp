▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin("int foo");
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin("");
mixin(123);
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(ExpLiteralString)
DeclarationMixinString(ExpLiteralInteger)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

mixin(#@EXP_ANY);

#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(#@EXP_ANY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin #error:EXP_OPEN_PARENS¤【()】 #error:EXP_SEMICOLON
mixin #error:EXP_OPEN_PARENS¤【()】 ;
mixin #error:EXP_OPEN_PARENS¤【()】 #error:EXP_SEMICOLON #error:SE_decl ) ;
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString()
DeclarationMixinString()
DeclarationMixinString() InvalidSyntaxElement() DeclarationEmpty

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin ( #error:EXPRULE_exp );
mixin ( #error:EXPRULE_exp #error:EXP_CLOSE_PARENS ; import foo;
mixin ( #error:EXPRULE_exp ) #error:EXP_SEMICOLON
mixin ( #error:EXPRULE_exp #error:EXP_CLOSE_PARENS #error:EXP_SEMICOLON

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin(#@EXP_ASSIGN__NO_PENDING #error:EXP_CLOSE_PARENS ;
mixin(#@EXP_ASSIGN__NO_PENDING #error:EXP_CLOSE_PARENS #error:EXP_SEMICOLON
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(#@EXP_ASSIGN__NO_PENDING)
DeclarationMixinString(#@EXP_ASSIGN__NO_PENDING)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
mixin(#@EXP_ANY ) #error:EXP_SEMICOLON
#AST_STRUCTURE_EXPECTED:
DeclarationMixinString(#@EXP_ANY)