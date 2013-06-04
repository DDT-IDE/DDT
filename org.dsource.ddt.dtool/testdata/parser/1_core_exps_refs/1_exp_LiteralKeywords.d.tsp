▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    #@LIT_KEYWORDS《this●super●null●true●false●$》
#STRUCTURE_EXPECTED:  #@《
ExpThis●ExpSuper●ExpNull●ExpLiteralBool●ExpLiteralBool●ExpArrayLength
》(LIT_KEYWORDS)

Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@FIRST_ENTRY《
 ►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@ASSIGNEXP_NB ● MapEntry(#@EXP_ASSIGN #@ASSIGNEXP_NB)】●
 ►#?STRUCTURE_EXPECTED!【 #@NO_EXP : #@NO_EXP           ● MapEntry(#@NO_EXP #@NO_EXP)】●
¤》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@SPECIAL_KEYWORDS《__DATE__●__TIME__●__TIMESTAMP__●__VENDOR__●__FILE__》 #@《/*NoExtra*/●"extra str"》
#STRUCTURE_EXPECTED: ExpLiteralString
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)   #@SPECIAL_KEYWORDS《__LINE__●__VERSION__》
#STRUCTURE_EXPECTED: ExpLiteralInteger
