Ⓗ▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#@ST_BLOCK《
 ►#@ST_BLOCK__SAMPLE●
 ►#@BROKEN_ST_BLOCK●
 ►#?STRUCTURE_EXPECTED!【 /*No StBlock*/ ● 】●
 ►#?STRUCTURE_EXPECTED!【 ; ● EmptyStatement】●
¤》

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)    #@ST_BLOCK
#STRUCTURE_EXPECTED: #@ST_BLOCK
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     { #error(SE_Statement) module ; }
#STRUCTURE_EXPECTED:  BlockStatement( InvalidSyntaxElement EmptyStatement )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(STATEMENT)     { #error(SE_Statement) ]      Foo foo; }
#STRUCTURE_EXPECTED:  BlockStatement( InvalidSyntaxElement DefVariable(RefIdentifier DefSymbol) )
