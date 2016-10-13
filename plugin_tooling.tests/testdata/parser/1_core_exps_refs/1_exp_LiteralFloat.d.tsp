▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#@《123.456●.456E12●0.0F●0x25_AD_3FP+1●0x.AAP12_》
#STRUCTURE_EXPECTED:
ExpLiteralFloat
Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂  Float Literals (most code is copy paste from lexer tests)
#@NUMS《0●1●345●1234567890●123_5_78__●1234567_9》
#@NUMS__LITE《1234567890》
#@NUMSX《#@^NUMS●0123456789》

#@F_SP1《123●123.45●2_535●0.123●.897》

#@HNUMS《0●1●345●1234567890ABCDEF●0123456789ABCDEF●123_4567_9●_0●__123__》
#@HNUMS__LITE《1234567890ABCDEF》

#@HF_SP1《0x123●0x25_AD_3F●0x0》
#@HF_SP1x《#@^HF_SP1●0x123.45●0x0.123●0x.897》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 
#PARSE(EXPRESSION) #comment(NO_STDOUT)
#@《

  ►#@《
    ►#@^NUMS.#@^NUMSX●
    ►.#@^NUMSX●
    ►#@^NUMS#NO_DECIMAL(flag)●
  ¤》#@《#@《fi●F●i●#?NO_DECIMAL{Fi,L}》●#@{E1,e012,E+1,e-1,E+0,e-0_12__,E-_012}#@《●F●fi●i●Li》》●

  ►#@^NUMS.●

  ►#@《
    ►0x#@^HNUMS.#@^HNUMS●
    ►0x.#@^HNUMS●
    ►0x#@^HNUMS●
    ►0x#@^HNUMS.●
  ¤》#@{P1,p012,P+1,p-1,P+0,p-0_12__,P-_012}#@《●fi●F●L●i》●

¤》#STRUCTURE_EXPECTED:
ExpLiteralFloat

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) 
#@《
  ►#@F_SP1•e_●
  ►#@F_SP1•E●
  ►#@HF_SP1x•P_●
  ►#@HF_SP1x•P●
¤》#@{f,F,}#error(MT_FxD)

#STRUCTURE_EXPECTED:
ExpLiteralFloat
