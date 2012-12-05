//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - basic delim
q"/asdf" asdfd/"
q".asdf" asdfd."
q".."
q""asdf# asdfd""
q"(( )"  (())  asdf"  [<}  (xx"xx))"
q"[[ ]"  [[]]  asdf"  <{)  [xx"xx]]"
q"<< >"  <<>>  asdf"  {(]  <xx"xx>>"
q"{{ }"  {{}}  asdf"  ([>  {xx"xx}}"
/+#LEXERTEST
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
STRING_DELIM, EOL,
+/

//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - basic delim
q"/asdf/ asdfd"
q".. asdfd"
q""asdf" asdfd"
q"( asdf (asdf)) asdf" q"( asdf (asdf)) asdf)"
q"[ asdf [asdf]] asdf" q"[ asdf [asdf]] asdf]"
q"< asdf <asdf>> asdf" q"< asdf <asdf>> asdf>"
q"{ asdf {asdf}} asdf" q"{ asdf {asdf}} asdf}"
/+#LEXERTEST
STRING_DELIM!, EOL,
STRING_DELIM!, EOL,
STRING_DELIM!, EOL,
STRING_DELIM!, WS, STRING_DELIM!,EOL,
STRING_DELIM!, WS, STRING_DELIM!,EOL,
STRING_DELIM!, WS, STRING_DELIM!,EOL,
STRING_DELIM!, WS, STRING_DELIM!,EOL,
+/

//#SPLIT_SOURCE_TEST __________________
q"/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"//+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"(/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"/asdf"
/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"(asdf("
/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"( asdf (asdf)" xxx/+#LEXERTEST
STRING_DELIM!+/


//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - identifier delim
q"EOS
This is a multi-line " EOS
EOS
heredoc string
EOS"EOS
q"abc
"
"abc
abc
abc"
q"a
a"
/+#LEXERTEST
STRING_DELIM,ID,EOL,
STRING_DELIM,EOL,
STRING_DELIM,EOL,
+/
//#SPLIT_SOURCE_TEST __________________
q"xx asdf 
xx"
foobar/+#LEXERTEST
STRING_DELIM!,EOL,ID
+/
//#SPLIT_SOURCE_TEST __________________
q"xxx/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"xxx"asdf/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"xxx blah/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________ (invalid char(space) after id)
q"xxx 
xxx"foobar/+#LEXERTEST
STRING_DELIM!,ID+/
//#SPLIT_SOURCE_TEST __________________ (invalid char after id, test recovery)
q"xxx!
asd "
xxx 
xxx" foobar/+#LEXERTEST
STRING_DELIM!,WS,ID+/
//#SPLIT_SOURCE_TEST __________________
q"xxx
xxx "foobar/+#LEXERTEST
STRING_DELIM!+/
//#SPLIT_SOURCE_TEST __________________
q"xxx
xxx/+#LEXERTEST
STRING_DELIM!+/


// TODO Token String
//#SPLIT_SOURCE_TEST _____________________ TOKEN STRING
q{}
q{ asdf __TIME__  {nest braces} q"[{]" { q{nestedToken } String} }
q{asdf 
/* } */ {
// }  
}
"}" blah  }xxx
q{asdf {
` aaa` }
}
q{#!/usrs }
}
/+#LEXERTEST
STRING_TOKENS,EOL,
STRING_TOKENS,EOL,
STRING_TOKENS,ID,EOL,
STRING_TOKENS,EOL,
STRING_TOKENS,EOL,
+/

//#SPLIT_SOURCE_TEST ____________________
q{/+#LEXERTEST
STRING_TOKENS!+/
//#SPLIT_SOURCE_TEST ____________________
q{ aasdf
/+#LEXERTEST
STRING_TOKENS!+/
//#SPLIT_SOURCE_TEST ____________________
q{ aasdf
/*asdf
/+#LEXERTEST
STRING_TOKENS!
+/
//#SPLIT_SOURCE_TEST _____________
q{ asas sdas }
/+#LEXERTEST
STRING_TOKENS!+/
//#SPLIT_SOURCE_TEST _____________
q{ sdaa }
/+#LEXERTEST
STRING_TOKENS!+/

//#SPLIT_SOURCE_TEST ____________________
q{ __EOF__ }
/+#LEXERTEST
STRING_TOKENS!,+/
