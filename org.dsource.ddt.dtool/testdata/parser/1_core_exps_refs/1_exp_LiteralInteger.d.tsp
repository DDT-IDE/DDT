▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
123
#STRUCTURE_EXPECTED:
ExpLiteralInteger
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #comment(NO_STDOUT)
#@《
►#@《0●1●123456789●123_45_6789_●123_45__67___》●
►#@《0b0110●0b0●0B1●0B_01__10__》●
►0b_#error(MT_Ix)●
►0b00123456#error(MT_IBx)●
►0b001_23456#error(MT_IBx)●
►#@《001234567●0_01__234567__●0_●00●01》●
►0123456789#error(MT_IOx)●
►0981234567#error(MT_IOx)●
►#@《0x0123456789ABCDEFabcdef●0x_0123456_789ABCD___EFabcdef__●0x0●0X1●0xA●0XF●0x123●0x0123FF》●
►0x_#error(MT_Ix)●
¤》#@INTEGER_SUFFIX{,L,Lu,LU,u,U}

#STRUCTURE_EXPECTED:
ExpLiteralInteger
