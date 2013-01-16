//#SOURCE_TESTS 
//#SPLIT_SOURCE_TEST _____________________
#!/usr/bin blah
blah
/+#LEXERTEST
SCRIPT_LINE_INTRO, ID, EOL
+/

//#SPLIT_SOURCE_TEST _____________________
#!/+#LEXERTEST
SCRIPT_LINE_INTRO
+/
//#SPLIT_SOURCE_TEST _____________________
#!
blah/+#LEXERTEST
SCRIPT_LINE_INTRO, ID
+/


//#SPLIT_SOURCE_TEST _____________________
 #!/usr/bin
blah
/+#LEXERTEST
WS, SPECIAL_TOKEN_LINE!,
ID,EOL
+/

//#SPLIT_SOURCE_TEST _____________________
#/+#LEXERTEST
SPECIAL_TOKEN_LINE!,
+/
