▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic cases
int xx;
Bar.foo foo = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefPrimitive DefSymbol)
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol InitializerExp(?))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@TYPE_REFS__NO_BROKEN fooA;
#@TYPE_REFS__NO_BROKEN fooC = 1, foo2    , foo3 = 3;

#STRUCTURE_EXPECTED:
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol)
DefVariable(#@TYPE_REFS__NO_BROKEN DefSymbol InitializerExp(?) DefVarFragment(DefSymbol) DefVarFragment(DefSymbol InitializerExp(?)))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test array initializer

#@BRACKET_END《#@《●,》 #@CLOSEBRACKET_OR_BREAK》
#@ARRAY_INIT_PREFIX《
  ►#?STRUCTURE_EXPECTED!【#@INIT_NONVOID__SAMPLE, ●ArrayInitEntry(#@INIT_NONVOID__SAMPLE)】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN_ORNO__LITE : #@INIT_NONVOID__SAMPLE, ●ArrayInitEntry(#@EXP_ASSIGN_ORNO__LITE #@INIT_NONVOID__SAMPLE)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   [ #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  InitializerArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   [ #@NO_INIT ,]
#STRUCTURE_EXPECTED:  InitializerArray(ArrayInitEntry(#@NO_INIT))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   [ #@INIT_NONVOID  #@BRACKET_END
#STRUCTURE_EXPECTED:  InitializerArray(ArrayInitEntry(#@INIT_NONVOID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   [ #@ARRAY_INIT_PREFIX  #@INIT_NONVOID  #@BRACKET_END
#STRUCTURE_EXPECTED:  InitializerArray(#@ARRAY_INIT_PREFIX ArrayInitEntry(#@INIT_NONVOID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   [ #@ARRAY_INIT_PREFIX  #@EXP_ASSIGN_ORNO__LITE : #@INIT_NONVOID  #@BRACKET_END
#STRUCTURE_EXPECTED:  InitializerArray(#@ARRAY_INIT_PREFIX ArrayInitEntry(#@EXP_ASSIGN_ORNO__LITE #@INIT_NONVOID))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test struct initializer

#@BRACE_END《#@《●,》 #@CLOSEBRACE_OR_BREAK》
#@REFID_OR_NO《
  ►#?STRUCTURE_EXPECTED!【foo● RefIdentifier】●
  ►#?STRUCTURE_EXPECTED!【#@NO_ID● #@NO_ID】●
¤》
#@STRUCT_INIT_PREFIX《
  ►#?STRUCTURE_EXPECTED!【#@INIT_NONVOID__SAMPLE, ●StructInitEntry(#@INIT_NONVOID__SAMPLE)】●
  ►#?STRUCTURE_EXPECTED!【#@REFID_OR_NO : #@INIT_NONVOID__SAMPLE, ●StructInitEntry(#@REFID_OR_NO #@INIT_NONVOID__SAMPLE)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   {  #@CLOSEBRACE_OR_BREAK
#STRUCTURE_EXPECTED:  InitializerStruct()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   {  #@NO_INIT ,}
#STRUCTURE_EXPECTED:  InitializerStruct(StructInitEntry(#@NO_INIT))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   {  #@INIT_NONVOID   #@BRACE_END
#STRUCTURE_EXPECTED:  InitializerStruct(StructInitEntry(#@INIT_NONVOID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   { #@STRUCT_INIT_PREFIX  #@INIT_NONVOID  #@BRACE_END
#STRUCTURE_EXPECTED:  InitializerStruct(#@STRUCT_INIT_PREFIX StructInitEntry(#@INIT_NONVOID))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   { #@STRUCT_INIT_PREFIX  #@REFID_OR_NO : #@INIT_NONVOID  #@BRACE_END
#STRUCTURE_EXPECTED:  InitializerStruct(#@STRUCT_INIT_PREFIX StructInitEntry(#@REFID_OR_NO #@INIT_NONVOID))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Struct init vs Function literal
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂   
#PARSE(INITIALIZER)   {  #error(EXP_CLOSE_BRACE) #@BREAK
#STRUCTURE_EXPECTED:  InitializerExp(ExpFunctionLiteral(BlockStatement))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ TODO after parseStatements is done   
#PARSE(INITIALIZER)   {  foo : #error(EXP_CLOSE_BRACE) #@BREAK
#STRUCTURE_EXPECTED:  InitializerExp(ExpFunctionLiteral(BlockStatement))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

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
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPE_REFS__NO_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPE_REFS__NO_BROKEN #@NO_ID #@BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@TYPE_REFS__NO_BROKEN)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NB__LITE fooB = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NB__LITE DefSymbol #@INITIALIZERS)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)              #@TYPEREF_NB__LITE #@PREFIX fooB = #@INITIALIZERS #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: DefVariable(#@TYPEREF_NB__LITE #@PREFIX DefVarFragment(DefSymbol #@INITIALIZERS))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls (deprecated) TODO
C style decls
int foo*;
int[int] foo* = 2, foo2    , foo3 = 3;

#STRUCTURE_EXPECTED:
