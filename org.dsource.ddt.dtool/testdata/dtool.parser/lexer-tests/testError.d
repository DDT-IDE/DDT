//#SPLIT_SOURCE_TEST _____________________ test error tokens
aaaaaaaaaa
/+#LEXERTEST
ID,ERROR,ID,ERROR,ID,EOL 
+/
//#SPLIT_SOURCE_TEST __________________
aaaaa aa/a
/+#LEXERTEST
ID,ERROR,ID,WS,ERROR,ID,ERROR,ID,DIV,ERROR,ID,EOL 
+/
//#SPLIT_SOURCE_TEST _________ boundary case
/+#LEXERTEST
ERROR+/
//#SPLIT_SOURCE_TEST _________ boundary case
/+#LEXERTEST
ERROR+/
