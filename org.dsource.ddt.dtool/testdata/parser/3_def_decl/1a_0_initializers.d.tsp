▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   #@EXP_ASSIGN #?EXP_ARRAY_LITERAL{#:DISCARD_CASE} #?EXP_RULE_BROKEN【#@BREAK】
#STRUCTURE_EXPECTED:  InitializerExp(#@EXP_ASSIGN)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@INITIALIZERS_NONVOID《
  ►#@NO_INIT●
  ►#@INITIALIZERS_NONVOID_NE●
¤》
#@INITIALIZERS_NONVOID_NE《
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN #?EXP_ARRAY_LITERAL{#:DISCARD_CASE} ●InitializerExp(#@EXP_ASSIGN)】●
  ►#?STRUCTURE_EXPECTED!【[ 7,  4 : foo,]● 
    InitializerArray( ArrayInitEntry(InitializerExp(Integer)) ArrayInitEntry(Integer InitializerExp(#@ExpIdentifier)) 
  )】●
  ►#?STRUCTURE_EXPECTED!【{ foo : bar, bar } ●InitializerStruct(
    StructInitEntry(RefIdentifier InitializerExp(#@ExpIdentifier)) StructInitEntry(InitializerExp(#@ExpIdentifier))
  )】●
¤》

#@INITIALIZERS_NONVOID__SAMPLE《
  ►#@NO_INIT●
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__LITE ●InitializerExp(#@EXP_ASSIGN__LITE)】●
¤》
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test array initializer
#@BRACKET_END《#@《●,》 #@CLOSEBRACKET_OR_BREAK》

#@ARRAY_INIT_ENTRY《
  ►#?STRUCTURE_EXPECTED!【#@INITIALIZERS_NONVOID_NE ●ArrayInitEntry(#@INITIALIZERS_NONVOID_NE)】●
  ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_ANY__LITE : #@INITIALIZERS_NONVOID ●
          ArrayInitEntry(#@ASSIGNEXP_ANY__LITE   #@INITIALIZERS_NONVOID)】●
¤》
#@ARRAY_INIT_ENTRY__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_ANY__LITE : #@INITIALIZERS_NONVOID__SAMPLE ●
          ArrayInitEntry(#@ASSIGNEXP_ANY__LITE   #@INITIALIZERS_NONVOID__SAMPLE)】●
¤》

#@ARRAY_INIT《
  ►#?STRUCTURE_EXPECTED!【[ #@CLOSEBRACKET_OR_BREAK●InitializerArray()】●
  ►#?STRUCTURE_EXPECTED!【[ #@NO_INIT ,]●InitializerArray(ArrayInitEntry(#@NO_INIT))】●
  ►#?STRUCTURE_EXPECTED!【[ #@ARRAY_INIT_ENTRY__SAMPLE  #@BRACKET_END ●
          InitializerArray(#@ARRAY_INIT_ENTRY__SAMPLE)】●
          
  ►#?STRUCTURE_EXPECTED!【[ #@ARRAY_INIT_ENTRY ]●
          InitializerArray(#@ARRAY_INIT_ENTRY)】●
  ►#?STRUCTURE_EXPECTED!【[ #@ARRAY_INIT_ENTRY__SAMPLE , #@ARRAY_INIT_ENTRY ] ●
          InitializerArray(#@ARRAY_INIT_ENTRY__SAMPLE   #@ARRAY_INIT_ENTRY)】●
  
  ►#@_ARRAY_INIT__SPECIAL_CASES●
  ►#?STRUCTURE_EXPECTED!【[ 123 , #@BRACKET_BREAK = 456●InitializerArray(ArrayInitEntry(InitializerExp(Integer)))】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   #@ARRAY_INIT
#STRUCTURE_EXPECTED:  #@ARRAY_INIT

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test struct initializer

#@BRACE_END《#@《●,》 #@CLOSEBRACE_OR_BREAK》
#@REFID_OR_NO《#?STRUCTURE_EXPECTED!【foo● RefIdentifier】●#?STRUCTURE_EXPECTED!【#@NO_ID● #@NO_ID】》

#@STRUCT_INIT_ENTRY《
  ►#?STRUCTURE_EXPECTED!【#@INITIALIZERS_NONVOID_NE ●StructInitEntry(#@INITIALIZERS_NONVOID_NE)】●
  ►#?STRUCTURE_EXPECTED!【#@REFID_OR_NO : #@INITIALIZERS_NONVOID●
         StructInitEntry(#@REFID_OR_NO   #@INITIALIZERS_NONVOID)】●
¤》
#@STRUCT_INIT_ENTRY__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【fooasdfasdfas : #@INITIALIZERS_NONVOID__SAMPLE ●
         StructInitEntry(RefIdentifier   #@INITIALIZERS_NONVOID__SAMPLE)】●
¤》

#@STRUCT_INIT《
  ►#?STRUCTURE_EXPECTED!【{ #@CLOSEBRACE_OR_BREAK●InitializerStruct()】●
  ►#?STRUCTURE_EXPECTED!【{  #@NO_INIT ,}●InitializerStruct(StructInitEntry(#@NO_INIT))】●
  
  ►#?STRUCTURE_EXPECTED!【{ #@STRUCT_INIT_ENTRY__SAMPLE #@BRACE_END ●
         InitializerStruct(#@STRUCT_INIT_ENTRY__SAMPLE )】●

  ►#?STRUCTURE_EXPECTED!【{ #@STRUCT_INIT_ENTRY         } ●
         InitializerStruct(#@STRUCT_INIT_ENTRY)】●
  ►#?STRUCTURE_EXPECTED!【{ #@STRUCT_INIT_ENTRY__SAMPLE , #@STRUCT_INIT_ENTRY  } ●
         InitializerStruct(#@STRUCT_INIT_ENTRY__SAMPLE   #@STRUCT_INIT_ENTRY)】●
  
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INIT_STRUCT)   #@STRUCT_INIT
#STRUCTURE_EXPECTED:  #@STRUCT_INIT
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(INITIALIZER)   #@_STRUCT_INIT__SPECIAL_CASES
#STRUCTURE_EXPECTED:  #@_STRUCT_INIT__SPECIAL_CASES

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ if broken parse as exp   
#PARSE(INITIALIZER)   {  #error(EXP_CLOSE_BRACE) #@BREAK
#STRUCTURE_EXPECTED:  InitializerExp(ExpFunctionLiteral(BlockStatement))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ if broken parse as exp
#PARSE(INITIALIZER)   {  foo : #error(EXP_CLOSE_BRACE) #@BREAK
#STRUCTURE_EXPECTED:  InitializerExp(ExpFunctionLiteral(BlockStatement(StatementLabel(?))))

