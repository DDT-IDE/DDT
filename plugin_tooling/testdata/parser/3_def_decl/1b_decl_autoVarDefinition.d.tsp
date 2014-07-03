▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
auto foo = 2;
#STRUCTURE_EXPECTED:
DeclarationAttrib(AttribBasic DefAutoVariable(DefSymbol Integer) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  #@ATTRIBS_AUTODecl fooB = #@EXP_ASSIGN__LITE;
#STRUCTURE_EXPECTED: DeclarationAttrib(#@ATTRIBS_AUTODecl DefAutoVariable(DefSymbol #@EXP_ASSIGN__LITE) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Test composite attribs dont make auto decl
#PARSE(DECLARATION)  #@COMPLEX_ATTRIBS foo #@NO_DEFID #@SEMICOLON_BREAK = #@EXP_ASSIGN__LITE; 
#STRUCTURE_EXPECTED: DeclarationAttrib(#@COMPLEX_ATTRIBS IncompleteDeclarator(RefIdentifier) )
                                        
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@AUTO_START《
  ►#?STRUCTURE_EXPECTED!【auto● DeclarationAttrib(AttribBasic 】●
  ►#?STRUCTURE_EXPECTED!【static● DeclarationAttrib(AttribBasic 】●
  ►#?STRUCTURE_EXPECTED!【const● DeclarationAttrib(AttribBasic 】●
  ►#?STRUCTURE_EXPECTED!【@disable● DeclarationAttrib(AttribAtKeyword(Symbol) 】●
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
#STRUCTURE_EXPECTED:  #@AUTO_START DefAutoVariable(DefSymbol #@AUTO_INIT) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , #@AUTO_FRAG #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DeclarationAttrib(AttribBasic DefAutoVariable(DefSymbol #@INITIALIZERS__SAMPLE #@AUTO_FRAG) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   auto foo = #@INITIALIZERS__SAMPLE , foo2 #error(EXP_ASSIGN)【】 , #@AUTO_FRAG ;
#STRUCTURE_EXPECTED:  DeclarationAttrib(AttribBasic DefAutoVariable(DefSymbol #@INITIALIZERS__SAMPLE DefVarFragment(DefSymbol) #@AUTO_FRAG) )
