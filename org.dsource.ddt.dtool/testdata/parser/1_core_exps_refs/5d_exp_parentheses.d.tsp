▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    ( #@EXP_ANY #@CLOSEPARENS_END
#STRUCTURE_EXPECTED:  ExpParentheses( #@EXP_ANY )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ parentheses followed by DOT syntax 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)    ( #@EXPS__NO_POSSIBLE_REFS ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@EXPS__NO_POSSIBLE_REFS ) RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(EXPRESSION)    ( #@TYPEREF_NBNEParens ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@TYPEREF_NBNEParens ) RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  
#PARSE(EXPRESSION)    ( #@NO_TOE ) . foo
#STRUCTURE_EXPECTED:  ExpReference(RefQualified(ExpParentheses( #@NO_TOE ) RefIdentifier))
