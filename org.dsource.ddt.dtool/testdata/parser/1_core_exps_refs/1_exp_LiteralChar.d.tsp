▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
'"'
#STRUCTURE_EXPECTED:
ExpLiteralChar
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#@《
►'"'●
►'a'●
►''#error(MT_CHx0)●
►'a#error(MT_CHxF)●
►'#error(MT_CHxF)●
►'
#error(MT_CHxL)●
►'aaa'#error(MTC_CH_L)●

►// TODO: analize token contents and add correct parser errors
'\xF'●
►'\u012'●
►'\U0123ABC'●
►'\&'●

►'\#error(MTC_CHxF)●
¤》#comment(EOF):

#STRUCTURE_EXPECTED:
ExpLiteralChar
