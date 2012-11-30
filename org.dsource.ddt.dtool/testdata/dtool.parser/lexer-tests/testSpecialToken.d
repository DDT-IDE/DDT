//#SPLIT_SOURCE_TEST ___________________ 
String#line 6 "foo\bar"
x;// this is now line 6 of file foo\bar
// #line 6 "no EFFECT"
#line 7
foobar#line 17
q{ foobar#line 17
}
#line 20     
/+#LEXERTEST
ID,SPECIAL_TOKEN_LINE,
ID, *, COMMENT_LINE,
COMMENT_LINE,
SPECIAL_TOKEN_LINE,
ID, SPECIAL_TOKEN_LINE,
STRING_TOKENS,EOL,
SPECIAL_TOKEN_LINE,
+/

//#SPLIT_SOURCE_TEST ___________________ 
#li asdf
123
#linexx asdf
foo
#line
-
#line 17 "
+
#line 17 "asfdf
/
#line 17"asfdf
/.
#line "asfdf
*
#line 123 "asfdf"asdf
/+#LEXERTEST
SPECIAL_TOKEN_LINE!, INTEGER, EOL,
SPECIAL_TOKEN_LINE!, ID,EOL,
SPECIAL_TOKEN_LINE!, MINUS,EOL,
SPECIAL_TOKEN_LINE!, PLUS,EOL,
SPECIAL_TOKEN_LINE!, DIV,EOL,
SPECIAL_TOKEN_LINE!, DIV,DOT,EOL,
SPECIAL_TOKEN_LINE!, STAR,EOL,
SPECIAL_TOKEN_LINE!,
+/

//#SPLIT_SOURCE_TEST ___________________ 
#line 20/+#LEXERTEST
SPECIAL_TOKEN_LINE,
+/
//#SPLIT_SOURCE_TEST ___________________ 
#line 20 "asdfds"/+#LEXERTEST
SPECIAL_TOKEN_LINE,
+/
