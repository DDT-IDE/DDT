▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [1 : "asdf", "2" : foo, 66 : "66"]
#AST_STRUCTURE_EXPECTED:  ExpLiteralMapArray(MapEntry(Integer String) MapEntry(String *) MapEntry(Integer String))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ 7 : "asdf", "2" : foo #error(EXP_CLOSE_BRACKET) #@BREAK
#AST_STRUCTURE_EXPECTED:  ExpLiteralMapArray(MapEntry(Integer String) MapEntry(String *) )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)        [ #error(EXP_CLOSE_BRACKET) #@BREAK 
#AST_STRUCTURE_EXPECTED:  ExpLiteralArray()
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ 7 : true, #@KEY_EXP《
►#@EXP_ASSIGN : #@EXP_ASSIGN●
►#@EXP_ASSIGN : #error(EXPRULE_exp) ●
►#@EXP_ASSIGN #error(EXP_COLON)●
►/*MISSING_BOTH*/ #error(EXPRULE_exp) : #error(EXPRULE_exp) ●
►/*MISSING_ENTRY*/ #error(EXPRULE_exp) #error(EXP_COLON) ●
¤》  #@BkCLOSE_OR_BREAK  
#AST_STRUCTURE_EXPECTED:
ExpLiteralMapArray( 
  MapEntry(Integer Bool) 
  MapEntry(#@《
    #@EXP_ASSIGN #@EXP_ASSIGN●
    #@EXP_ASSIGN MissingExpression ●
    #@EXP_ASSIGN● 
    MissingExpression MissingExpression ●
    MissingExpression
    》(KEY_EXP) ) 
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
[ #@KEY_EXP《#@EXP_ASSIGN : #@EXP_ASSIGN●#@EXP_ASSIGN : #error(EXPRULE_exp) ●
  /*MISSING_BOTH*/ #error(EXPRULE_exp) : #error(EXPRULE_exp) 
》   #@BkCLOSE_OR_BREAK  
#AST_STRUCTURE_EXPECTED:
ExpLiteralMapArray( 
  MapEntry(#@《#@EXP_ASSIGN #@EXP_ASSIGN●#@EXP_ASSIGN MissingExpression ● 
    MissingExpression MissingExpression 》(KEY_EXP) ) 
)

