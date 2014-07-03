Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@KEYWORD《new●delete》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@KEYWORD #error(EXP_OPEN_PARENS)【】 #@BREAK
#STRUCTURE_EXPECTED:  DeclarationAllocatorFunction()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@KEYWORD (#@FN_PARAMS_SAMPLE #error(EXP_CLOSE_PARENS) #@BREAK
#STRUCTURE_EXPECTED:  DeclarationAllocatorFunction(#@FN_PARAMS_SAMPLE $
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(DECLARATION)   #@KEYWORD (#@FN_PARAMS_SAMPLE) #@FN_BODY__SIMPLE_TESTER
#STRUCTURE_EXPECTED:  DeclarationAllocatorFunction(#@FN_PARAMS_SAMPLE #@FN_BODY__SIMPLE_TESTER)
