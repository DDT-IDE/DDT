//#SPLIT_SOURCE_TEST _____________________
/+#LEXERTEST#
EOF
+/
//#SPLIT_SOURCE_TEST _____________________
 aaa
/+#LEXERTEST#
EOF,
+/
//#SPLIT_SOURCE_TEST _____________________
aa
/+#LEXERTEST#
ID,EOF
+/
//#SPLIT_SOURCE_TEST _____________________
abc 
/+#LEXERTEST#
ID,EOF,
+/

//#SPLIT_SOURCE_TEST _____________________
/+#LEXERTEST#
EOF,
+/

//#SPLIT_SOURCE_TEST _____________________
.__EOF__./
/+#LEXERTEST#
DOT,EOF
+/