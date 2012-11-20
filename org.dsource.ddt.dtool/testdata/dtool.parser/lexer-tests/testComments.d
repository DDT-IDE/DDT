//#SPLIT_SOURCE_TEST _____________________ Single line Comment
//
// asdf
// asdf
asdf
// /*    
asdf
/+#LEXERTEST
COMMENT,COMMENT,COMMENT,*,EOL,COMMENT,*,EOL
+/

//#SPLIT_SOURCE_TEST _____________________ Multi comment
/* */a/**/a/* /*  */
/* // *//* 
multiline coment /+ 
 */
/+#LEXERTEST
COMMENT,*,COMMENT,*,COMMENT,EOL
COMMENT,COMMENT,EOL
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
COMMENT,*,COMMENT,*,COMMENT,EOL
COMMENT,COMMENT,EOL
+/

//#SPLIT_SOURCE_TEST _____________________ the /*/ situation
/*/ */
/+/aa+/
/+#LEXERTEST
COMMENT,EOL,COMMENT,EOL
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
COMMENT,*,ERROR
+/