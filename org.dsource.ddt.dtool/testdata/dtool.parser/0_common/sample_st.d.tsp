Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

// TODO: put statements in this block:
#@ST_BLOCK__SAMPLE《►#?STRUCTURE_EXPECTED!【 { Foo foo; } ● BlockStatement(DefVariable(RefIdentifier DefSymbol))】》
     #@NO_ST_BLOCK《►#?STRUCTURE_EXPECTED!【 /*Required StBlock*/ #error(EXPRULE_Block) ● BlockStatement 】》
 #@BROKEN_ST_BLOCK《►#?STRUCTURE_EXPECTED!【 { Foo foo; #error(EXP_CLOSE_BRACE) #@BREAK● 
    BlockStatement(DefVariable(? ?)) $】》
   
#@ST_BLOCK__TESTER《
 ►#@ST_BLOCK__SAMPLE●
 ►#@NO_ST_BLOCK #@BREAK●
 ►#@BROKEN_ST_BLOCK●
 ►#?STRUCTURE_EXPECTED!【 #error(EXPRULE_Block) #@BREAK body {} ● BlockStatement #@BREAK 】●
¤》
