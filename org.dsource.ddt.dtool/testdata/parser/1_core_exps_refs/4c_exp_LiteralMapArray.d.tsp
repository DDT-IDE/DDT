▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [1 : "asdf", "2" : foo, 66 : "66" ]
#STRUCTURE_EXPECTED:  ExpLiteralMapArray(MapEntry(Integer String) MapEntry(String *) MapEntry(Integer String))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)    [ #@CLOSEBRACKET_OR_BREAK
#STRUCTURE_EXPECTED:  ExpLiteralArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ 7 : true, #@KEY_EXP《
►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@EXP_ASSIGN●#@EXP_ASSIGN #@EXP_ASSIGN】●
►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@NO_EXP ●#@EXP_ASSIGN #@NO_EXP】●
►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN #error(EXP_COLON)●#@EXP_ASSIGN】●
►#?STRUCTURE_EXPECTED!【/*MISSING_BOTH*/  #@NO_EXP : #@NO_EXP ●#@NO_EXP #@NO_EXP】●
►#?STRUCTURE_EXPECTED!【/*MISSING_ENTRY*/ #@NO_EXP #error(EXP_COLON) ●#@NO_EXP】●
¤》  #@CLOSEBRACKET_OR_BREAK  
#STRUCTURE_EXPECTED:
ExpLiteralMapArray( 
  MapEntry(Integer Bool) 
  MapEntry(#@KEY_EXP) 
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ #@KEY_EXP《
►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@EXP_ASSIGN●#@EXP_ASSIGN #@EXP_ASSIGN】●
►#?STRUCTURE_EXPECTED!【#@EXP_ASSIGN : #@NO_EXP ●#@EXP_ASSIGN #@NO_EXP】●
►#?STRUCTURE_EXPECTED!【/*MISSING_BOTH*/ #@NO_EXP : #@NO_EXP ●#@NO_EXP #@NO_EXP】●
¤》   #@CLOSEBRACKET_OR_BREAK  
#STRUCTURE_EXPECTED:
ExpLiteralMapArray( MapEntry(#@KEY_EXP ) )

