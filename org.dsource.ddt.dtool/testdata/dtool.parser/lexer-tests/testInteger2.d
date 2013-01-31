Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ Integer Literals
#@INTEGER_SUFFIX{,L,Lu,LU,u,U}
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@《0●1●123456789●123_45_6789_●123_45__67___》#@INTEGER_SUFFIX
#LEXERTEST:
INTEGER, EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
_123
0123456789
1234P1f
#LEXERTEST:
ID, EOL,
INTEGER_OCTAL!IOx, EOL,
INTEGER, ID, EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ binary
#@《0b0110●0b0●0B1●0B_01__10__》#@INTEGER_SUFFIX
#LEXERTEST:
INTEGER_BINARY, EOL,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
0b_
0b00123456
0b001_23456
#LEXERTEST:
INTEGER_BINARY!Ix, EOL, 
INTEGER_BINARY!IBx, EOL,
INTEGER_BINARY!IBx, EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ octal
#@《001234567●0_01__234567__●0_●00●01》#@INTEGER_SUFFIX
#LEXERTEST:
INTEGER_OCTAL, EOL,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
0123456789
0981234567
#LEXERTEST:
INTEGER_OCTAL!IOx, EOL,
INTEGER_OCTAL!IOx, EOL,

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ hex
#@《0x0123456789ABCDEFabcdef●0x_0123456_789ABCD___EFabcdef__●0x0●0X1●0xA●0XF●0x123●0x0123FF》#@INTEGER_SUFFIX
#LEXERTEST:
INTEGER_HEX, EOL,
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
0x_
0xabcdefGHIKLM
0xabcdEFghiklm
#LEXERTEST:
INTEGER_HEX!Ix, EOL, 
INTEGER_HEX, ID, EOL,
INTEGER_HEX, ID, EOL,