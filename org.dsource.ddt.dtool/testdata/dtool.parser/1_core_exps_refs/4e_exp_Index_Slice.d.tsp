
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        slicee[]
#STRUCTURE_EXPECTED:  ExpSlice( #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee[ #error(EXP_CLOSE_BRACKET) #@BREAK
#STRUCTURE_EXPECTED:  ExpSlice( #@ExpIdentifier  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee[123]
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier Integer )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    slicee[bar]
#STRUCTURE_EXPECTED:  ExpIndex( #@ExpIdentifier #@ExpIdentifier )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    this[123 , bar]
#STRUCTURE_EXPECTED:  ExpIndex( ExpThis Integer #@ExpIdentifier )
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@EXP_OR_NO《#@EXP_ASSIGN●#@NO_EXP》

#@INDEXEE《
  ►#?STRUCTURE_EXPECTED!【slicee #SINGLE_ID(flag)● #@ExpIdentifier】● 
  ►#?STRUCTURE_EXPECTED!【slicee() ● ExpCall(#@ExpIdentifier)】 ●
  ►#?STRUCTURE_EXPECTED!【slicee[123 .. 44] ● ExpSlice(#@ExpIdentifier Integer Integer) 】●
  ►#?STRUCTURE_EXPECTED!【slicee[123, 44] ● ExpIndex(#@ExpIdentifier Integer Integer) 】●
¤》

#@ARGLIST《
 ►#?SINGLE_ID【 
  ►#?STRUCTURE_EXPECTED!【[ #@EXP_ASSIGN__NO_POSSIBLE_REFS  #@BkCLOSE_OR_BREAK ● #@EXP_ASSIGN__NO_POSSIBLE_REFS】●
  ►#?STRUCTURE_EXPECTED!【[ #@EXP_ASSIGN  #@BkCLOSE_OR_BREAK ● #@EXP_ASSIGN】●  
 ¤】● 
  ►#?STRUCTURE_EXPECTED!【[ #@EXP_OR_NO , #@EXP_OR_NO #@BkCLOSE_OR_BREAK ● #@EXP_OR_NO #@EXP_OR_NO 】● 
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE #@ARGLIST
#STRUCTURE_EXPECTED:  ExpIndex( #@INDEXEE #@ARGLIST )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE [123 .. bar]
#STRUCTURE_EXPECTED:  ExpSlice( #@INDEXEE Integer #@ExpIdentifier )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@INDEXEE [#@EXP_OR_NO .. #@EXP_OR_NO #@BkCLOSE_OR_BREAK
#STRUCTURE_EXPECTED:  ExpSlice( #@INDEXEE #@EXP_OR_NO #@EXP_OR_NO )
