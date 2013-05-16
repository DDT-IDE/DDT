Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Errors
#@NO_INIT《#?STRUCTURE_EXPECTED!【/*MISSING:Init*/ #error(EXPRULE_Initializer)● InitializerExp(MissingExpression)】》

#@_ARRAY_INIT_SAMPLE《#?STRUCTURE_EXPECTED!【[ 7, ]● InitializerArray(ArrayInitEntry(InitializerExp(Integer)))】》

#@INIT_NONVOID《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN #?EXP_ARRAY_LITERAL{#:DISCARD_CASE} ●InitializerExp(#@EXP_ASSIGN)】●
  
  ►#?STRUCTURE_EXPECTED!【#@_ARRAY_INIT_SAMPLE●#@_ARRAY_INIT_SAMPLE】●
  ►#?STRUCTURE_EXPECTED!【[ 5, 4 : foo ]●InitializerArray(
    ArrayInitEntry(InitializerExp(Integer)) 
    ArrayInitEntry(Integer InitializerExp(#@ExpIdentifier)))】●

  ►#?STRUCTURE_EXPECTED!【{ foo }●InitializerStruct(StructInitEntry(InitializerExp(#@ExpIdentifier)))】●
  ►#?STRUCTURE_EXPECTED!【{ foo : "zsdf" }●InitializerStruct(StructInitEntry(RefIdentifier InitializerExp(String)))】●
  ►#?STRUCTURE_EXPECTED!【{ #@_ARRAY_INIT_SAMPLE , bar : { foo : "zsdf"} }●InitializerStruct(
    StructInitEntry(#@_ARRAY_INIT_SAMPLE)
    StructInitEntry(RefIdentifier InitializerStruct(StructInitEntry(RefIdentifier InitializerExp(String))))
  )】●
  ►#?STRUCTURE_EXPECTED!【{ foo : bar, foobar; }/*Literal that looks like struct init*/●InitializerExp(
    ExpFunctionLiteral(BlockStatement( StatementLabel(?) StatementExpression(ExpInfix(* *)) )) )】●
¤》
#@INIT_NONVOID__SAMPLE《
  ►#@NO_INIT●
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE●InitializerExp(#@EXP_ASSIGN__LITE)】●
¤》
// Note the subtle difference that INIT_NONVOID__SAMPLE has NO_INIT case


#@INITIALIZERS《
  ►#@NO_INIT●
  ►#@INIT_NONVOID●
  ►#?STRUCTURE_EXPECTED!【void●InitializerVoid】●
¤》

#@INITIALIZERS__SAMPLE《
  ►#@NO_INIT●
  ►#@INIT_NONVOID__SAMPLE●
  ►#?STRUCTURE_EXPECTED!【void●InitializerVoid】●
¤》

