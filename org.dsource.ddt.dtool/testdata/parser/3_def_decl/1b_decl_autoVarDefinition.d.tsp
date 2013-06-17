▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
auto foo = 2;
static bar = foo;
auto xa = 456, xb = 123;
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib( DefAutoVariable(DefSymbol Integer) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@ExpIdentifier) )
DeclarationBasicAttrib( DefAutoVariable(DefSymbol Integer DefVarFragment(DefSymbol Integer)) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test normal var definition is not broken
#@ATTRIBS_AUTODecl__SAMPLE #@TYPEREF_NBNE fooC = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED: #?TM_ATTRIB_AMBIG【#:DISCARD_CASE】 /* TODO include attris in defVariable*/
#@ATTRIBS_AUTODecl__SAMPLE DefVariable(#@TYPEREF_NBNE DefSymbol #@EXP_ASSIGN__LITE ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test composite attribs dont make auto decl
#@COMPLEX_ATTRIBS foo #error(EXP_IDENTIFIER) ;
#STRUCTURE_EXPECTED:
#@COMPLEX_ATTRIBS IncompleteDeclaration(RefIdentifier) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ATTRIBS_AUTODecl fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:
#@ATTRIBS_AUTODecl DefAutoVariable(DefSymbol #@EXP_ASSIGN__LITE ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
const shared fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED:
DeclarationBasicAttrib(DeclarationBasicAttrib( DefAutoVariable(DefSymbol #@EXP_ASSIGN__LITE ) ))
                                        
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@AUTO_START《
  ►#?STRUCTURE_EXPECTED!【auto● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【static● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【const● DeclarationBasicAttrib( 】●
  ►#?STRUCTURE_EXPECTED!【@disable● DeclarationAtAttrib(Symbol 】●
¤》

#@AUTO_INIT《
  ►#?STRUCTURE_EXPECTED!【= #@INITIALIZERS_TESTER ● #@INITIALIZERS_TESTER】●
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
