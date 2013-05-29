▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
Bar.foo foo = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(?))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPEREF_NBNE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_BROKEN /*NoMissingExp*/ #@BREAK #@ReqSemicolon fooB 
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPEREF_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE fooB #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol #@INITIALIZERS)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with fragment
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA, fooB = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol DefVarFragment(DefSymbol #@INITIALIZERS))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PREFIX《
  ►#?STRUCTURE_EXPECTED!【prefix,  ●DefSymbol 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  ●DefSymbol InitializerExp(Integer) 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  prf2 = 123,●DefSymbol InitializerExp(?) DefVarFragment(* InitializerExp(*)) 】●
  ►#?STRUCTURE_EXPECTED!【prefix, #@NO_ID, ●DefSymbol DefVarFragment(#@NO_DEFID) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)              #@TYPEREF_NBNE__LITE #@PREFIX fooB = #@INITIALIZERS__SAMPLE #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: DefVariable(#@TYPEREF_NBNE__LITE #@PREFIX DefVarFragment(DefSymbol #@INITIALIZERS__SAMPLE))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls (deprecated)
C style decls
The parser reads the c-style decl but doesn't actually store the c-style type info anywhere
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[] #@【 /**/ ● #error(EXP_SEMICOLON) #@BREAK * 】  ;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[10] = 2, foo2 #error(EXP_SEMICOLON) #@BREAK [10];
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefIndexing(CStyleRootRef Integer) InitializerExp(Integer) DefVarFragment(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[  /**/  #error(EXP_CLOSE_BRACKET) #@【 /**/ ● #error(EXP_SEMICOLON)#@BREAK = 2 】;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  auto foo[] #@NO_DEFID; // Make sure this is not confused with C-Style decl
#STRUCTURE_EXPECTED: DeclarationBasicAttrib( IncompleteDeclaration(*) )
