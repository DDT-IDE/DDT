Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@LAMBDA_BODY《
  ►#?STRUCTURE_EXPECTED!【=> #@EXP_ASSIGN #?EXP_RULE_BROKEN【#@BREAK】● #@EXP_ASSIGN 】● 
  ►#?STRUCTURE_EXPECTED!【=> #@NO_EXP #@BREAK● #@NO_EXP】●
¤》
#@LAMBDA_BODY__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【=> #@EXP_ASSIGN__LITE● #@EXP_ASSIGN__LITE 】● 
¤》
#@FN_BLOCK_BODY《
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
¤》
#@FN_BLOCK_BODY__SAMPLE《#@ST_BLOCK__SAMPLE》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ identifier => lambda syntax
#PARSE(EXPRESSION)    foo #@LAMBDA_BODY
#STRUCTURE_EXPECTED:  ExpSimpleLambda(SimpleLambdaDefUnit(DefSymbol)  #@LAMBDA_BODY)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FLAG_AS_BROKEN_TEST《#test(IGNORE_BREAK_CHECK) #parser(AllowAnyErrors) #AST_SOURCE_EXPECTED(NoCheck) #IGNORE_BREAK_FLAG_CHECK(test)》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@FN_BLOCK_BODY
#STRUCTURE_EXPECTED: 
 ExpFunctionLiteral( #@FN_BLOCK_BODY )
 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test params
#PARSE(EXPRESSION)   (#@FN_PARAMS__NO_BROKEN) #@FN_ATTRIB__SAMPLE #@FN_BLOCK_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS__NO_BROKEN  #@FN_ATTRIB__SAMPLE #@FN_BLOCK_BODY__SAMPLE)

Ⓗ▂▂▂▂▂▂▂▂▂▂ // test commented out 
#PARSE(EXPRESSION)   (#@FN_PARAMS__NO_BROKEN) #@FN_ATTRIB__SAMPLE #@LAMBDA_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
            ExpLambda(#@FN_PARAMS__NO_BROKEN  #@FN_ATTRIB__SAMPLE #@LAMBDA_BODY__SAMPLE)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  "shot in the dark" test
#PARSE(EXPRESSION)   (#@FN_PARAMS  #@FN_BLOCK_BODY__SAMPLE #@FLAG_AS_BROKEN_TEST 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB  #@FN_BLOCK_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB  #@FN_BLOCK_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB  #@LAMBDA_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
            ExpLambda(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB  #@LAMBDA_BODY__SAMPLE)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@FN_BLOCK_BODY
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE  #@FN_BLOCK_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  // Needs body to parse as ExpFunctionLiteral
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@NO_BODY #@FLAG_AS_BROKEN_TEST
#STRUCTURE_EXPECTED: ExpParentheses(*) 
▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@LAMBDA_BODY
#STRUCTURE_EXPECTED: 
            ExpLambda(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE  #@LAMBDA_BODY)
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test that 'in' and 'body' don't parse as FunctionBody (even though grammar says it should)           
▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@BREAK  in {} #@FLAG_AS_BROKEN_TEST
▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@BREAK  body {} #@FLAG_AS_BROKEN_TEST
▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) nothrow  in {} #@FLAG_AS_BROKEN_TEST
#STRUCTURE_EXPECTED: ExpParentheses(*)
▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) in {} #@FLAG_AS_BROKEN_TEST
#STRUCTURE_EXPECTED: ExpParentheses(*)
▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) body {} #@FLAG_AS_BROKEN_TEST 
#STRUCTURE_EXPECTED: ExpParentheses(*)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with the function/delegate keyword
#@FN_KEYWORD《function●delegate》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: ExpFunctionLiteral()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD ( #error(EXP_CLOSE_PARENS) #@BREAK
#STRUCTURE_EXPECTED: ExpFunctionLiteral()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@FN_BLOCK_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
ExpFunctionLiteral(                #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE  #@FN_BLOCK_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with typeref
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE__LITE (#@FN_PARAMS_SAMPLE #@CLOSEPARENS_OR_BREAK #@FN_ATTRIB__SAMPLE #@FN_BLOCK_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE__LITE  #@FN_PARAMS_SAMPLE #@CLOSEPARENS_OR_BREAK #@FN_ATTRIB__SAMPLE #@FN_BLOCK_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE__LITE (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE #@NO_ST_BLOCK
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE__LITE  #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE #@NO_ST_BLOCK)