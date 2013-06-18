▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
Bar.foo foo = 2;
#STRUCTURE_EXPECTED:
DefVariable(RefQualified(RefIdentifier RefIdentifier) DefSymbol Integer)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@ATTRIBS_Opt #@TYPEREF_BROKEN__NoTypeAppend /*NoMissingExp*/ #@BREAK #@ReqSemicolon fooB 
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@ATTRIBS_Opt #@TYPEREF_BROKEN__NoTypeAppend)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@ATTRIBS_Opt #@TYPEREF_NBNE__NoSingleDefId #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclaration(#@ATTRIBS_Opt #TM_AMBIG_AS_RAW(flag) #@TYPEREF_NBNE) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)                 foo                           #@NO_ID #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  IncompleteDeclaration(RefIdentifier) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@ATTRIBS_Opt #@TYPEREF_NBNE fooB #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@ATTRIBS_Opt #TM_AMBIG_AS_RAW(flag) #@TYPEREF_NBNE DefSymbol)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA = #@INITIALIZERS_TESTER #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol #@INITIALIZERS_TESTER)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with fragment
#PARSE(DECLARATION)   #@TYPEREF_NBNE__LITE fooA, fooB = #@INITIALIZERS_TESTER #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED:  DefVariable(#@TYPEREF_NBNE__LITE DefSymbol DefVarFragment(DefSymbol #@INITIALIZERS_TESTER))
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@PREFIX《
  ►#?STRUCTURE_EXPECTED!【prefix,  ●DefSymbol 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  ●DefSymbol Integer 】●
  ►#?STRUCTURE_EXPECTED!【prefix = 123,  prf2 = 123,●DefSymbol Integer DefVarFragment(* Integer) 】●
  ►#?STRUCTURE_EXPECTED!【prefix, #@NO_ID, ●DefSymbol DefVarFragment(#@NO_DEFID) 】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)              #@TYPEREF_NBNE__LITE #@PREFIX fooB = #@INITIALIZERS__SAMPLE #@SEMICOLON_OR_BREAK
#STRUCTURE_EXPECTED: DefVariable(#@TYPEREF_NBNE__LITE #@PREFIX DefVarFragment(DefSymbol #@INITIALIZERS__SAMPLE))

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ C-style decls (deprecated)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[] #@【 /**/ ● #error(EXP_SEMICOLON) #@BREAK * 】  ;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[10] = 2, foo2 #error(EXP_SEMICOLON) #@BREAK [10];
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefIndexing(CStyleRootRef Integer) Integer DefVarFragment(?) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  int foo[  /**/  #error(EXP_CLOSE_BRACKET) #@【 /**/ ● #error(EXP_SEMICOLON)#@BREAK = 2 】;
#STRUCTURE_EXPECTED: DefVariable(RefPrimitive DefSymbol RefTypeDynArray(CStyleRootRef))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(DECLARATION)  auto foo[] #@NO_DEFID; // Make sure this is not confused with C-Style decl
#STRUCTURE_EXPECTED: IncompleteDeclaration(AttribBasic * )
