//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - basic delim
q"/asdf" asdfd/"
q".asdf" asdfd."
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
+/

//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - basic delim
q"/asdf/ asdfd"
q".asdf. asdfd"
q""asdf" asdfd"
q"( asdf (asdf)) asdf" q"( asdf (asdf)) asdf)"
q"[ asdf [asdf]] asdf" q"[ asdf [asdf]] asdf]"
q"< asdf <asdf>> asdf" q"< asdf <asdf>> asdf>"
q"{ asdf {asdf}} asdf" q"{ asdf {asdf}} asdf}"
/+#LEXERTEST
ERROR, EOL,
ERROR, EOL,
ERROR, EOL,
ERROR, WS, ERROR,EOL,
ERROR, WS, ERROR,EOL,
ERROR, WS, ERROR,EOL,
ERROR, WS, ERROR,EOL,
+/

//#SPLIT_SOURCE_TEST __________________
q"/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"//+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"(/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"/asdf"
/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"(asdf("
/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"( asdf (asdf)" xxx/+#LEXERTEST
ERROR+/


//#SPLIT_SOURCE_TEST _____________________ DELIM STRING - identifier delim
q"EOS
This is a multi-line " EOS
EOS
heredoc string
EOS"EOS
q"a
a
a"
q"xx123x
a
xx123x"
/+#LEXERTEST
STRING_DELIM,ID,EOL,
STRING_DELIM,EOL,
STRING_DELIM,EOL,
+/
//#SPLIT_SOURCE_TEST __________________
q"xx asdf 
xx"
foobar/+#LEXERTEST
ERROR,EOL,ID
+/
//#SPLIT_SOURCE_TEST __________________
q"xxx/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"xxx"asdf/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"xxx blah/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________ (invalid char(space) after id)
q"xxx 
xxx"foobar/+#LEXERTEST
ERROR,ID+/
//#SPLIT_SOURCE_TEST __________________ (invalid char after id, test recovery)
q"xxx!
asd "
xxx 
xxx" foobar/+#LEXERTEST
ERROR,WS,ID+/
//#SPLIT_SOURCE_TEST __________________
q"xxx
xxx "foobar/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST __________________
q"xxx
xxx/+#LEXERTEST
ERROR+/


// TODO Token String

