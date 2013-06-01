▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(EXPRESSION)    [ 1, "2" #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpLiteralArray(Integer String)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [ #@CLOSEBRACKET_OR_BREAK 
#STRUCTURE_EXPECTED:  ExpLiteralArray()
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [ #@EXPARGS_ESEP  #@CLOSEBRACKET_OR_BREAK  
#STRUCTURE_EXPECTED:  ExpLiteralArray( #@EXPARGS_ESEP )
