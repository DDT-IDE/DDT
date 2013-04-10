▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases

auto foo = 2;
static bar = foo;
auto xa = 456, xb = 123;

#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(Integer)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(#@ExpIdentifier)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(?) DefVarFragment(DefSymbol InitializerExp(?))) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SIMPLE_ATTRIBS fooB = #@EXP_ASSIGN;
#@SIMPLE_ATTRIBS fooC = #@EXP_ASSIGN, foo3 = #@EXP_ASSIGN;

#AST_STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS      ( DefAutoVariable(DefSymbol InitializerExp(#@EXP_ASSIGN) ) )
#@SIMPLE_ATTRIBS      ( DefAutoVariable(DefSymbol InitializerExp(#@EXP_ASSIGN) 
                                        DefVarFragment(DefSymbol InitializerExp(#@EXP_ASSIGN)) ) )
                                        
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test normal var definition is not broken
#@SIMPLE_ATTRIBS Foo fooB = #@EXP_ASSIGN__LITE;
#@SIMPLE_ATTRIBS Foo fooB;
#@SIMPLE_ATTRIBS Foo fooC = #@EXP_ASSIGN__LITE, fooC2 ;

#AST_STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS ( DefVariable(RefIdentifier DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) )
#@SIMPLE_ATTRIBS ( DefVariable(RefIdentifier DefSymbol ) )
#@SIMPLE_ATTRIBS ( DefVariable(RefIdentifier DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) DefVarFragment(DefSymbol) ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SIMPLE_ATTRIBS #@TYPE_REFS__NO_BROKEN fooC = #@EXP_ASSIGN__LITE, fooC2;

#AST_STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS ( DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) DefVarFragment(DefSymbol) ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test composite attribs dont make auto decl
extern(C) foo #error(EXP_IDENTIFIER) ;
align foo #error(EXP_IDENTIFIER) ;
pragma(msg) foo #error(EXP_IDENTIFIER) ;
#AST_STRUCTURE_EXPECTED:
DeclarationLinkage( InvalidDeclaration(RefIdentifier) )
DeclarationAlign( InvalidDeclaration(RefIdentifier) )
DeclarationPragma( InvalidDeclaration(RefIdentifier) )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors
#@AUTO_START《auto●static●__gshared /*@disable TODO*/》

#@AUTO_END《
  ►#?AST_STRUCTURE_EXPECTED!【= #@EXP_ASSIGN #@SEMICOLON_OR_NO ● InitializerExp(#@EXP_ASSIGN)】●
  ►#?AST_STRUCTURE_EXPECTED!【= #@NO_INIT #@SEMICOLON_OR_NO ● #@NO_INIT】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ASSIGN)【】 ; ● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@AUTO_START foo #@AUTO_END
#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@AUTO_END ) )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@AUTO_MID《
  ►#?AST_STRUCTURE_EXPECTED!【= #@EXP_ASSIGN ● InitializerExp(#@EXP_ASSIGN)】●
  ►#?AST_STRUCTURE_EXPECTED!【= #@NO_INIT ● #@NO_INIT】●
  ►#?AST_STRUCTURE_EXPECTED!【#error(EXP_ASSIGN)【】 ● 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
auto foo #@AUTO_MID , bar #@AUTO_END 
#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@AUTO_MID DefVarFragment(DefSymbol #@AUTO_END) ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
auto foo #@AUTO_MID , foo2 #@AUTO_MID , foo3 #@AUTO_END 
#AST_STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@AUTO_MID
  DefVarFragment(DefSymbol #@AUTO_MID) 
  DefVarFragment(DefSymbol #@AUTO_END) ) )
