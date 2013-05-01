▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ARG!《#@NO_EXP●#@EXPS__NO_UNAMBIG_REFS》
#PARSE(EXPRESSION)    ( #@ARG #@CLOSEPARENS_OR_BREAK
#STRUCTURE_EXPECTED:  ExpParentheses( #@ARG )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    ( #error(TYPE_AS_EXP_VALUE)【#@TYPEREFS_UNAMBIG】  #@CLOSEPARENS_OR_BREAK  //unambig Refs 
#STRUCTURE_EXPECTED:  ExpParentheses( #@TYPEREFS_UNAMBIG )

