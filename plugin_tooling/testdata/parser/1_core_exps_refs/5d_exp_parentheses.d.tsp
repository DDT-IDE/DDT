▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    ( #@EXP_ANY #@CLOSEPARENS_END
#STRUCTURE_EXPECTED:  ExpParentheses( #@EXP_ANY )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ // test reporting of TYPE_AS_EXP errors (there should be no error)  
#PARSE(EXPRESSION)    ( int )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂   
#PARSE(EXPRESSION)    ( int[] ) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂   
#PARSE(EXPRESSION)    ( int[typeof(1)] ) 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂   
#PARSE(EXPRESSION)    (void function(int)) 

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ parentheses followed by DOT syntax 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)    ( #@EXPS__NO_POSSIBLE_REFS ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@EXPS__NO_POSSIBLE_REFS ) RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(EXPRESSION)    ( #@TYPEREF_NENBParens ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@TYPEREF_NENBParens ) RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(EXPRESSION)    ( #@NO_TOE ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@NO_TOE ) RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(EXPRESSION)    ( int ).foo 
