Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ 

 #@ST__SAMPLE《►#?STRUCTURE_EXPECTED!【 for(;true; i++) { } ● StatementFor(EmptyStatement ? * BlockStatement)】》
#@ST__SAMPLE2《►#?STRUCTURE_EXPECTED!【 Foo foo; ● DefVariable(RefIdentifier ?) 】》
#@ST__BROKEN《#@BROKEN_ST_BLOCK》


#@BLOCKSTATEMENT《#?BLOCK_ST_UNSCOPED【BlockStatementUnscoped●BlockStatement】》

#@ST_BLOCK__SAMPLE《
  ►#?STRUCTURE_EXPECTED!【 { #@ST__SAMPLE2 #@ST__SAMPLE  } ● #@BLOCKSTATEMENT (#@ST__SAMPLE2 #@ST__SAMPLE)】》
#@NO_ST_BLOCK《
  ►#?STRUCTURE_EXPECTED!【 /*Required StBlock*/ #error(EXPRULE_Block) ● #@BLOCKSTATEMENT 】》
#@BROKEN_ST_BLOCK《
  ►#?STRUCTURE_EXPECTED!【 { #@ST__SAMPLE #error(EXP_CLOSE_BRACE) #@BREAK● #@BLOCKSTATEMENT (#@ST__SAMPLE $】》

#@NO_STOrBLOCK《►#?STRUCTURE_EXPECTED!【 /*Required St*/ #error(EXPRULE_StOrBlock) ● #@BLOCKSTATEMENT 】》
#@NO_ST《 ►#?STRUCTURE_EXPECTED!【 /*Required St*/ #error(EXPRULE_Statement) ● #@BLOCKSTATEMENT 】》


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
#@ST__NE《
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

#@BLOCK_ST《
 ►#@NO_ST_BLOCK●
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
¤》
#@BLOCK_ST__NB《
 ►#@NO_ST_BLOCK●
 ►#@ST_BLOCK__SAMPLE●
¤》
 