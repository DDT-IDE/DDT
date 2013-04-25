▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
auto foo = 2;
static bar = foo;
auto xa = 456, xb = 123;
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(Integer)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(#@ExpIdentifier)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(?) DefVarFragment(DefSymbol InitializerExp(?))) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test normal var definition is not broken
#@SIMPLE_ATTRIBS #@TYPE_REFS__NO_BROKEN fooC = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS ( DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test composite attribs dont make auto decl
extern(C) foo #error(EXP_IDENTIFIER) ;
align foo #error(EXP_IDENTIFIER) ;
pragma(msg) foo #error(EXP_IDENTIFIER) ;
#STRUCTURE_EXPECTED:
DeclarationLinkage( InvalidDeclaration(RefIdentifier) )
DeclarationAlign( InvalidDeclaration(RefIdentifier) )
DeclarationPragma(Symbol InvalidDeclaration(RefIdentifier) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SIMPLE_ATTRIBS fooB = #@EXP_ASSIGN__LITE;

#STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS ( DefAutoVariable(DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) )
                                        
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@AUTO_START《auto●static●__gshared /*TODO: @disable */》

#@AUTO_INIT《
  ►#?STRUCTURE_EXPECTED!【= #@INITIALIZERS ● #@INITIALIZERS】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ASSIGN)【】 ● 】●
¤》
#@AUTO_FRAG《
  ►#?STRUCTURE_EXPECTED!【#@NO_DEFID ● DefVarFragment(#@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【bar #@AUTO_INIT ● DefVarFragment(DefSymbol #@AUTO_INIT)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@AUTO_START foo #@AUTO_INIT #@SEMICOLON_OR_NO
#STRUCTURE_EXPECTED:  DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@AUTO_INIT ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , #@AUTO_FRAG #@SEMICOLON_OR_NO
#STRUCTURE_EXPECTED:  DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@INITIALIZERS__SAMPLE #@AUTO_FRAG ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , foo2 #error(EXP_ASSIGN)【】 , #@AUTO_FRAG ;
#STRUCTURE_EXPECTED:  DeclarationBasicAttrib(DefAutoVariable(? #@INITIALIZERS__SAMPLE DefVarFragment(DefSymbol) #@AUTO_FRAG ) )
