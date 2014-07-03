Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors
#@NO_INIT《#?STRUCTURE_EXPECTED!【/*MISSING:Init*/ #error(EXPRULE_Initializer)● MissingExpression】》


#@INITIALIZERS__SAMPLE《
  ►#@NO_INIT●
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE ●#@EXP_ASSIGN__LITE】●
  ►#?STRUCTURE_EXPECTED!【void●InitializerVoid】●
¤》

#@INITIALIZERS_TESTER《
  ►#@INITIALIZERS__SAMPLE●
  ►#@_ARRAY_INIT__SPECIAL_CASES●
  ►#@_STRUCT_INIT__SPECIAL_CASES●
¤》


#@_ARRAY_INIT__SPECIAL_CASES《
  ►#?STRUCTURE_EXPECTED!【[ [123] : foo,]● 
    InitializerArray( ArrayInitEntry(InitializerArray(ArrayInitEntry(Integer)) #@ExpIdentifier) )】●
    
  ►#?STRUCTURE_EXPECTED!【[ 7,  4 : foo,]● 
    InitializerArray( ArrayInitEntry(Integer) ArrayInitEntry(Integer #@ExpIdentifier) )】●

  ►#?STRUCTURE_EXPECTED!【#error(INIT_USED_IN_EXP)《[ #@_ENTRIES_UNLIKE_ARRAY_LITERAL ]》 ~ [42]●ExpInfix(
    InitializerArray( #@_ENTRIES_UNLIKE_ARRAY_LITERAL )
    ExpLiteralArray(Integer)
  )】●
  ►#?STRUCTURE_EXPECTED!【[ #@INDEX_OPT "bar", #@INDEX_OPT [foo:foo] , #@INDEX_OPT [a,b] ] ~ [42]●ExpInfix(
    InitializerArray( ArrayInitEntry(#@INDEX_OPT *) ArrayInitEntry(#@INDEX_OPT *) * )
    ExpLiteralArray(Integer)
    )】●
¤》
#@INDEX_OPT《 ► ● ►#?STRUCTURE_EXPECTED!【123 : ● Integer】 ●¤》  // Note this has to be Integer to properly test

#@_ENTRIES_UNLIKE_ARRAY_LITERAL《
  ►#@_ENTRIES_UNLIKE_ARRAY_LITERAL__BASE●
  ►#?STRUCTURE_EXPECTED!【 [#@_ENTRIES_UNLIKE_ARRAY_LITERAL__BASE]● ArrayInitEntry(InitializerArray(#@_ENTRIES_UNLIKE_ARRAY_LITERAL__BASE))】●
¤》
#@_ENTRIES_UNLIKE_ARRAY_LITERAL__BASE《
  ►#?STRUCTURE_EXPECTED!【555, 4 : "foo" ● ArrayInitEntry(*) ArrayInitEntry(Integer String)】●
  ►#?STRUCTURE_EXPECTED!【4 : "foo" , 555● ArrayInitEntry(Integer String) ArrayInitEntry(*)】●
  ►#?STRUCTURE_EXPECTED!【#@INDEX_OPT { field : init }● ArrayInitEntry(#@INDEX_OPT InitializerStruct(*))】●
¤》



#@_STRUCT_INIT__SPECIAL_CASES《
  ►#?STRUCTURE_EXPECTED!【{ foo : bar, bar } ●InitializerStruct(
    StructInitEntry(RefIdentifier #@ExpIdentifier) StructInitEntry(#@ExpIdentifier)
  )】●
  ►#?STRUCTURE_EXPECTED!【{ foo : bar, bar; } /* Literal that looks like struct init */ ●
  ExpFunctionLiteral(BlockStatement( StatementLabel(?) StatementExpression(ExpInfix(* *)) ))】●
¤》
