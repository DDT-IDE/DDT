▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
"abc"
#STRUCTURE_EXPECTED:
ExpLiteralString
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)

#@STRINGS_UNTERMINATED!《
►`asdf #ERROR(MT_Sx)●
►r" asdf \x\f /* #ERROR(MT_Sx)●
►" asdf /* #ERROR(MT_Sx)●
►x" aaffdd \f/*aaff aad#ERROR(MT_Sx)●
►q"( asdf (asdf)" xxx#ERROR(MT_Sx)●
►q"#ERROR(MT_SDxD)●
►q"xxx
xxx "foobar#ERROR(MT_Sx)●
►q"xxx#ERROR(MT_SDxID)●
►q{ unterminated token string#ERROR(MT_Sx)●
¤》

#@STRINGS《
►` asdf \\\f 
/*sdf asd`●
►``●
►r" asdf \a\f /*sdf asd"w●
►r""●
►" asfdd \f/*sdf asd"d●
►"  \uFFAZ" #comment(ERROR_INVALID_HEX_SEQUENCE)●
►""●
►x" aaffdd \f/*aaff aad"d●
►x""●
►// ------------------ String Delim ------------------
q"/asdf" asdfd/"●
►q"/asdf/ asdfd" #ERROR(MT_SDx)●
►// ------------------ String Heredoc ------------------
q"EOS
` asdf \x\f /*
This is a multi-line " EOS
EOS
heredoc string
EOS"●
►q"xx asdf 
xx"#ERROR(MT_SDxID)●
►// ------------------ String token ------------------
q{ asdf __TIME__  {nest braces} q"[{]" { q{nestedToken } String} }●
¤》#@《
#comment(EMPTY)●
"second string"●
`string 0` r"2nd string" x" string3 " q"/string"4 xxx/"  q"EOS
string five
EOS" q{ string 6 } ●
#@STRINGS_UNTERMINATED●
¤》#comment(EOF):

#STRUCTURE_EXPECTED:
ExpLiteralString
