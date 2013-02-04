▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
'"'
#AST_STRUCTURE_EXPECTED:
ExpLiteralChar
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#@《►
'"'●
'a'●
''#error(MT_CHx0)●
'a#error(MT_CHxF)●
'#error(MT_CHxF)●
'
#error(MT_CHxL)●
'aaa'#error(MTC_CH_L)●

// TODO: analize token contents and add correct parser errors
'\xF'#error(MTC_CH_L)●
'\u012'#error(MTC_CH_L)●
'\U0123ABC'#error(MTC_CH_L)●
'\&'#error(MTC_CH_L)●

'\#error(MTC_CHxF)●
》#comment(EOF):

#AST_STRUCTURE_EXPECTED:
ExpLiteralChar
