▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
Bar.foo foo = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(?))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE fooB #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPEREF_NBNE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_BROKEN /*NoMissingExp*/ #@BREAK #@ReqSemicolon fooB 
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPEREF_BROKEN)
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
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#STRUCTURE_EXPECTED:
