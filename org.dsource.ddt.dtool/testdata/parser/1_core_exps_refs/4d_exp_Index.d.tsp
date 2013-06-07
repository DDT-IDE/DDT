▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(EXPRESSION)    slicee[bar]
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ basic case
#PARSE(EXPRESSION)    this[123 , bar]
#STRUCTURE_EXPECTED:  ExpIndex( ExpThis Integer #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@EXP_POSTFIX [ #@EXP_ASSIGN__LITE  #@《 ●,》 #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@EXP_POSTFIX #@EXP_ASSIGN__LITE )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【slicee #SINGLE_ID(flag)● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【slicee() ● ExpCall(#@ExpIdentifier)】 ●
¤》

#@ARGLIST《
 ►#?SINGLE_ID【 
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN__NO_POSSIBLE_REFS #@《 ●,》 ● #@EXP_ASSIGN__NO_POSSIBLE_REFS】●
  ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN  #@《 ●,》  ● #@EXP_ASSIGN】●  
 ¤】● 
  ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_NB , #@ASSIGNEXP_NBNE #@《 ●,》  ● #@ASSIGNEXP_NB #@ASSIGNEXP_NBNE 】● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE [ #@ARGLIST #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpIndex( #@INDEXEE #@ARGLIST )
