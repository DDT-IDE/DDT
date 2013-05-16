Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test array initializer

#@BRACKET_END《#@《●,》 #@CLOSEBRACKET_OR_BREAK》
#@ARRAY_INIT_PREFIX《
  ►#?STRUCTURE_EXPECTED!【#@INIT_NONVOID__SAMPLE, ●ArrayInitEntry(#@INIT_NONVOID__SAMPLE)】●
  ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_ANY__LITE : #@INIT_NONVOID__SAMPLE, ●ArrayInitEntry(#@ASSIGNEXP_ANY__LITE #@INIT_NONVOID__SAMPLE)】●
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
#PARSE(INITIALIZER)   [ #@ARRAY_INIT_PREFIX  #@ASSIGNEXP_ANY__LITE : #@INIT_NONVOID  #@BRACKET_END
#STRUCTURE_EXPECTED:  InitializerArray(#@ARRAY_INIT_PREFIX ArrayInitEntry(#@ASSIGNEXP_ANY__LITE #@INIT_NONVOID))

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
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂    
#PARSE(INITIALIZER)   {  foo : #error(EXP_CLOSE_BRACE) #@BREAK
#STRUCTURE_EXPECTED:  InitializerExp(ExpFunctionLiteral(BlockStatement(StatementLabel(?))))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂    
#PARSE(INITIALIZER)   { foo : bar, foobar; } /* Literal that looks like struct init */ 
#STRUCTURE_EXPECTED:  InitializerExp(
ExpFunctionLiteral(BlockStatement( StatementLabel(?) StatementExpression(ExpInfix(* *)) )) )

