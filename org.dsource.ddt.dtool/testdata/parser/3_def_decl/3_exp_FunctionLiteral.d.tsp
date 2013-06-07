Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FLAG_AS_BROKEN_TEST《#test(IGNORE_BREAK_CHECK) #parser(AllowAnyErrors) #AST_SOURCE_EXPECTED(NoCheck) #IGNORE_BREAK_FLAG_CHECK(test)》

#@LAMBDA_BODY《
  ►#?STRUCTURE_EXPECTED!【=> #@EXP_ASSIGN #?EXP_RULE_BROKEN【#@BREAK】● #@EXP_ASSIGN 】● 
  ►#?STRUCTURE_EXPECTED!【=> #@NO_EXP #@BREAK● #@NO_EXP】●
¤》
#@LAMBDA_BODY__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【=> #@EXP_ASSIGN__LITE● #@EXP_ASSIGN__LITE 】● 
¤》

#@FNLIT_BODY《
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
 ►#@LAMBDA_BODY●
¤》
#@FNLIT_BODY__SAMPLE《
 ►#@ST_BLOCK__SAMPLE●
 ►#@LAMBDA_BODY__SAMPLE●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ identifier => lambda syntax
#PARSE(EXPRESSION)    foo #@LAMBDA_BODY
#STRUCTURE_EXPECTED:  ExpSimpleLambda(SimpleLambdaDefUnit(DefSymbol) #@LAMBDA_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@IGNORE_REST #@LAMBDA_BODY__SAMPLE
#STRUCTURE_EXPECTED:  
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@BLOCK_ST__NE
#STRUCTURE_EXPECTED:  ExpFunctionLiteral( #@BLOCK_ST__NE )
 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test params
#PARSE(EXPRESSION)   (#@FN_PARAMS__NO_BROKEN) #@FN_ATTRIB__SAMPLE #@FNLIT_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS__NO_BROKEN  #@FN_ATTRIB__SAMPLE #@FNLIT_BODY__SAMPLE)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ blind test against FN_PARAMS and broken parens
#PARSE(EXPRESSION)   (#@FN_PARAMS  #@ST_BLOCK__SAMPLE #@FLAG_AS_BROKEN_TEST

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test attrib
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB  #@FNLIT_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB  #@FNLIT_BODY__SAMPLE)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test body
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@FNLIT_BODY
#STRUCTURE_EXPECTED: 
   ExpFunctionLiteral(#@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE  #@FNLIT_BODY)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  // Needs body to parse as ExpFunctionLiteral
#PARSE(EXPRESSION)   (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@NO_ST_BLOCK #@FLAG_AS_BROKEN_TEST
#STRUCTURE_EXPECTED: ExpParentheses(*) 
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
#PARSE(EXPRESSION)   #@FN_KEYWORD (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE  #@FNLIT_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
ExpFunctionLiteral(                #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE  #@FNLIT_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ with typeref
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE__LITE (#@FN_PARAMS_SAMPLE #@CLOSEPARENS_OR_BREAK #@FN_ATTRIB__SAMPLE #@FNLIT_BODY__SAMPLE
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE__LITE  #@FN_PARAMS_SAMPLE #@CLOSEPARENS_OR_BREAK #@FN_ATTRIB__SAMPLE #@FNLIT_BODY__SAMPLE)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)   #@FN_KEYWORD #@TYPEREF_NBNE__LITE (#@FN_PARAMS_SAMPLE) #@FN_ATTRIB__SAMPLE #@NO_ST_BLOCK #@BREAK
#STRUCTURE_EXPECTED: 
               ExpFunctionLiteral(#@TYPEREF_NBNE__LITE  #@FN_PARAMS_SAMPLE  #@FN_ATTRIB__SAMPLE #@NO_ST_BLOCK)