//#SPLIT_SOURCE_TEST _____________________ IDENTIFIERS
abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_
Astart
0start123
_start
rStart hStart xStart qStart
/+#LEXERTEST
ID,EOL,
ID,EOL,
INTEGER,ID,EOL,
ID,EOL,
ID ,WS,ID ,WS,ID ,WS,ID ,EOL
+/

//#SPLIT_SOURCE_TEST __________________ unicode chars
aaa日1本2人3龍aaaaaaa��__�__
本xxx本
this_Id_has_supplementary_plane_𐌰𐌱𐌲/**/𐌰𐌱𐌲_this_Id_has_supplementary_plane
/+#LEXERTEST
ID,EOL,
ID,EOL,
ID,COMMENT_MULTI,ID,EOL,
+/

//#SPLIT_SOURCE_TEST ___________________ 
aaa日1本2人3龍aaa
/+#LEXERTEST
ID,ERROR,EOL,
+/
