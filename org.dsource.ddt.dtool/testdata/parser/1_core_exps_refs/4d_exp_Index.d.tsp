▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(EXPRESSION)    slicee[bar]
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(EXPRESSION)    this[123 , bar]
#STRUCTURE_EXPECTED:  ExpIndex( ExpThis Integer #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@EXP_POSTFIX [ #@EXP_ASSIGN__LITE  #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@EXP_POSTFIX #@EXP_ASSIGN__LITE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【slicee #SINGLE_ID(flag)● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【slicee() ● ExpCall(#@ExpIdentifier)】 ●
  ►#?STRUCTURE_EXPECTED!【slicee[123, 44] ● ExpIndex(#@ExpIdentifier Integer Integer) 】●
¤》

#@ARGLIST《
 ►#?SINGLE_ID【 
  ►#?STRUCTURE_EXPECTED!【[ #@EXP_ASSIGN__NO_POSSIBLE_REFS  #@CLOSEBRACKET_OR_BREAK ● #@EXP_ASSIGN__NO_UNAMBIG_REFS】●
  ►#?STRUCTURE_EXPECTED!【[ #@EXP_ASSIGN  #@CLOSEBRACKET_OR_BREAK ● #@EXP_ASSIGN】●  
 ¤】● 
  ►#?STRUCTURE_EXPECTED!【[ #@ASSIGNEXP_NB , #@ASSIGNEXP_NB #@CLOSEBRACKET_OR_BREAK ● #@ASSIGNEXP_NB #@ASSIGNEXP_NB 】● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE #@ARGLIST
#STRUCTURE_EXPECTED:  ExpIndex( #@INDEXEE #@ARGLIST )
