▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
auto foo = 2;
static bar = foo;
auto xa = 456, xb = 123;
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(Integer)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(#@ExpIdentifier)) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(?) DefVarFragment(DefSymbol InitializerExp(?))) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test normal var definition is not broken
#@SIMPLE_ATTRIBS__SAMPLE #@TYPEREF_NBNE fooC = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED: #?TM_ATTRIB_AMBIG【#:DISCARD_CASE】
#@SIMPLE_ATTRIBS__SAMPLE DefVariable(#@TYPEREF_NBNE DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test composite attribs dont make auto decl
extern(C) foo #error(EXP_IDENTIFIER) ;
align foo #error(EXP_IDENTIFIER) ;
pragma(msg) foo #error(EXP_IDENTIFIER) ;
#STRUCTURE_EXPECTED:
DeclarationLinkage( IncompleteDeclaration(RefIdentifier) )
DeclarationAlign( IncompleteDeclaration(RefIdentifier) )
DeclarationPragma(Symbol IncompleteDeclaration(RefIdentifier) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@SIMPLE_ATTRIBS fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:
#@SIMPLE_ATTRIBS DefAutoVariable(DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
const shared fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(DeclarationBasicAttrib( DefAutoVariable(DefSymbol InitializerExp(#@EXP_ASSIGN__LITE) ) ))
                                        
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@AUTO_START《
  ►#?STRUCTURE_EXPECTED!【auto● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【static● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【const● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【@disable● DeclarationAtAttrib(Symbol 】●
¤》

#@AUTO_INIT《
  ►#?STRUCTURE_EXPECTED!【= #@INITIALIZERS ● #@INITIALIZERS】●
  ►#?STRUCTURE_EXPECTED!【#error(EXP_ASSIGN)【】 ● 】●
¤》
#@AUTO_FRAG《
  ►#?STRUCTURE_EXPECTED!【#@NO_DEFID ● DefVarFragment(#@NO_DEFID)】●
  ►#?STRUCTURE_EXPECTED!【bar #@AUTO_INIT ● DefVarFragment(DefSymbol #@AUTO_INIT)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@AUTO_START foo #@AUTO_INIT #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  #@AUTO_START DefAutoVariable(DefSymbol #@AUTO_INIT ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , #@AUTO_FRAG #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@INITIALIZERS__SAMPLE #@AUTO_FRAG ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , foo2 #error(EXP_ASSIGN)【】 , #@AUTO_FRAG ;
#STRUCTURE_EXPECTED:  DeclarationBasicAttrib(DefAutoVariable(? #@INITIALIZERS__SAMPLE DefVarFragment(DefSymbol) #@AUTO_FRAG ) )
