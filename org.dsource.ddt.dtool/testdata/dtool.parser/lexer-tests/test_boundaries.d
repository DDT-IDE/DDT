//#SOURCE_TESTS 
//#SPLIT_SOURCE_TEST _____________________
/+#LEXERTEST  
EOF,
+/
//#SPLIT_SOURCE_TEST _____________________

/+#LEXERTEST --
EOL,
EOF,
+/
//#SPLIT_SOURCE_TEST _________ boundary case

/+  +/
""
/+#LEXERTEST
ERROR,EOL,
COMMENT_NESTED,EOL,
STRING_DQ,EOL
+/