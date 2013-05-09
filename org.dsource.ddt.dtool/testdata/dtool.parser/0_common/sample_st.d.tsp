Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

// TODO: put statements in this block:
#@ST_BLOCK__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【 { #@ST__SAMPLE } ● BlockStatement(#@ST__SAMPLE)】》
#@NO_ST_BLOCK《
  ►#?STRUCTURE_EXPECTED!【 /*Required StBlock*/ #error(EXPRULE_Block) ● BlockStatement 】》
#@BROKEN_ST_BLOCK《
  ►#?STRUCTURE_EXPECTED!【 { Bar barX; #error(EXP_CLOSE_BRACE) #@BREAK● BlockStatement(#@ST__SAMPLE $】》

#@NO_STOrBLOCK《►#?STRUCTURE_EXPECTED!【 /*Required St*/ #error(EXPRULE_StOrBlock) ● BlockStatement 】》
#@NO_ST《 ►#?STRUCTURE_EXPECTED!【 /*Required St*/ #error(EXPRULE_Statement) ● BlockStatement 】》

#@ST__SAMPLE《►#?STRUCTURE_EXPECTED!【 Foo foo; ● DefVariable(RefIdentifier DefSymbol)】》
#@ST__BROKEN《#@BROKEN_ST_BLOCK》


// Note: it's usefull that all these four ST_* variations do not reference each other, even though they could. 
// This is so they can be used in combination with each other and still fully expand cases, 
// which can't be done otherwise due to a TSP limitation.
   
#@ST__ANY《
 ►#@NO_STOrBLOCK●
 ►#@ST_BLOCK__SAMPLE●
 ►#@ST__SAMPLE●
 ►#?STRUCTURE_EXPECTED!【 ; ● EmptyStatement 】●
 ►#@BROKEN_ST_BLOCK●
¤》
#@ST__NB《
 ►#@NO_STOrBLOCK●
 ►#@ST_BLOCK__SAMPLE●
 ►#@ST__SAMPLE●
 ►#?STRUCTURE_EXPECTED!【 ; ● EmptyStatement 】●
¤》
#@ST__NB_NE《
 ►#@ST_BLOCK__SAMPLE●
 ►#@ST__SAMPLE●
 ►#?STRUCTURE_EXPECTED!【 ; ● EmptyStatement 】●
¤》

#@ST_BLOCK《
 ►#@NO_ST_BLOCK●
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
¤》
#@ST_BLOCK__NB《
 ►#@NO_ST_BLOCK●
 ►#@ST_BLOCK__SAMPLE●
¤》
 