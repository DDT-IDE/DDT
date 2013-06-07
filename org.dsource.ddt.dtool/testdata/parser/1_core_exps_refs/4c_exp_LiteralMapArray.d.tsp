▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [1 : "asdf", "2" : foo, 66 : "66" ]
#STRUCTURE_EXPECTED:  ExpLiteralMapArray(MapEntry(Integer String) MapEntry(String *) MapEntry(Integer String))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [ #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpLiteralArray()
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FIRST_ENTRY《
 ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@ASSIGNEXP_NB ● MapEntry(#@EXP_ASSIGN #@ASSIGNEXP_NB)】●
 ►#?STRUCTURE_EXPECTED!【 #@NO_EXP : #@NO_EXP           ● MapEntry(#@NO_EXP #@NO_EXP)】●
¤》
#@ENTRY《
  ►#@FIRST_ENTRY●
  ►#?STRUCTURE_EXPECTED!【#@ASSIGNEXP_NBNE #error(EXP_COLON)●MapEntry(#@ASSIGNEXP_NBNE)】●
¤》
#@ENTRY__SAMPLE《
►#?STRUCTURE_EXPECTED!【7 : true ● MapEntry(Integer Bool)】●
¤》

#@ARGS《
  ►#?STRUCTURE_EXPECTED!【#@FIRST_ENTRY #@《 ●,》● #@FIRST_ENTRY】 ●
  ►#?STRUCTURE_EXPECTED!【#@ENTRY__SAMPLE , #@ENTRY #@《 ●,》 ● #@ENTRY__SAMPLE #@ENTRY】 ●
  ►#?STRUCTURE_EXPECTED!【#@ENTRY__SAMPLE , #@ENTRY__SAMPLE , #@ENTRY ● #@ENTRY__SAMPLE #@ENTRY__SAMPLE #@ENTRY】 ●
¤》 
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [ #@ARGS #@CLOSEBRACKET_OR_BREAK  
#STRUCTURE_EXPECTED:  ExpLiteralMapArray( #@ARGS )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ this might change in future
#PARSE(EXPRESSION)    [ #@ENTRY__SAMPLE , #@NO_CloseBRACKET #@BREAK  
#STRUCTURE_EXPECTED:  ExpLiteralMapArray( #@ENTRY__SAMPLE )

