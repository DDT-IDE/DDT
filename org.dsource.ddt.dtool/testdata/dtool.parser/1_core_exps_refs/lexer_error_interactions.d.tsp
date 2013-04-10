Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂

TODO: refactor these code samples?

#@LEXER_ERRORS《
''#error(MT_CHx0)●
'a#error(MT_CHxF)●
'#error(MT_CHxF)●
'
#error(MT_CHxL)●
'aaa'#error(MTC_CH_L)●
'\U0123ABC'#error(MTC_CH_L) #comment(the error will change in future) ●

0b00123456#error(MT_IBx)●
0123456789#error(MT_IOx)●

123.456E_#error(MT_FxD)●
0x25_AD_3FP#error(MT_FxD)●

`asdf #ERROR(MT_Sx)●
r" asdf \x\f /* #ERROR(MT_Sx)●
" asdf /* #ERROR(MT_Sx)●
q"/asdf/ asdfd" #ERROR(MT_SDx)●
q"xx asdf 
xx"#ERROR(MT_SDxID)●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
int foo #error(EXP_SEMICOLON) #@LEXER_ERRORS#error(<SE_decl)#comment(EOF):

#STRUCTURE_EXPECTED:
DefVariable(? ?) InvalidSyntaxElement 

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@LEXER_ERRORS#error(<SE_decl)#comment(EOF):

#STRUCTURE_EXPECTED:
InvalidSyntaxElement
