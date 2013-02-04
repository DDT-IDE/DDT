Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Float Literals
#@NUMS《0●1●345●1234567890●123_5_78__●1234567_9》
#@NUMSX《#@^NUMS●0123456789》

#@F_SP1《123●123.45●2_535●0.123●.897》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@《►
#@^NUMS.#@^NUMSX●.#@^NUMSX●#@^NUMS#NO_DECIMAL●
》#@《#@《fi●F●i●#?NO_DECIMAL{Fi,L}》●#@{E1,e012,E+1,e-1,E+0,e-0_12__,E-_012}#@《●F●fi●i●Li》》
#LEXERTEST:
FLOAT, EOL
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ decimal dot with no numbers following
#@^NUMS.#LEXERTEST:

FLOAT
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ errors
123__._123
._123
123.f
123.E123
0123f
0b01010f
0b01010.101f
#LEXERTEST:
FLOAT, ID, EOL,
DOT, ID, EOL,
FLOAT, ID, EOL,
FLOAT, ID, EOL,
INTEGER_OCTAL, ID, EOL,
INTEGER_BINARY, ID, EOL,
INTEGER_BINARY, FLOAT, EOL,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Exponent, no digits
#@(F_SP1)e_#@{f,F,}
#@(F_SP1)E#@{f,F,}#LEXERTEST:
FLOAT!FxD, EOL,
FLOAT!FxD,


Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Float Literals - HEX
#@HNUMS《0●1●345●1234567890ABCDEF●0123456789ABCDEF●123_4567_9●_0●__123__》

#@HF_SP1《0x123●0x25_AD_3F●0x0》
#@HF_SP1x《#@^HF_SP1●0x123.45●0x0.123●0x.897》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#@《►
0x#@^HNUMS.#@^HNUMS●0x.#@^HNUMS●0x#@^HNUMS●0x#@^HNUMS.●
》#@{P1,p012,P+1,p-1,P+0,p-0_12__,P-_012}#@《●f●Fi●L●i》#LEXERTEST:

FLOAT_HEX
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
0x0123456789ABCDEF.p0123456789ABCDEF
#LEXERTEST:
FLOAT_HEX, ID, EOL

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ HEX - Exponent, no digits
#@(HF_SP1x)P_#@{f,F,}
#@(HF_SP1x)P#@{f,F,}#LEXERTEST:
FLOAT_HEX!FxD, EOL,
FLOAT_HEX!FxD,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ HEX - no exponent
#@(HF_SP1)L
#@(HF_SP1x)i
#@(HF_SP1).L
#LEXERTEST:
INTEGER_HEX, EOL,
FLOAT_HEX!FxE, EOL,
FLOAT_HEX!FxE, EOL,
