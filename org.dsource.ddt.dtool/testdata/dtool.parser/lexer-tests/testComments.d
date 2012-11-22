//#SPLIT_SOURCE_TEST _____________________ Single line Comment
// asdf
//
asdf
// /*    
aaaa
// asdf
// asdf
// asdf/+#LEXERTEST
COMMENT_LINE,COMMENT_LINE,ID,EOL,COMMENT_LINE,ID,EOL,
COMMENT_LINE,COMMENT_LINE,COMMENT_LINE
+/

//#SPLIT_SOURCE_TEST _____________________ Multi comment
/* */a/**/a/* /*  */
/* // *//* 
multiline coment /+ 
 */
/+#LEXERTEST
COMMENT_MULTI,*,COMMENT_MULTI,*,COMMENT_MULTI,EOL,
COMMENT_MULTI,COMMENT_MULTI,EOL
+/

//#SPLIT_SOURCE_TEST _____________________
/+ +/a/++/a/+ /+  +/ asdf  +/
/+ // +//+ 
multiline coment /+  /+ 
  +/ //
  /*
+/
*/
 +/
/+#LEXERTEST
COMMENT_NESTED,*,COMMENT_NESTED,*,COMMENT_NESTED,EOL,
COMMENT_NESTED,COMMENT_NESTED,EOL
+/

//#SPLIT_SOURCE_TEST _____________________ the /*/ situation
/*/ */
/+/aa+/
/+#LEXERTEST
COMMENT_MULTI,EOL,COMMENT_NESTED,EOL
+/


//#SPLIT_SOURCE_TEST _____________________ test error cases
/* asdf
/+/aa+/*
/+#LEXERTEST
ERROR
+/
//#SPLIT_SOURCE_TEST _____________________ 
/* asdf
*/aa/+ asf
/+ asdf +/ 
asdf/+#LEXERTEST
COMMENT_MULTI,*,ERROR
+/