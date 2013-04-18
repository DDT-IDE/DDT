▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases

Foo foo;
int xx;
Bar.foo foo = 2;

#STRUCTURE_EXPECTED:
DefVariable(RefIdentifier DefSymbol)
DefVariable(RefPrimitive DefSymbol)
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(?))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS__NO_BROKEN fooA;
#@TYPE_REFS__NO_BROKEN fooB = 55 ;
#@TYPE_REFS__NO_BROKEN fooC = 1, foo2    , foo3 = 3;

#STRUCTURE_EXPECTED:
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol)
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(?))
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@INITIALIZERS《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN●InitializerExp(#@EXP_ASSIGN)】●
  ►#?STRUCTURE_EXPECTED!【void●InitializerVoid】●
  ►#@NO_INIT●
¤》
#@PREFIX《
  ►#?STRUCTURE_EXPECTED!【prefix,  ●DefSymbol 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  ●DefSymbol InitializerExp(Integer) 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  prf2 = 123,●DefSymbol InitializerExp(?) DefVarFragment(* InitializerExp(*)) 】●
  ►#?STRUCTURE_EXPECTED!【prefix, #@NO_ID, ●DefSymbol DefVarFragment(#@NO_DEFID) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPE_REFS__NO_BROKEN fooB #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPE_REFS__NO_BROKEN #@NO_ID ;
#STRUCTURE_EXPECTED:  InvalidDeclaration(#@TYPE_REFS__NO_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPE_REFS__NO_BROKEN #@NO_ID #@BREAK
#STRUCTURE_EXPECTED:  InvalidDeclaration(#@TYPE_REFS__NO_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREFS_NB__LITE fooB = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREFS_NB__LITE DefSymbol #@INITIALIZERS)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)              #@TYPEREFS_NB__LITE #@PREFIX fooB = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: DefVariable(#@TYPEREFS_NB__LITE #@PREFIX DefVarFragment(DefSymbol #@INITIALIZERS))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls
#comment(TODO):
C style decls
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#STRUCTURE_EXPECTED:

