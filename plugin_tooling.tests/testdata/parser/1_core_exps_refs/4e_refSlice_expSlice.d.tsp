▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     #@TYPEREFS__NBNE_APPENDABLE [ #@EXP_ASSIGN__LITE .. #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefSlice(#@TYPEREFS__NBNE_APPENDABLE #@EXP_ASSIGN__LITE #@EXP_ASSIGN__LITE )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(REFERENCE)     slicee [#@ASSIGNEXP_NB .. #@ASSIGNEXP_NB #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefSlice( RefIdentifier #@ASSIGNEXP_NB #@ASSIGNEXP_NB )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ specific case bug test
#PARSE(REFERENCE)     slicee [foo .. #@INT_AS_EXP #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  RefSlice( RefIdentifier #@ExpIdentifier #@INT_AS_EXP )

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
// ExpSlice used to be a class of its own, but now it's an ExpInfix operator
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee[ #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION)    #@EXP_POSTFIX [#@EXP_ASSIGN__LITE  ..  #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@EXP_POSTFIX ExpInfix(#@EXP_ASSIGN__LITE #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK ))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee [#@EXP_ASSIGN__NO_PENDING .. #@ASSIGNEXP_NB #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier ExpInfix( #@EXP_ASSIGN__NO_PENDING #@ASSIGNEXP_NB ) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee [123, #@EXP_ASSIGN__LITE .. #@EXP_ASSIGN__LITE #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier Integer 
                         ExpInfix( #@EXP_ASSIGN__LITE    #@EXP_ASSIGN__LITE ) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ base exp missing
#:DISCARD_CASE // Ignore this test, no longer applies

#PARSE(EXPRESSION)    #@NO_EXP [#@ASSIGNEXP_NBNE .. #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  
   ExpIndex( #@NO_EXP ExpInfix( #@ASSIGNEXP_NBNE    #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK ) )

